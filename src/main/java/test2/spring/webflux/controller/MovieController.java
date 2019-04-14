package test2.spring.webflux.controller;

import org.omg.CosNaming.NamingContextPackage.NotFound;
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

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/movie")
public class MovieController {

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/{id}/add-user-favourite")
    public Mono addFavourite(@PathVariable("id") String strId,
                                             @AuthenticationPrincipal CurrentUser currentUser) {
        List<Movie> movies = new ArrayList<>();
        return movieRepository.findById(strId)
                .switchIfEmpty(Mono.error(NotFound::new))
                .filter(movie -> {
                    movies.add(movie);
                    currentUser.getUser().getMovieList().add(movie);
                    return true;
                })
                .then(userRepository
                        .save(currentUser.getUser())
                        .map(user -> movies.get(movies.size() - 1)));
    }

    @PostMapping
    public Mono<Movie> add(@RequestBody Movie movie){
        return movieRepository.save(movie);
    }

    @GetMapping(produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
    public Flux<Movie> getAll(){
        return movieRepository.findAll();
    }

    @GetMapping("/user/favourite")
    public Flux<Movie> getAllByUserFavourite(@AuthenticationPrincipal CurrentUser currentUser){
        return userRepository.findById(currentUser.getUser().getId())
                .map(User::getMovieList)
                .flatMapMany(Flux::fromIterable);
    }
}