package hung.megamarketv2.ecommerce.modules.security.configs;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.encrypt.BytesEncryptor;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.authorization.JdbcOAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AccessTokenAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2TokenRevocationAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.client.JdbcRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;
import org.springframework.security.oauth2.server.authorization.web.authentication.OAuth2AccessTokenResponseAuthenticationSuccessHandler;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.mvc.support.DefaultHandlerExceptionResolver;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;

import hung.megamarketv2.common.generic.results.Result;
import hung.megamarketv2.common.generic.constants.SecurityConstants;
import hung.megamarketv2.ecommerce.modules.security.ErrorCodes.RSAKeyServiceErrorCodes;
import hung.megamarketv2.ecommerce.modules.security.Exceptions.UnableToGetRSAKeyException;
import hung.megamarketv2.ecommerce.modules.security.services.RSAKeyService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	public static final String BYTES_ENCRYPTOR = "bytesEncryptor";

	public static final String REGISTERED_CLIENT_REPOSITORY = "registeredClientRepository";

	public static final String OAUTH2_AUTHORIZATION_SERVICE = "oauth2AuthorizationService";

	public static final String JWT_DECODER = "jwtDecoder";

	@Value("${keyset-uri}")
	private String keySetUri;

	@Value("${introspection-uri}")
	private String introspectionUri;

	@Value("${encryption-salt}")
	private String encryptionSalt;

	@Value("${encryption-password}")
	private String encryptionPassword;

	@Value("${resource-server.client-id}")
	private String resourceServerClientId;

	@Value("${ecommerce-front-end-base_uri}")
	private String frontEndBaseUri;

	private final RedisTemplate<String, Object> redisTemplate;

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	@Order(1)
	@DependsOn({ OAUTH2_AUTHORIZATION_SERVICE, JWT_DECODER })
	public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http,
			OAuth2AuthorizationService auth2AuthorizationService, JwtDecoder jwtDecoder)
			throws Exception {

		OAuth2AuthorizationServerConfiguration
				.applyDefaultSecurity(http);

		http.getConfigurer(
				OAuth2AuthorizationServerConfigurer.class).tokenEndpoint(
						tokenEndpoint -> tokenEndpoint
								.accessTokenResponseHandler(
										new CustomTokenResponseHandler(redisTemplate, jwtDecoder)))
				.tokenRevocationEndpoint(
						tokenRevocationEndpoint -> tokenRevocationEndpoint.revocationResponseHandler(
								new CustomRevocationResponseHandler(redisTemplate,
										auth2AuthorizationService)))
				.oidc(Customizer.withDefaults());

		http.cors(c -> {
			CorsConfigurationSource source = _ -> {
				CorsConfiguration config = new CorsConfiguration();
				config.setAllowedOrigins(
						List.of(frontEndBaseUri));
				config.setAllowedMethods(
						List.of("GET", "POST", "PUT", "DELETE"));
				config.setAllowedHeaders(List.of("*"));
				return config;
			};
			c.configurationSource(source);
		});

		http.exceptionHandling(e -> e.authenticationEntryPoint(
				new LoginUrlAuthenticationEntryPoint("/login")));
		return http.build();
	}

	@Bean
	public HandlerExceptionResolver exceptionResolver() {
		// TODO: add custom exception resolver
		return new DefaultHandlerExceptionResolver();
	}

	@Bean
	@Order(2)
	@DependsOn(JWT_DECODER)
	SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http, JwtDecoder jwtDecoder,
			HandlerExceptionResolver exceptionResolver)
			throws Exception {

		http.formLogin(Customizer.withDefaults());

		http.authorizeHttpRequests(
				c -> c.requestMatchers("/user/registration/**").permitAll()
						.anyRequest().authenticated());

		http.csrf(csrf -> csrf.disable());

		http.oauth2ResourceServer(
				c -> c.jwt(
						j -> j.jwkSetUri(keySetUri)))
				.authenticationProvider(new PreBearerTokenAuthenticationProvider(redisTemplate, jwtDecoder)).cors(
						c -> {
							CorsConfigurationSource source = _ -> {
								CorsConfiguration config = new CorsConfiguration();
								config.setAllowedOrigins(
										List.of(frontEndBaseUri));
								config.setAllowedMethods(
										List.of("GET", "POST", "PUT", "DELETE"));
								config.setAllowedHeaders(List.of("*"));
								return config;
							};
							c.configurationSource(source);
						});

		return http.build();
	}

	@Bean
	public OAuth2TokenCustomizer<JwtEncodingContext> oAuth2TokenCustomizer() {

		return context -> {
			String tokenType = context.getTokenType().getValue();
			// Manually adding the user's roles to the access token.
			if (tokenType.equals(OAuth2TokenType.ACCESS_TOKEN.getValue())) {
				/**
				 * If the token request comes from the resource server, we extracts custom
				 * authorities and custom sub from
				 * the HTTP request parameters. Otherwise, we retrieves authorities from the
				 * principal's granted authorities.
				 * 
				 * The method then adds the authorities and sub (if available) to the
				 * token's claims.
				 */
				String[] stringAuthorities = null;
				String sub = null;
				String principalName = context.getPrincipal().getName();
				boolean isTokenRequestedByResourceServer = principalName.equals(resourceServerClientId);

				if (isTokenRequestedByResourceServer) {
					HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder
							.currentRequestAttributes()).getRequest();

					stringAuthorities = request.getParameterValues(SecurityConstants.AUTHORITIES);
					sub = request.getParameter(SecurityConstants.SUB);
				}

				if (stringAuthorities == null) {
					Collection<? extends GrantedAuthority> authorities = context.getPrincipal().getAuthorities();
					stringAuthorities = authorities.stream().map(GrantedAuthority::getAuthority)
							.toArray(String[]::new);
				}

				context.getClaims().claim(SecurityConstants.AUTHORITIES, stringAuthorities);

				if (sub != null) {
					context.getClaims().claim(SecurityConstants.SUB, sub);
				}

			}

		};
	}

	@Bean
	public AuthorizationServerSettings authorizationServerSettings() {
		return AuthorizationServerSettings.builder().build();
	}

	@Bean(name = OAUTH2_AUTHORIZATION_SERVICE)
	@DependsOn(REGISTERED_CLIENT_REPOSITORY)
	public OAuth2AuthorizationService auth2AuthorizationService(JdbcOperations jdbcOperations,
			RegisteredClientRepository registeredClientRepository) {

		return new JdbcOAuth2AuthorizationService(
				jdbcOperations, registeredClientRepository);
	}

	@Bean(name = REGISTERED_CLIENT_REPOSITORY)
	public RegisteredClientRepository registeredClientRepository(JdbcTemplate jdbcTemplate) {
		return new JdbcRegisteredClientRepository(jdbcTemplate);
	}

	@Bean(name = BYTES_ENCRYPTOR)
	public BytesEncryptor bytesEncryptor() {
		return Encryptors.standard(encryptionPassword, encryptionSalt);
	}

	@Bean
	@DependsOn(BYTES_ENCRYPTOR)
	public JWKSource<SecurityContext> jwkSource(RSAKeyService rsaKeyService) {
		RSAKey rsaKey;

		Result<RSAKey, RSAKeyServiceErrorCodes> result = rsaKeyService.getLatestKey();

		if (result.isSuccessful) {
			rsaKey = result.value;
		} else {
			Result<RSAKey, RSAKeyServiceErrorCodes> creationResult = rsaKeyService.createNewKey();

			if (!creationResult.isSuccessful) {
				throw new UnableToGetRSAKeyException("Could not create new key");
			}

			rsaKey = creationResult.value;
		}

		JWKSet jwkSet = new JWKSet(rsaKey);

		return new ImmutableJWKSet<>(jwkSet);
	}

	@Bean
	public JwtAuthenticationConverter jwtAuthenticationConverter() {
		JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();

		// Preventing Spring Security from automatically prefixing authorities
		grantedAuthoritiesConverter.setAuthorityPrefix("");

		grantedAuthoritiesConverter.setAuthoritiesClaimName(SecurityConstants.AUTHORITIES);

		JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
		jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);

		return jwtAuthenticationConverter;
	}
}

@RequiredArgsConstructor
class CustomTokenResponseHandler implements AuthenticationSuccessHandler {
	private final RedisTemplate<String, Object> redisTemplate;
	private final JwtDecoder jwtDecoder;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {

		OAuth2AccessTokenAuthenticationToken accessTokenAuthentication = (OAuth2AccessTokenAuthenticationToken) authentication;

		OAuth2AccessToken accessToken = accessTokenAuthentication.getAccessToken();
		String accessTokenValue = accessToken.getTokenValue();
		Jwt jwt = jwtDecoder.decode(accessTokenValue);
		String jti = jwt.getId();
		String accessTokenKey = SecurityConstants.ACCESS_TOKEN_PREFIX + jti;

		Duration accessTokenTimeToLive = Instant.now().until(accessToken.getExpiresAt());
		OAuth2RefreshToken refreshToken = accessTokenAuthentication.getRefreshToken();

		redisTemplate.executePipelined(new RedisCallback<Object>() {
			public Object doInRedis(RedisConnection connection) throws DataAccessException {
				String refreshTokenValue = "";

				if (refreshToken != null) {
					refreshTokenValue = handleRefreshTokenInRedis(refreshToken, accessTokenKey);
				}

				redisTemplate.opsForValue().set(accessTokenKey, refreshTokenValue, accessTokenTimeToLive);

				return null;
			}
		});

		OAuth2AccessTokenResponseAuthenticationSuccessHandler successHandler = new OAuth2AccessTokenResponseAuthenticationSuccessHandler();
		successHandler.onAuthenticationSuccess(request, response, accessTokenAuthentication);
	}

	private String handleRefreshTokenInRedis(OAuth2RefreshToken refreshToken, String accessTokenKey) {
		String refreshTokenValue = refreshToken.getTokenValue();
		Object oldAccessToken = redisTemplate.opsForValue().get(refreshTokenValue);

		if (oldAccessToken != null) {
			redisTemplate.delete(oldAccessToken.toString());
		}

		Duration refreshTokenTimeToLive = Instant.now()
				.until(refreshToken.getExpiresAt());

		redisTemplate.opsForValue().set(refreshTokenValue, accessTokenKey,
				refreshTokenTimeToLive);

		return refreshTokenValue;
	}
}

@RequiredArgsConstructor
class CustomRevocationResponseHandler implements AuthenticationSuccessHandler {
	private final RedisTemplate<String, Object> redisTemplate;
	private final OAuth2AuthorizationService auth2AuthorizationService;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {

		OAuth2TokenRevocationAuthenticationToken revocationAuthentication = (OAuth2TokenRevocationAuthenticationToken) authentication;
		String revokedToken = revocationAuthentication.getToken();
		OAuth2Authorization authorization = auth2AuthorizationService.findByToken(revokedToken, null);
		String accessTokenValue = authorization.getAccessToken().getToken().getTokenValue();
		String accessTokenKey = SecurityConstants.ACCESS_TOKEN_PREFIX + accessTokenValue;

		redisTemplate.delete(accessTokenKey);

	}

}

@RequiredArgsConstructor
class PreBearerTokenAuthenticationProvider implements AuthenticationProvider {
	private final RedisTemplate<String, Object> redisTemplate;
	private final JwtDecoder jwtDecoder;

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		var authenticationToken = (BearerTokenAuthenticationToken) authentication;
		String accessToken = authenticationToken.getToken();
		Jwt jwt = jwtDecoder.decode(accessToken);
		String jti = jwt.getId();
		String accessTokenKey = SecurityConstants.ACCESS_TOKEN_PREFIX + jti;

		Boolean hasKey = redisTemplate.hasKey(accessTokenKey);

		if (!hasKey) {
			throw new RuntimeException(new InvalidBearerTokenException("Access token not found in Redis"));
		}

		return null;
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return BearerTokenAuthenticationToken.class.isAssignableFrom(authentication);
	}
}
