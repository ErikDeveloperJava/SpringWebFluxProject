package test2.spring.webflux;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.reactive.config.EnableWebFlux;
import test2.spring.webflux.model.User;
import test2.spring.webflux.model.UserType;
import test2.spring.webflux.repository.UserRepository;

@SpringBootApplication
public class DemoApplication implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        userRepository.existsByUserType(UserType.ADMIN)
                .subscribe(value -> {
                    if (!value) {
                        userRepository
                                .save(User
                                        .builder()
                                        .name("Admin")
                                        .surname("Admin")
                                        .password(passwordEncoder.encode("admin"))
                                        .email("admin@gmail.com")
                                        .age(20)
                                        .userType(UserType.ADMIN)
                                        .build())
                                .block();
                    }
                });
    }
}