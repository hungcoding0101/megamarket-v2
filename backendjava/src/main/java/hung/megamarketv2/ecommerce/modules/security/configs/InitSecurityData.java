package hung.megamarketv2.ecommerce.modules.security.configs;

import java.time.Duration;
import java.util.Objects;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;

@Configuration
public class InitSecurityData {

        @Value("${auth-client-id}")
        private String clientID;

        @Value("${auth-client-secret}")
        private String clientSecret;

        @Value("${resource-server.client-id}")
        private String resourceServerClientId;

        @Value("${resource-server.client-secret}")
        private String resourceServerClientSecret;

        @Value("${access-token-lifetime-in-seconds}")
        private int accessTokenLifeTimeInSeconds;

        @Value("${resource-server.access-token-lifetime-in-seconds}")
        private int resourceServerAccessTokenLifeTimeInSeconds;

        @Value("${refresh-token-lifetime-in-seconds}")
        private int refreshTokenLifeTimeInSeconds;

        @Bean
        public ApplicationRunner applicationRunner(RegisteredClientRepository registeredClientRepository,
                        PasswordEncoder passwordEncoder) {
                return _ -> {
                        if (Objects.isNull(registeredClientRepository.findByClientId(clientID))) {
                                RegisteredClient registeredClient = RegisteredClient
                                                .withId(UUID.randomUUID().toString())
                                                .clientId(clientID)
                                                .clientSecret(passwordEncoder.encode(clientSecret))
                                                .clientAuthenticationMethod(
                                                                ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                                                .authorizationGrantType(
                                                                AuthorizationGrantType.AUTHORIZATION_CODE)
                                                .authorizationGrantType(
                                                                AuthorizationGrantType.REFRESH_TOKEN)
                                                .redirectUri("http://localhost:5173/")
                                                .scope(OidcScopes.OPENID).tokenSettings(TokenSettings.builder()
                                                                .accessTokenFormat(OAuth2TokenFormat.SELF_CONTAINED)
                                                                .accessTokenTimeToLive(Duration.ofSeconds(
                                                                                accessTokenLifeTimeInSeconds))
                                                                .refreshTokenTimeToLive(Duration.ofSeconds(
                                                                                refreshTokenLifeTimeInSeconds))
                                                                .reuseRefreshTokens(true)
                                                                .build())
                                                .build();

                                registeredClientRepository.save(registeredClient);
                        }

                        if (Objects.isNull(registeredClientRepository.findByClientId(resourceServerClientId))) {
                                RegisteredClient registeredResourceServerClient = RegisteredClient
                                                .withId(UUID.randomUUID().toString())
                                                .clientId(resourceServerClientId)
                                                .clientSecret(passwordEncoder.encode(resourceServerClientSecret))
                                                .clientAuthenticationMethod(
                                                                ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                                                .authorizationGrantType(
                                                                AuthorizationGrantType.CLIENT_CREDENTIALS)
                                                .scope(OidcScopes.OPENID).tokenSettings(TokenSettings.builder()
                                                                .accessTokenFormat(OAuth2TokenFormat.SELF_CONTAINED)
                                                                .accessTokenTimeToLive(Duration.ofSeconds(
                                                                                resourceServerAccessTokenLifeTimeInSeconds))
                                                                .build())
                                                .build();

                                registeredClientRepository.save(registeredResourceServerClient);
                        }
                };
        }
}
