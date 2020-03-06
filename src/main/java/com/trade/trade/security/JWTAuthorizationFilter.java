package com.trade.trade.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.trade.trade.exceptions.ResourceNotFoundException;
import com.trade.trade.models.User;
import com.trade.trade.repositories.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JWTAuthorizationFilter extends BasicAuthenticationFilter {
    private UserRepository userRepository;

    public JWTAuthorizationFilter(AuthenticationManager authenticationManager, UserRepository userRepository) {
        super(authenticationManager);
        this.userRepository = userRepository;
    }

    private Authentication getUsernamePasswordAuthentication(HttpServletRequest request) {
        String token = request.getHeader(JWTProperties.HEADER_STRING).replace(JWTProperties.TOKEN_PREFIX, "");

        if (token != null) {
            String username = JWT.require(Algorithm.HMAC512(JWTProperties.SECRET.getBytes()))
                    .build()
                    .verify(token)
                    .getSubject();

            if (username != null) {
                User user = userRepository.findByUsername(username)
                        .orElseThrow(() -> new ResourceNotFoundException(User.class, username));
                UserPrincipal userPrincipal = new UserPrincipal(user);
                return new UsernamePasswordAuthenticationToken(username, null, userPrincipal.getAuthorities());
            }
        }

        return null;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        String header = request.getHeader(JWTProperties.HEADER_STRING);

        if (header == null || !header.startsWith(JWTProperties.TOKEN_PREFIX)) {
            chain.doFilter(request, response);
            return;
        }

        Authentication authentication = getUsernamePasswordAuthentication(request);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        chain.doFilter(request, response);
    }
}
