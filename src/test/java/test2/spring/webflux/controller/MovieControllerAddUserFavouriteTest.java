package test2.spring.webflux.controller;

import io.jsonwebtoken.impl.DefaultClaims;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import test2.spring.webflux.config.SecurityConfig;
import test2.spring.webflux.config.SecurityTestConfig;
import test2.spring.webflux.model.Movie;
import test2.spring.webflux.model.User;
import test2.spring.webflux.model.UserType;
import test2.spring.webflux.repository.MovieRepository;
import test2.spring.webflux.repository.UserRepository;
import test2.spring.webflux.util.JwtUtil;

import java.util.HashSet;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@WebFluxTest(controllers = MovieController.class)
@ContextConfiguration(classes = {SecurityConfig.class, MovieController.class, SecurityTestConfig.class})
public class MovieControllerAddUserFavouriteTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private MovieRepository movieRepository;

    @MockBean
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Test
    public void authorizationTest() {
        webTestClient
                .post()
                .uri("/movie/12/favourite")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isEqualTo(403);
    }

    @Test
    public void wrongMovieIdTest() {
        String movieId = "1";
        User currentUser = User
                .builder()
                .id("1")
                .name("Tomas")
                .surname("Walter")
                .email("tomas@gmail.com")
                .password("pass")
                .userType(UserType.USER)
                .age(12)
                .build();
        String authorizationToken = jwtUtil.generateToken(currentUser.getEmail(), new DefaultClaims());
        when(userRepository.findByEmail(currentUser.getEmail()))
                .thenReturn(Mono
                        .just(currentUser));
        when(movieRepository.findById(movieId))
                .thenReturn(Mono.empty());
        webTestClient
                .post()
                .uri("/movie/" + movieId + "/favourite")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .header("AUTHORIZATION", authorizationToken)
                .exchange()
                .expectStatus()
                .isNotFound();
    }

    @Test
    public void addFavouriteListSuccess() {
        String movieId = "1";
        User currentUser = User
                .builder()
                .id("1")
                .name("Tomas")
                .surname("Walter")
                .email("tomas@gmail.com")
                .password("pass")
                .userType(UserType.USER)
                .age(12)
                .movieList(new HashSet<>())
                .build();
        Movie movie = Movie
                .builder()
                .id(movieId)
                .year(2019)
                .name("movie_01")
                .director("director_01")
                .build();
        String authorizationToken = jwtUtil.generateToken(currentUser.getEmail(), new DefaultClaims());
        when(userRepository.findByEmail(currentUser.getEmail()))
                .thenReturn(Mono
                        .just(currentUser));
        when(movieRepository.findById(movieId))
                .thenReturn(Mono.just(movie));
        when(userRepository.save(currentUser))
                .thenReturn(Mono.just(currentUser));
        webTestClient
                .post()
                .uri("/movie/" + movieId + "/favourite")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .header("AUTHORIZATION", authorizationToken)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(Movie.class)
                .isEqualTo(movie);
    }
}