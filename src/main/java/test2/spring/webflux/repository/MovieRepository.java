package test2.spring.webflux.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import test2.spring.webflux.model.Movie;

public interface MovieRepository extends ReactiveMongoRepository<Movie,String> {
}
