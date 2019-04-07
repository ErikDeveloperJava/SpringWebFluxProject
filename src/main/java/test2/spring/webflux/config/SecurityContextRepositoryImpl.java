package test2.spring.webflux.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import test2.spring.webflux.util.JwtUtil;

import java.util.List;

public class SecurityContextRepositoryImpl implements ServerSecurityContextRepository {

    @Autowired
    private ReactiveUserDetailsService reactiveUserDetailsService;

    @Autowired
    private JwtUtil jwtUtil;


    @Override
    public Mono<Void> save(ServerWebExchange serverWebExchange, SecurityContext securityContext) {
        return null;
    }

    @Override
    public Mono<SecurityContext> load(ServerWebExchange serverWebExchange) {
        List<String> authorziationHeaderList = serverWebExchange
                .getRequest()
                .getHeaders()
                .get("AUTHORIZATION");
        String authorizationToken = authorziationHeaderList != null ? authorziationHeaderList.get(0) : null;
        if (authorizationToken != null) {
            String email = jwtUtil.parseToken(authorizationToken);
            if (email != null) {
                return reactiveUserDetailsService.findByUsername(email)
                        .cast(CurrentUser.class)
                        .map(currentUser -> new SecurityContextImpl(new UsernamePasswordAuthenticationToken(currentUser,null,currentUser.getAuthorities())));
            }
        }
        return Mono.empty();
    }
}
