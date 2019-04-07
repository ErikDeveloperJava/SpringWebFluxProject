package test2.spring.webflux.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;
import test2.spring.webflux.model.User;
import test2.spring.webflux.model.UserType;

public interface UserRepository extends ReactiveMongoRepository<User,String> {

    Mono<User> findByEmail(String email);

    Mono<Boolean> existsByUserType(UserType userType);
}