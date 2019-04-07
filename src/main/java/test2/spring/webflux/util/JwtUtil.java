package test2.spring.webflux.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {


    @Value("${jwt.secret}")
    private String secret;

    public String generateToken(String email, Claims claims){
        return Jwts
                .builder()
                .setClaims(claims)
                .setSubject(email)
                .signWith(SignatureAlgorithm.HS512,secret.getBytes())
                .compact();
    }

    public String parseToken(String token){
        return (String) Jwts
                .parser()
                .setSigningKey(secret.getBytes())
                .parseClaimsJws(token)
                .getBody().get("sub");
    }
}