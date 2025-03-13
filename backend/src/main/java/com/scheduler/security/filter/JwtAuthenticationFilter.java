package com.scheduler.security.filter;

import com.scheduler.config.JwtConfig;
import io.jsonwebtoken.Claims;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Collections;

@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtConfig jwtConfig;

    public JwtAuthenticationFilter(JwtConfig jwtConfig) {
        this.jwtConfig = jwtConfig;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) {
        try {
            String token = extractToken(request);
            log.debug("Processing request: {} {}", request.getMethod(), request.getRequestURI());
            log.debug("Token: {}", token != null ? "present" : "null");

            if (token != null) {
                try {
                    Claims claims = jwtConfig.parseToken(token);
                    String username = claims.getSubject();
                    List<String> roles = claims.get("roles", List.class);
                    log.debug("Token validated for user: {}, roles: {}", username, roles);

                    if (roles == null || roles.isEmpty()) {
                        roles = Collections.singletonList("USER");
                    }

                    List<SimpleGrantedAuthority> authorities = roles.stream()
                        .map(role -> new SimpleGrantedAuthority("ROLE_" + role.toUpperCase().replace("ROLE_", "")))
                        .collect(Collectors.toList());

                    UsernamePasswordAuthenticationToken authentication = 
                        new UsernamePasswordAuthenticationToken(username, null, authorities);
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    log.debug("Authentication set for user: {}", username);
                } catch (Exception e) {
                    log.error("Invalid JWT token: {}", e.getMessage());
                    SecurityContextHolder.clearContext();
                }
            } else {
                log.debug("No JWT token found in request");
            }

            chain.doFilter(request, response);
        } catch (Exception e) {
            log.error("Could not set user authentication", e);
            SecurityContextHolder.clearContext();
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/api/auth/") || 
               path.startsWith("/api/public/") ||
               request.getMethod().equals("OPTIONS");
    }

    private String extractToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }
}
