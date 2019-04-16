package test2.spring.webflux.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import test2.spring.webflux.model.UserType;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .authorizeExchange()
                .pathMatchers(HttpMethod.POST,"/movie")
                .hasAuthority("ADMIN")
                .pathMatchers("/login")
                .hasAuthority(UserType.ROLE_ANONYMOUS.name())
                .pathMatchers("/user","/movie")
                .permitAll()
                .anyExchange()
                .hasAuthority("USER")
        .and()
                .formLogin().disable()
                .csrf()
                .disable()
                .securityContextRepository(serverSecurityContextRepository())
                .build();
    }

    @Bean
    public ServerSecurityContextRepository serverSecurityContextRepository(){
        return new SecurityContextRepositoryImpl();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}