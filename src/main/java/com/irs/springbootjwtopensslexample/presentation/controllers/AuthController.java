package com.irs.springbootjwtopensslexample.presentation.controllers;

import com.irs.springbootjwtopensslexample.business.services.AuthService;
import com.irs.springbootjwtopensslexample.presentation.dtos.LoginRequestDTO;
import com.irs.springbootjwtopensslexample.presentation.dtos.LoginResponseDTO;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@AllArgsConstructor
public class AuthController {
    
    private final AuthService authService;
    
    @PostMapping(value = "/")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO loginRequestDTO) {
        return new ResponseEntity<>(authService.login(loginRequestDTO), HttpStatus.OK);
    }
}
