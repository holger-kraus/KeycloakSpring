package com.example.holger.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority;
import org.springframework.security.oauth2.core.user.OAuth2UserAuthority;
import org.springframework.security.web.SecurityFilterChain;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/", "/login**", "/webjars/**", "/error**").permitAll()
                .anyRequest().authenticated()
            )
            .oauth2Login(oauth2 -> oauth2
                .userInfoEndpoint(userInfo -> userInfo
                    .userAuthoritiesMapper(this.userAuthoritiesMapper())
                )
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/")
                .permitAll()
            );
        
        return http.build();
    }
    
    private GrantedAuthoritiesMapper userAuthoritiesMapper() {
        return (authorities) -> {
            Set<GrantedAuthority> mappedAuthorities = new HashSet<>();
            
            authorities.forEach(authority -> {
                if (authority instanceof OidcUserAuthority oidcUserAuthority) {
                    mappedAuthorities.addAll(extractKeycloakAuthorities(oidcUserAuthority.getIdToken().getClaims()));
                } else if (authority instanceof OAuth2UserAuthority oauth2UserAuthority) {
                    mappedAuthorities.addAll(extractKeycloakAuthorities(oauth2UserAuthority.getAttributes()));
                }
            });
            
            return mappedAuthorities;
        };
    }
    
    @SuppressWarnings("unchecked")
    private Set<GrantedAuthority> extractKeycloakAuthorities(Map<String, Object> claims) {
        Set<GrantedAuthority> authorities = new HashSet<>();
        
        // Add logic to extract roles from Keycloak claims
        // This example assumes you have realm_access/roles in your token
        if (claims.containsKey("realm_access")) {
            Map<String, Object> realmAccess = (Map<String, Object>) claims.get("realm_access");
            if (realmAccess.containsKey("roles")) {
                ((List<String>) realmAccess.get("roles")).forEach(role -> {
                    authorities.add(new SimpleGrantedAuthority("ROLE_" + role));
                });
            }
        }
        
        return authorities;
    }
}