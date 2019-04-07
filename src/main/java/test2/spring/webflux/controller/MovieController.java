package test2.spring.webflux.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import test2.spring.webflux.config.CurrentUser;
import test2.spring.webflux.model.Movie;
import test2.spring.webflux.model.User;
import test2.spring.webflux.repository.MovieRepository;
import test2.spring.webflux.repository.UserRepository;

@RestController
@RequestMapping("/movie")
public class MovieController {

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/{id}/add-user-favourite")
    public Mono<ResponseEntity> addFavourite(@PathVariable("id") String strId,
                                             @AuthenticationPrincipal CurrentUser currentUser) {
        return movieRepository.findById(strId)
                .defaultIfEmpty(new Movie())
                .map(movie -> {
                    if (movie.getId() == null) {
                        return ResponseEntity
                                .notFound()
                                .build();
                    }
                    currentUser.getUser().getMovieList().add(movie);
                    return userRepository.save(currentUser.getUser())
                            .map(user -> ResponseEntity.ok(movie))
                            .block();
                });
    }

    @PostMapping
    public Mono<Movie> add(@RequestBody Movie movie){
        return movieRepository.save(movie);
    }

    @GetMapping(produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
    public Flux<Movie> getAll(){
        return movieRepository.findAll();
    }
}