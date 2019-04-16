package test2.spring.webflux.controller;

import io.jsonwebtoken.impl.DefaultClaims;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import test2.spring.webflux.config.ReactiveUserDetailsServiceImpl;
import test2.spring.webflux.config.SecurityConfig;
import test2.spring.webflux.config.SecurityTestConfig;
import test2.spring.webflux.dto.LoginDto;
import test2.spring.webflux.model.User;
import test2.spring.webflux.model.UserType;
import test2.spring.webflux.repository.UserRepository;
import test2.spring.webflux.util.JwtUtil;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@WebFluxTest(controllers = LoginController.class)
@ContextConfiguration(classes = {LoginController.class, SecurityConfig.class, SecurityTestConfig.class})
public class LoginControllerTest {

    private static final String URI = "/login";

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;


    @Test
    public void authorizationTest(){
        String email = "tomas@gmail.com";
        String authorizationToken = jwtUtil.generateToken(email, new DefaultClaims());
        when(userRepository.findByEmail(email))
                .thenReturn(Mono.just(User
                        .builder()
                        .name("Tomas")
                        .surname("Walter")
                        .email(email)
                        .password("**************")
                        .age(20)
                        .userType(UserType.USER)
                        .build()));
        webTestClient
                .post()
                .uri(URI)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("AUTHORIZATION",authorizationToken)
        .exchange()
                .expectStatus()
                .isEqualTo(403);
    }

    @Test
    public void notExistsEmailTest(){
        String email = "tomas@gmail.com";
        when(userRepository.findByEmail(email))
                .thenReturn(Mono.empty());
        webTestClient
                .post()
                .uri(URI)
                .contentType(MediaType.APPLICATION_JSON)
                .syncBody(User
                        .builder()
                        .email(email)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
        .exchange()
                .expectStatus()
                .isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void wrongPasswordTest(){
        String email = "tomas@gmail.com";
        when(userRepository.findByEmail(email))
                .thenReturn(Mono.just(User
                        .builder()
                        .email(email)
                        .password(passwordEncoder.encode("pass"))
                        .build()));
        webTestClient
                .post()
                .uri(URI)
                .contentType(MediaType.APPLICATION_JSON)
                .syncBody(User
                        .builder()
                        .email(email)
                        .password("password")
                        .build())
                .accept(MediaType.APPLICATION_JSON)
        .exchange()
                .expectStatus()
                .isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void authenticatedSuccessFully(){
        String email = "tomas@gmail.com";
        String password = "tomas";
        User user = User
                .builder()
                .id("1")
                .name("Tomas")
                .surname("Walter")
                .userType(UserType.USER)
                .age(20)
                .email(email)
                .password(passwordEncoder.encode(password))
                .build();
        when(userRepository.findByEmail(email))
                .thenReturn(Mono.just(user));
        String authorizationToken = jwtUtil.generateToken(email, new DefaultClaims());
        LoginDto exceptResponse = LoginDto
                .builder()
                .authorizationToken(authorizationToken)
                .user(user)
                .build();
        webTestClient
                .post()
                .uri(URI)
                .contentType(MediaType.APPLICATION_JSON)
                .syncBody(User
                        .builder()
                        .email(email)
                        .password(password)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
        .exchange()
                .expectStatus()
                .isOk()
                .expectBody(LoginDto.class)
                .isEqualTo(exceptResponse);
    }
}