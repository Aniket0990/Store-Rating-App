package com.example.store_rating_app.security;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.store_rating_app.repository.UserRepository;
import com.example.store_rating_app.service.JwtService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getServletPath();
        System.out.println("[JWT FILTER] Request Path: " + path);

        // Skip /auth/** endpoints
        if (path.startsWith("/api/auth")) {
            System.out.println("[JWT FILTER] Auth endpoint, skipping JWT check.");
            filterChain.doFilter(request, response);
            return;
        }

        final String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            System.out.println("[JWT FILTER] No Authorization header or invalid format.");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized: Missing or invalid token");
            return;
        }

        final String token = authHeader.substring(7);
        System.out.println("[JWT FILTER] Extracted Token: " + token);

        String email;
        try {
            email = jwtService.extractUsername(token);
            System.out.println("[JWT FILTER] Email from token: " + email);
        } catch (Exception ex) {
            System.out.println("[JWT FILTER] Failed to extract email from token: " + ex.getMessage());
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized: Invalid token");
            return;
        }

        if (email == null) {
            System.out.println("[JWT FILTER] Email is null, cannot authenticate.");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized: Token missing email");
            return;
        }

        var appUserOpt = userRepository.findByEmail(email);
        if (appUserOpt.isEmpty()) {
            System.out.println("[JWT FILTER] User not found for email: " + email);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized: User not found");
            return;
        }

        var appUser = appUserOpt.get();

        // Validate token
        boolean isValid = jwtService.validateToken(token, appUser);
        System.out.println("[JWT FILTER] Token valid? " + isValid);

        if (!isValid) {
            System.out.println("[JWT FILTER] Token validation failed.");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized: Token invalid or expired");
            return;
        }

        // Extract authorities
        Object rawAuthorities = jwtService.extractClaim(token, claims -> claims.get("authorities"));
        List<SimpleGrantedAuthority> authoritiesList = List.of();

        if (rawAuthorities instanceof String s) {
            authoritiesList = List.of(new SimpleGrantedAuthority(s));
        } else if (rawAuthorities instanceof List<?> list) {
            authoritiesList = list.stream()
                    .map(Object::toString)
                    .map(SimpleGrantedAuthority::new)
                    .toList();
        }

        if (authoritiesList.isEmpty()) {
            System.out.println("[JWT FILTER] No authorities found in token!");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized: Role missing");
            return;
        }

        // Set authentication in SecurityContext
        var authToken = new UsernamePasswordAuthenticationToken(appUser, null, authoritiesList);
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authToken);

        System.out.println("[JWT FILTER] Authentication set in SecurityContext with authorities: " + authoritiesList);

        filterChain.doFilter(request, response);
    }
}
