package test2.spring.webflux.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import test2.spring.webflux.model.User;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginDto {

    private String authorizationToken;

    private User user;
}