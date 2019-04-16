package test2.spring.webflux.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Document(collection="user")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(exclude = {"movieList"})
public class User {

    @Id
    private String id;

    private String name;

    private String surname;

    private String password;

    private String email;

    private int age;

    private UserType userType;

    @DBRef
    private Set<Movie> movieList = new HashSet<>();
}
