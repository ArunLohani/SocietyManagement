package com.project.societyManagement.config;

import com.project.societyManagement.filter.JWTFilter;
import com.project.societyManagement.handler.Oauth2SuccessHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Slf4j
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {

    private final JWTFilter jwtFilter;
    @Autowired
    private  Oauth2SuccessHandler oauth2SuccessHandler;
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf->csrf.disable())
                .sessionManagement(session->session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(request ->
                        request.requestMatchers(
                                "/auth/**",
                                        "/v3/api-docs/**" ,
                                        "/v3/api-docs",
                                        "/swagger-ui/**",
                                        "/swagger-ui.html",
                                        "/actuator/refresh").permitAll()
                                .anyRequest().authenticated())
                .oauth2Login(oauth->
                        oauth.
                                failureHandler((request, response, exception) ->{
                                    log.error("Oauth2 Error occurred {}", exception.getMessage());
                                } ).successHandler(oauth2SuccessHandler))

                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }


}
