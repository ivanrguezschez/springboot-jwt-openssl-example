package com.irs.springbootjwtopensslexample.business.services;

import com.irs.springbootjwtopensslexample.presentation.dtos.LoginRequestDTO;
import com.irs.springbootjwtopensslexample.presentation.dtos.LoginResponseDTO;

public interface AuthService {
    
     LoginResponseDTO login(LoginRequestDTO loginRequestDTO);
}
