package com.example.demo.security;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    public static final String INVALID_TOKEN_ATTRIBUTE = "invalidJwtToken";
    private static final Pattern BEARER_HEADER = Pattern.compile("^Bearer\\s+(.+)$", Pattern.CASE_INSENSITIVE);

    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        String authorization = request.getHeader("Authorization");
        if (authorization != null && !authorization.isBlank()) {
            Matcher matcher = BEARER_HEADER.matcher(authorization.trim());
            if (matcher.matches()) {
                authenticate(request, matcher.group(1).trim());
            } else {
                request.setAttribute(INVALID_TOKEN_ATTRIBUTE, true);
            }
        }
        filterChain.doFilter(request, response);
    }

    private void authenticate(HttpServletRequest request, String token) {
        try {
            if (token.isBlank()) {
                request.setAttribute(INVALID_TOKEN_ATTRIBUTE, true);
                return;
            }
            Claims claims = jwtService.parseToken(token);
            String userId = claims.getSubject();
            String role = claims.get("role", String.class);
            if (userId != null && !userId.isBlank() && role != null && !role.isBlank()
                    && SecurityContextHolder.getContext().getAuthentication() == null) {
                var authorities = List.of(new SimpleGrantedAuthority("ROLE_" + role.trim().toUpperCase(Locale.ROOT)));
                var authentication = new UsernamePasswordAuthenticationToken(userId, null, authorities);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else {
                request.setAttribute(INVALID_TOKEN_ATTRIBUTE, true);
            }
        } catch (RuntimeException exception) {
            request.setAttribute(INVALID_TOKEN_ATTRIBUTE, true);
        }
    }
}
