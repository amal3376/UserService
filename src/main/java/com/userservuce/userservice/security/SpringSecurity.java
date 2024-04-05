package com.userservuce.userservice.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SpringSecurity {
    @Bean
    @Order(1)
    public SecurityFilterChain filteringCriteria(HttpSecurity http) throws Exception{
        http.cors().disable(); //(cors->cors.disable());
        http.csrf().disable();//(csrf -> csrf.disable());
        http.authorizeHttpRequests(authorize -> authorize.anyRequest().permitAll());
//         http.authorizeHttpRequests(authorize -> authorize.requestMatchers("/auth/logout").denyAll());
        return http.build();
    }
    // Objects that handels what all api endpoints should be authenticated
    // vs what all shouldn't be authenticated

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
