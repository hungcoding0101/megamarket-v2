package hung.megamarketv2.ecommerce.modules.security.configs;

import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.encrypt.BytesEncryptor;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;

import hung.megamarketv2.common.generic.results.Result;
import hung.megamarketv2.ecommerce.modules.security.ErrorCodes.RSAKeyServiceErrorCodes;
import hung.megamarketv2.ecommerce.modules.security.Exceptions.UnableToGetRSAKeyException;
import hung.megamarketv2.ecommerce.modules.security.services.RSAKeyService;
import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	public static final String AUTHORITIES = "authorities";

	public static final String BYTES_ENCRYPTOR = "bytesEncryptor";

	@Value("${auth-client-id}")
	private String clientID;

	@Value("${auth-client-secret}")
	private String clientSecret;

	@Value("${keyset-uri}")
	private String keySetUri;

	@Value("${access-token-lifetime-in-seconds}")
	private int accessTokenLifeTimeInSeconds;

	@Value("${refresh-token-lifetime-in-seconds}")
	private int refreshTokenLifeTimeInSeconds;

	@Value("${encryption-salt}")
	private String encryptionSalt;

	@Value("${encryption-password}")
	private String encryptionPassword;

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	@Order(1)
	public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http)
			throws Exception {

		OAuth2AuthorizationServerConfiguration
				.applyDefaultSecurity(http);

		http.getConfigurer(
				OAuth2AuthorizationServerConfigurer.class)
				.oidc(Customizer.withDefaults());

		http.exceptionHandling(e -> e.authenticationEntryPoint(
				new LoginUrlAuthenticationEntryPoint("/login")));

		return http.build();
	}

	@Bean
	@Order(2)
	SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http)
			throws Exception {

		http.formLogin(Customizer.withDefaults());

		http.authorizeHttpRequests(
				c -> c.requestMatchers("/user/registration/**").permitAll()
						.anyRequest().authenticated());

		http.csrf(csrf -> csrf.disable());

		http.oauth2ResourceServer(
				c -> c.jwt(
						j -> j.jwkSetUri(keySetUri)));

		return http.build();
	}

	@Bean
	public OAuth2TokenCustomizer<JwtEncodingContext> oAuth2TokenCustomizer() {

		return context -> {
			String tokenType = context.getTokenType().getValue();

			// Manually adding the user's roles to the access token.
			if (tokenType.equals(OAuth2TokenType.ACCESS_TOKEN.getValue())) {

				Collection<? extends GrantedAuthority> authorities = context.getPrincipal().getAuthorities();

				List<String> stringAuthorities = authorities.stream().map(GrantedAuthority::getAuthority).toList();

				context.getClaims().claim(AUTHORITIES, stringAuthorities);
			}

		};
	}

	@Bean
	public AuthorizationServerSettings authorizationServerSettings() {
		return AuthorizationServerSettings.builder().build();
	}

	@Bean
	public RegisteredClientRepository registeredClientRepository(PasswordEncoder passwordEncoder) {
		RegisteredClient registeredClient = RegisteredClient
				.withId(UUID.randomUUID().toString())
				.clientId(clientID)
				.clientSecret(passwordEncoder.encode(clientSecret))
				.clientAuthenticationMethod(
						ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
				.authorizationGrantType(
						AuthorizationGrantType.AUTHORIZATION_CODE)
				.authorizationGrantType(
						AuthorizationGrantType.CLIENT_CREDENTIALS)
				.authorizationGrantType(
						AuthorizationGrantType.REFRESH_TOKEN)
				.redirectUri("https://www.java.com/en/")
				.scope(OidcScopes.OPENID).tokenSettings(TokenSettings.builder()
						.accessTokenTimeToLive(Duration.ofSeconds(accessTokenLifeTimeInSeconds))
						.refreshTokenTimeToLive(Duration.ofSeconds(refreshTokenLifeTimeInSeconds))
						.reuseRefreshTokens(true)
						.build())
				.build();

		return new InMemoryRegisteredClientRepository(registeredClient);
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

		grantedAuthoritiesConverter.setAuthoritiesClaimName(AUTHORITIES);

		JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
		jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);

		return jwtAuthenticationConverter;

	}

}
