package io.aurasage.document.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
public class SecurityConfig {

    @Value("${aurasage.security.authentication.anonymous}")
    private boolean allowAnonymous;

    @Value("${aurasage.security.authentication.org-role}")
    private String orgRole;

    private final String rolePrefix = "ROLE_";

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        http
            .csrf(ServerHttpSecurity.CsrfSpec::disable)
            .authorizeExchange(exchange -> exchange
                // Allow login (forwarded to identity-service)
                // .pathMatchers(identityPrefix + "/auth/login").permitAll()
                // // Allow JWKS discovery
                // .pathMatchers("/.well-known/jwks.json").permitAll()
                // // Secure everything else
                // .anyExchange().authenticated()
                .anyExchange().permitAll()
            )
            .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
            .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable);

            if (allowAnonymous) {
                http.anonymous(anonymous -> anonymous
                    .key("aurasage-anonymous-key")
                    .principal("anonymous")
                    .authorities("ROLE_ANONYMOUS", rolePrefix + orgRole)
                );
            } else {
                http.anonymous(anonymous -> anonymous.disable());
            }

            // .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));

        return http.build();
    }

}
