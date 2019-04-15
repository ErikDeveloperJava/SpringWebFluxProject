package test2.spring.webflux.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
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

    @PostMapping("/{id}/favourite")
    public Mono addFavourite(@PathVariable("id") String strId,
                             @AuthenticationPrincipal CurrentUser currentUser) {
        return movieRepository.findById(strId)
                .defaultIfEmpty(Movie
                        .builder()
                        .id("empty")
                        .build())
                .flatMap(movie -> {
                    if (movie.getId().equals("empty")) {
                        return Mono.just(ResponseEntity
                                .notFound()
                                .build());
                    } else {
                        currentUser.getUser().getMovieList().add(movie);
                        return userRepository
                                .save(currentUser.getUser())
                                .map(user -> ResponseEntity.ok(movie));
                    }
                });
    }

    @PostMapping
    public Mono<Movie> add(@RequestBody Movie movie) {
        return movieRepository.save(movie);
    }

    @GetMapping(produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
    public Flux<Movie> getAll() {
        return movieRepository.findAll();
    }

    @GetMapping("/user/favourite")
    public Flux<Movie> getAllByUserFavourite(@AuthenticationPrincipal CurrentUser currentUser) {
        return userRepository.findById(currentUser.getUser().getId())
                .map(User::getMovieList)
                .flatMapMany(Flux::fromIterable);
    }

    @DeleteMapping("/{movieId}/favourite")
    public Mono<ResponseEntity> deleteUserFavouriteMovie(@AuthenticationPrincipal CurrentUser currentUser,
                                                @PathVariable("movieId") String movieId) {
        return movieRepository
                .findById(movieId)
                .defaultIfEmpty(Movie
                        .builder()
                        .id("empty")
                        .build())
                .flatMap(movie -> {
                    if (movie.getId().equals("empty")) {
                        return Mono.just(ResponseEntity.notFound().build());
                    } else {
                        return userRepository
                                .findById(currentUser.getUser().getId())
                                .flatMap(user -> {
                                    if (user.getMovieList().contains(movie)) {
                                        user.getMovieList().remove(movie);
                                        return userRepository
                                                .save(user)
                                                .flatMap(user1 -> Mono.just(ResponseEntity
                                                        .ok("movie deleted successfully")));
                                    } else {
                                        return Mono.just(ResponseEntity
                                                .ok("this movie does not exist in the user movie list"));
                                    }
                                });
                    }
                });
    }
}