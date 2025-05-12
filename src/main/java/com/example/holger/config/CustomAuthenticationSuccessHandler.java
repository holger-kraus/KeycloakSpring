package com.example.holger.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private static final Logger logger = LoggerFactory.getLogger(CustomAuthenticationSuccessHandler.class);

    private final OAuth2AuthorizedClientService authorizedClientService;

    public CustomAuthenticationSuccessHandler(OAuth2AuthorizedClientService authorizedClientService) {
        this.authorizedClientService = authorizedClientService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        if (authentication instanceof OAuth2AuthenticationToken authToken) {
            OAuth2AuthorizedClient client = authorizedClientService.loadAuthorizedClient(
                    authToken.getAuthorizedClientRegistrationId(),
                    authToken.getName()
            );

            String accessToken = client.getAccessToken().getTokenValue();
            long expiresIn = client.getAccessToken().getExpiresAt().getEpochSecond() - Instant.now().getEpochSecond();

            Cookie cookie = new Cookie("ACCESS_TOKEN", accessToken);
            cookie.setHttpOnly(true);
            cookie.setSecure(false);
            cookie.setPath("/");
            cookie.setMaxAge((int) expiresIn);

            logger.debug("Setting ACCESS_TOKEN cookie with {} seconds lifetime", expiresIn);
            response.addCookie(cookie);

            logger.debug("Redirecting user to /");
            response.sendRedirect("/");
        }
    }
}
