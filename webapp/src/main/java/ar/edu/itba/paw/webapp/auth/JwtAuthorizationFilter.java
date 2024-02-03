package ar.edu.itba.paw.webapp.auth;

import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.webapp.controller.dto.errors.ErrorCode;
import ar.edu.itba.paw.webapp.controller.dto.errors.ErrorHttpServletResponseDto;
import io.jsonwebtoken.*;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.HttpHeaders;
import java.io.IOException;
import java.util.Base64;
import java.util.Collections;
import java.util.Date;
import java.util.Optional;


@Component
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private static final String HEADER_AUTHORIZATION = "Authorization";
    private static final String TOKEN_PREFIX = "Bearer ";
    private static final String BASIC_PREFIX = "Basic ";

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
            // Get authorization header and value
            if (request.getPathInfo() == null) {
                chain.doFilter(request, response);
                return;
            }

            final String header = request.getHeader(HEADER_AUTHORIZATION);

            if (StringUtils.isEmpty(header)) {
                chain.doFilter(request, response);
                return;
            }

            if (header.startsWith(BASIC_PREFIX)) {
                handleBasicAuthentication(header, request, response, chain);
            } else if (header.startsWith(TOKEN_PREFIX)) {
                handleBearerTokenAuthentication(header, request, response, chain);
            } else {
                chain.doFilter(request, response);
            }

        }catch (ServletException | IOException e) {
            handleInternalServerError(response);
        }
    }

    private void handleBasicAuthentication(String header, HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException, UsernameNotFoundException {
        try {
            final String decoded = new String(Base64.getDecoder().decode(header.substring(BASIC_PREFIX.length())));
            final String[] credentials = decoded.split(":");
            if (credentials.length != 2) {
                chain.doFilter(request, response);
                return;
            }
            final String email = credentials[0];
            final String password = credentials[1];


            Optional<User> user = userService.findByEmail(email);
            if (!user.isPresent())
                throw new UsernameNotFoundException("");
            final UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(email, password);
            try {
                Authentication auth = authenticationManager.authenticate(token);
                if (!auth.isAuthenticated()) {
                    //response = unsuccessfulAuthentication(response, ErrorCode.INVALID_PASSWORD);
                    chain.doFilter(request, response);
                    return;
                }
            }catch (Exception exception){
                //response = unsuccessfulAuthentication(response, ErrorCode.INVALID_PASSWORD);
                chain.doFilter(request, response);
                return;
            }
            final String jwt = TokenProvider.generateToken(user.get());
            response.setHeader(HEADER_AUTHORIZATION, TOKEN_PREFIX + jwt);
            executeFilter(email, chain, request, response);
        } catch (IllegalArgumentException e) {
            chain.doFilter(request, response);
            //handleUnauthorized(response,ErrorCode.NOT_KNOW_ERROR);
        }
    }

    private void handleBearerTokenAuthentication(String header, HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException, UsernameNotFoundException {
        final String token = parseToken(header);

        if (!validateJwtToken(token)) {
            //unsuccessfulAuthentication(response, ErrorCode.INVALID_PASSWORD);
            chain.doFilter(request, response);
            return;
        }
        response.setHeader(HttpHeaders.AUTHORIZATION, header);
        final String email = TokenProvider.getUsername(token);
        executeFilter(email, chain, request, response);
    }

    private String parseToken(String authorizationHeaderValue) {
        return authorizationHeaderValue.substring(TOKEN_PREFIX.length());
    }

    private void executeFilter(String email, FilterChain chain, HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException, UsernameNotFoundException {
        final UserDetails user = pawUserDetailsService.loadUserByUsername(email);

        final UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                user, null,
                user == null ?
                        Collections.emptyList() : user.getAuthorities()
        );

        authentication.setDetails(
                new WebAuthenticationDetailsSource().buildDetails(req)
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        chain.doFilter(req, res);
    }

    private boolean validateJwtToken(final String token) {
        try {
            Claims claims = Jwts.parser().setSigningKey(TokenProvider.getKey()).parseClaimsJws(token).getBody();

            Date expirationDate = claims.getExpiration();
            if (expirationDate != null && expirationDate.before(new Date())) {
                LOGGER.debug("JWT token has expired");
                return false;
            }

            return true;
        } catch (Exception e) {
            LOGGER.debug("Error validating JWT: {}", e.getMessage());
            return false;
        }
    }

/*    protected HttpServletResponse unsuccessfulAuthentication(HttpServletResponse response, ErrorCode errorCode) {
        final JSONObject jsonObject = ErrorHttpServletResponseDto.produceErrorDto(errorCode.getCode(), errorCode.getMessage(), null);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        try {
            response.getWriter().write(jsonObject.toString());
            response.getWriter().flush();
            return response;
        }catch (IOException e) {
            // Log or handle the IOException appropriately
            LOGGER.error("IOException while writing to the response writer", e);
        }
        return response;
    }

    private void handleUnauthorized(HttpServletResponse response, ErrorCode errorCode){
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        final JSONObject jsonObject = ErrorHttpServletResponseDto.produceErrorDto(errorCode.getCode(), errorCode.getMessage(), null);
        try {
            response.getWriter().write(jsonObject.toString());
            response.getWriter().flush();
        }  catch (IOException e) {
        // Log or handle the IOException appropriately
        LOGGER.error("IOException while writing to the response writer", e);
    }
    }
*/
    private void handleInternalServerError(HttpServletResponse response) {
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }
}
