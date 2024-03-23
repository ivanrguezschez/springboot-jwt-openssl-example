package com.irs.springbootjwtopensslexample.business.services;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

public interface JwtService {

    String generate(Authentication authentication);

    String extractUsername(String token);
    
    boolean validate(String token, UserDetails userDetails);
}
