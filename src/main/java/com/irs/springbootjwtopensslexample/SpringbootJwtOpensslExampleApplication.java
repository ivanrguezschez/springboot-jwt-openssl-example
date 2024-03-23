package com.irs.springbootjwtopensslexample;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringbootJwtOpensslExampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringbootJwtOpensslExampleApplication.class, args);
    }

    /*
    @Bean
    CommandLineRunner commandLineRunner(PasswordEncoder passwordEncoder) {
        return args -> {
            System.out.println("Codificando contraseñas...");
            String passwordEncode = passwordEncoder.encode("password");
            System.out.println("password->" + passwordEncode + "<-");
        };
    }
    */
}
