package com.ivanrl.yaet;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;

@Component
@Profile("LOCAL") // TODO Test again when adding profiles
@RequiredArgsConstructor
public class LocalAuthFilter extends OncePerRequestFilter {

    private final ApplicationContext context;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        var principal = new DefaultOAuth2User(null, Map.of("name", "Local User"), "name");
        var auth = new OAuth2AuthenticationToken(principal, null, "google");
        SecurityContextHolder.getContext().setAuthentication(auth);

        var bean = context.getAutowireCapableBeanFactory().getBean(UserData.class);
        bean.setName("Local User");
        bean.setEmail("fake@email.com");

        filterChain.doFilter(request, response);
    }
}
