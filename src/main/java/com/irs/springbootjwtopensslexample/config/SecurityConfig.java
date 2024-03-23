package com.irs.springbootjwtopensslexample.config;

import com.irs.springbootjwtopensslexample.business.services.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
//@RequiredArgsConstructor
public class SecurityConfig {

    /*
        Comento @RequiredArgsConstructor ya que esto genera un constructor con el parametro JwtService pero sin el @Qualifier
        y aunque lo ponga (descomente) en el atributo private final JwtService jwtService; no funciona.
        Se tiene que crear un constructor donde se usa el @Qualifier en el parámetro.
    
        En esta clase y en todas las clases que usen JwtService
    */
    
    //@Qualifier("jsonwebtokenJwtService")
    private final JwtService jwtService;

    //private final AuthenticationProvider authenticationProvider;

    //public SecurityConfig(@Qualifier("jsonwebtokenJwtService") JwtService jwtService) {
    public SecurityConfig(@Qualifier("joseJwtService") JwtService jwtService) {
        this.jwtService = jwtService;
    }
          
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        /*
        return new InMemoryUserDetailsManager(
                List.of(
                         User.withUsername("admin")
                                .password("{noop}password")
                                .authorities("ADMIN")
                                .build(),
                        User.withUsername("user")
                                .password("{noop}123456")
                                .authorities("USER")
                                .build()
                )
        );
        */
        UserDetails admin = User.builder()
                .username("admin")
                .password(passwordEncoder().encode("password")) // password
                .roles("ADMIN", "USER")
                .build();
        UserDetails user = User.builder()
                .username("user")
                .password(passwordEncoder().encode("password")) // password
                .roles("USER")
                .build();
        
        return new InMemoryUserDetailsManager(admin, user);
    }

    /*
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());

        return authProvider;
    }
    */
    
    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtService, userDetailsService());
    }
        
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers("/api/v1/auth/**").permitAll();
                    auth.requestMatchers("/api/v1/admin").hasRole("ADMIN");
                    auth.requestMatchers("/api/v1/user").hasAnyRole("ADMIN", "USER");
                    auth.anyRequest().authenticated();
                })
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                //.authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                /*
                // Con esto conseguimos devolver un 401 Unathorized cuando el usuario está autenticado pero no está autorizado
                // Sin esto, cuando no esta autorizado devuelve un 403 Forbidden (no autorizado o acceso denegado)
                .exceptionHandling(exceptionHandlingCustomizer -> exceptionHandlingCustomizer.authenticationEntryPoint(
                    (request, response, authException) -> {
                        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "No Autorizado");
                    }
                ))
                */
                .build();
    }
}
