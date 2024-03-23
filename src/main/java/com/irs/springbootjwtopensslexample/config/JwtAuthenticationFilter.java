package com.irs.springbootjwtopensslexample.config;

import com.irs.springbootjwtopensslexample.business.services.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    //@Qualifier("jsonwebtokenJwtService")
    @Qualifier("joseJwtService")
    private final JwtService jwtService;
    
    private final UserDetailsService userDetailsService;
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        
        log.debug("Authorization Header: {}", authorizationHeader);
                    
        if (authorizationHeader == null || !authorizationHeader.startsWith(JwtConst.BEARER_TOKEN_PREFIX)) {
            log.debug("Authorization Header is null or no start with 'Bearer '");
            filterChain.doFilter(request, response);
            return;
        }
        
        final String jwt = authorizationHeader.substring(JwtConst.BEARER_TOKEN_PREFIX.length());

        final String username = jwtService.extractUsername(jwt);
        
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            boolean tokenValid = jwtService.validate(jwt, userDetails);
            
            log.debug("User find: {}", userDetails);
            log.debug("Token valid: {}", tokenValid);
                        
            if (tokenValid) {
                UsernamePasswordAuthenticationToken authentication = 
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        
        filterChain.doFilter(request, response);
    }
}
