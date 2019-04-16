package test2.spring.webflux.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import test2.spring.webflux.util.JwtUtil;

@TestConfiguration
public class SecurityTestConfig {

    @Bean
    public JwtUtil jwtUtil(){
        return new JwtUtil();
    }

    @Bean
    public ReactiveUserDetailsService reactiveUserDetailsService(){
        return new ReactiveUserDetailsServiceImpl();
    }
}
