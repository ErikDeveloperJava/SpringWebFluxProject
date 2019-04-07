package test2.spring.webflux.controller;

import io.jsonwebtoken.impl.DefaultClaims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import test2.spring.webflux.dto.LoginDto;
import test2.spring.webflux.model.User;
import test2.spring.webflux.repository.UserRepository;
import test2.spring.webflux.util.JwtUtil;

@RestController
@RequestMapping("/login")
public class LoginController {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping
    public Mono<ResponseEntity> login(@RequestBody User user){
        return userRepository
                .findByEmail(user.getEmail())
                .defaultIfEmpty(new User())
                .map(monoUser -> {
                    if(monoUser.getId() == null || !passwordEncoder.matches(user.getPassword(),monoUser.getPassword())){
                        return ResponseEntity
                                .status(HttpStatus.UNAUTHORIZED)
                                .build();
                    }
                    String authorizationToken = jwtUtil.generateToken(monoUser.getEmail(), new DefaultClaims());
                    return ResponseEntity
                            .ok(LoginDto
                                    .builder()
                                    .authorizationToken(authorizationToken)
                                    .user(monoUser)
                                    .build());
                });
    }
}