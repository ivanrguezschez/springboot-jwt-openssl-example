package com.irs.springbootjwtopensslexample.business.services.impl;

import com.irs.springbootjwtopensslexample.business.services.AuthService;
import com.irs.springbootjwtopensslexample.business.services.JwtService;
import com.irs.springbootjwtopensslexample.presentation.dtos.LoginRequestDTO;
import com.irs.springbootjwtopensslexample.presentation.dtos.LoginResponseDTO;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
//@AllArgsConstructor
public class AuthServiceImpl implements AuthService {
          
    private final AuthenticationManager authenticationManager;
    
    //@Qualifier("jsonwebtokenJwtService")
    //@Qualifier("joseJwtService")
    private final JwtService jwtService; 

    public AuthServiceImpl(AuthenticationManager authenticationManager,
            @Qualifier("joseJwtService") JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }
      
    @Override
    public LoginResponseDTO login(LoginRequestDTO loginRequestDTO) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequestDTO.getUsername(),
                        loginRequestDTO.getPassword()));
        
        String jwt = jwtService.generate(authentication);
        
        return LoginResponseDTO.builder().token(jwt).build();
    }
}
