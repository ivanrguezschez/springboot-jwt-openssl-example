package com.irs.springbootjwtopensslexample.presentation.controllers;

import java.security.Principal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {
    
    @GetMapping
    public String homeAdmin(Principal principal) {
        return "Admin Hello, " + principal.getName();
    }
}
