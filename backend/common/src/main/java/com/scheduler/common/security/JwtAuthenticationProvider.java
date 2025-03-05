package com.scheduler.common.security;

import com.scheduler.common.util.JwtTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

/**
 * JWT认证提供者
 */
@Slf4j
@Component
public class JwtAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String token = (String) authentication.getCredentials();
        String username = jwtTokenUtil.getUsernameFromToken(token);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        
        if (jwtTokenUtil.validateToken(token, userDetails)) {
            return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        }
        return null;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
 