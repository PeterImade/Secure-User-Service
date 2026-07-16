package com.example.user_service.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JWTService jwtService;
    private final UserDetailsService userDetailsService;

    // Constructor Injection
    public JwtAuthenticationFilter(JWTService jwtService, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {

        // 1. Extract the Authorization header
        final String authHeader = request.getHeader("Authorization");
        String email = null;
        String jwt = null;

        // 2. Check if the header is valid (starts with Bearer )
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwt = authHeader.substring(7); // Remove "Bearer " prefix
            email = jwtService.extractEmail(jwt); // Extract the email from the token
        }

        // 3. If we got an email and the user is not already authenticated
        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // 4. Load the user details from the database (or cache)
            UserDetails userDetails = userDetailsService.loadUserByUsername(email);

            // 5. Validate the token against the loaded user
            if (jwtService.validateToken(jwt, userDetails.getUsername())) {
                // 6. Create an authentication token and set it in the Security Context
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        // 7. Continue the filter chain (let the request reach the controller)
        chain.doFilter(request, response);
    }
}