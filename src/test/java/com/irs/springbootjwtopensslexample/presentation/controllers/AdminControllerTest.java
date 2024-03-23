package com.irs.springbootjwtopensslexample.presentation.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.irs.springbootjwtopensslexample.business.services.impl.AuthServiceImpl;
import com.irs.springbootjwtopensslexample.business.services.impl.JoseJwtServiceImpl;
import com.irs.springbootjwtopensslexample.config.JwtProperties;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.irs.springbootjwtopensslexample.config.SecurityConfig;
import com.irs.springbootjwtopensslexample.presentation.dtos.LoginRequestDTO;
import com.irs.springbootjwtopensslexample.presentation.dtos.LoginResponseDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@WebMvcTest({AdminController.class, AuthController.class, UserController.class})
@Import({SecurityConfig.class, JwtProperties.class, JoseJwtServiceImpl.class, AuthServiceImpl.class})
public class AdminControllerTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    private final LoginRequestDTO loginRequestDTO;
            
    public AdminControllerTest() {
        this.loginRequestDTO = LoginRequestDTO.builder()
                .username("admin")
                .password("password")
                .build();
    }

    @BeforeAll
    public static void setUpClass() {
    }

    @AfterAll
    public static void tearDownClass() {
    }

    @BeforeEach
    public void setUp() {
    }

    @AfterEach
    public void tearDown() {
    }

    @Test
    void rootWhenUnauthenticatedThen403() throws Exception {
        this.mvc.perform(get("/api/v1/admin"))
                .andExpect(status().isForbidden());
    }

    @Test
    void rootWhenAuthenticatedThenSaysHelloAdmin() throws Exception {
        MvcResult result = this.mvc.perform(post("/api/v1/auth/")
                .content(mapper.writeValueAsString(this.loginRequestDTO))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        LoginResponseDTO loginResponseDTO = mapper.readValue(jsonResponse, LoginResponseDTO.class);

        this.mvc.perform(get("/api/v1/admin")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + loginResponseDTO.getToken()))
                .andExpect(content().string("Admin Hello, admin"));
    }

    @Test
    void rootWhenAuthenticatedThenSaysHelloUser() throws Exception {
        MvcResult result = this.mvc.perform(post("/api/v1/auth/")
                .content(mapper.writeValueAsString(this.loginRequestDTO))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        LoginResponseDTO loginResponseDTO = mapper.readValue(jsonResponse, LoginResponseDTO.class);

       this.mvc.perform(get("/api/v1/user")
               .header(HttpHeaders.AUTHORIZATION, "Bearer " + loginResponseDTO.getToken()))
               .andExpect(content().string("User Hello, admin"));
    }
}
