package ar.edu.itba.paw.webapp.auth;

import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.User;
import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import java.net.URLDecoder;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.text.html.Option;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collections;
import java.util.NoSuchElementException;
import java.util.Optional;

@Component
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final static String HEADER_AUTHORIZATION = "Authorization";
    private final static String TOKEN_PREFIX = "Bearer ";
    private final static String BASIC_PREFIX = "Basic ";

    @Autowired
    private UserDetailsService pawUserDetailsService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    private static final Logger LOGGER = LoggerFactory.getLogger(JwtAuthorizationFilter.class);

    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        try {
            try {

                final String header = request.getHeader(HEADER_AUTHORIZATION);
                if (header == null) {
                    LOGGER.debug("no header");
                    chain.doFilter(request, response);
                    return;
                }
                if (header.startsWith(BASIC_PREFIX)) {
                    LOGGER.debug("Basic");

                    handleBasicAuthentication(header, request, response, chain);
                    return;
                }

                if (header.startsWith(TOKEN_PREFIX)) {
                    LOGGER.debug("Bearer");

                    handleBearerAuthentication(header, request, response, chain);
                    return;
                }
                LOGGER.debug("continuing filter");
                chain.doFilter(request, response);

            } catch (UsernameNotFoundException e) {
                // handleUserNotFound(response);
                chain.doFilter(request, response);
            }
        } catch (ServletException | IOException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private void handleBearerAuthentication(String header, HttpServletRequest request, HttpServletResponse response,
            FilterChain chain) throws IOException, ServletException {
        String token = parseToken(header);
        if (!validateJwtToken(token)) {
            chain.doFilter(request, response);
            return;
        }

        String email = TokenProvider.getUsername(token);
        executeFilter(email, chain, request, response);
    }

    private void handleBasicAuthentication(String header, HttpServletRequest request, HttpServletResponse response,
            FilterChain chain) throws IOException, ServletException {
        String decoded = new String(Base64.getDecoder().decode(header.substring(BASIC_PREFIX.length())));
        String[] credentials = decoded.split(":");
        if (credentials.length != 2 || StringUtils.isEmpty(credentials[0]) || StringUtils.isEmpty(credentials[1])) {
            chain.doFilter(request, response);
            return;
        }

        String email = URLDecoder.decode(credentials[0], StandardCharsets.UTF_8.name());
        String password = URLDecoder.decode(credentials[1], StandardCharsets.UTF_8.name());
        User user = userService.findByEmail(email);

        if (user.getPassword() == null)
            throw new IllegalArgumentException();
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(email, password);
        try {
            Authentication auth = authenticationManager.authenticate(token);
            if (!auth.isAuthenticated()) {
                chain.doFilter(request, response);
                return;
            }
        } catch (AuthenticationException e) {
            chain.doFilter(request, response);
            return;
        }

        try {
            String jwt = TokenProvider.generateToken(user);
            response.setHeader("Authorization", TOKEN_PREFIX + jwt);
            executeFilter(email, chain, request, response);
        } catch (NoSuchElementException e) {
            chain.doFilter(request, response);
        }
    }

    private String parseToken(String authorizationHeaderValue) {
        return authorizationHeaderValue.substring(TOKEN_PREFIX.length());
    }

    private void executeFilter(String email, FilterChain chain,
            HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException, UsernameNotFoundException {
        final UserDetails user = pawUserDetailsService.loadUserByUsername(email);

        final UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                user, null,
                user == null ? Collections.emptyList() : user.getAuthorities());

        authentication.setDetails(
                new WebAuthenticationDetailsSource().buildDetails(req));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        chain.doFilter(req, res);
    }

    private boolean validateJwtToken(final String token) {
        try {
            Jwts.parser().setSigningKey(TokenProvider.getKey()).parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            LOGGER.debug("Error validating jwt ={}", e.getMessage());
        }
        return false;
    }

    //
    // private void handleUserNotFound(HttpServletResponse response){
    // response.setStatus(HttpServletResponse.SC_NOT_FOUND);
    // response.setContentType("application/json");
    // response.setCharacterEncoding("UTF-8");
    //
    // try {
    // JSONObject jsonObject =
    // ErrorHttpServletResponseDto.produceErrorDto(ErrorCode.USER_NOT_FOUND.getCode(),
    // ErrorCode.USER_NOT_FOUND.getMessage(), null);
    // response.getWriter().write(jsonObject.toString());
    // response.getWriter().flush();
    // } catch (IOException ex) {
    // ex.printStackTrace();
    // response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    // }
    // }
    // protected void unsuccessfulAuthentication(HttpServletRequest request,
    // HttpServletResponse response) throws IOException {
    // final JSONObject jsonObject =
    // ErrorHttpServletResponseDto.produceErrorDto(ErrorCode.INVALID_PASSWORD.getCode(),
    // ErrorCode.INVALID_PASSWORD.getMessage(), null);
    // response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    // response.getWriter().write(jsonObject.toString());
    // response.getWriter().flush();
    // }

}
