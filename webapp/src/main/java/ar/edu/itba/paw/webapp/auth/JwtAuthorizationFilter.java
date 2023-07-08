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
            // Get authorization header and val
            if (request.getPathInfo() == null) {
                chain.doFilter(request, response);
                return;
            }

            final String header = request.getHeader(HEADER_AUTHORIZATION);
            if (StringUtils.isEmpty(header) ) {
                chain.doFilter(request, response);
                return;
            }

            if( header.startsWith("Basic ")){
                //si la contrasena o username tiene 2 puntos??
                LOGGER.info("Authorization header: " + header);
                final String decoded = new String(Base64.getDecoder().decode( header.substring(BASIC_PREFIX.length())));
                final String[] credentials = decoded.split(":");
                if(credentials.length != 2){
                    chain.doFilter(request,response);
                    return;
                }
                final String email = URLDecoder.decode(credentials[0], StandardCharsets.UTF_8.name());
                final String password = URLDecoder.decode(credentials[1], StandardCharsets.UTF_8.name());

                pawUserDetailsService.loadUserByUsername(email);

                final UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(email, password);
                Authentication auth = authenticationManager.authenticate(token);
                if(!auth.isAuthenticated()){
                    unsuccessfulAuthentication(request,response);
                    return;
                }
                Optional<User> user = userService.findByEmail(email);
                if(!user.isPresent())
                    throw new UsernameNotFoundException("");
                //create the jwt

                //TODO si el usuario esta verificado
                final String jwt = TokenProvider.generateToken(user.get());
                response.setHeader("Authorization", "Bearer " + jwt);
                executeFilter(email,chain,request,response);
                return;
            }

            if( header.startsWith("Bearer ")) {
                // Get jwt token and validate
                final String token = parseToken(header);

                if (!validateJwtToken(token)) {
                    chain.doFilter(request, response);
                    return;
                }

                final String email = TokenProvider.getUsername(token);
                // Get user identity and set it on the spring security context
                executeFilter(email, chain, request, response);
            }

        } catch (ServletException | IOException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return;
        } catch (UsernameNotFoundException e) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            try {
                final JSONObject jsonObject = ErrorHttpServletResponseDto.produceErrorDto(ErrorCode.USER_NOT_FOUND.getCode(), ErrorCode.USER_NOT_FOUND.getMessage(), null);
                response.getWriter().write(jsonObject.toString());
                response.getWriter().flush();
            } catch (IOException ex) {
                ex.printStackTrace();
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
            return;
        }
        try {
            chain.doFilter(request, response);
        }catch (IOException | ServletException e){
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private String parseToken(String authorizationHeaderValue) {
        return authorizationHeaderValue.substring(TOKEN_PREFIX.length());
    }

    private void executeFilter(String email, FilterChain chain,
                               HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException, UsernameNotFoundException {
        final UserDetails user = pawUserDetailsService.loadUserByUsername(email);

        final UsernamePasswordAuthenticationToken
                authentication = new UsernamePasswordAuthenticationToken(
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

    private boolean validateJwtToken(final String token)  {
        try {
            Jwts.parser().setSigningKey(TokenProvider.getKey()).parseClaimsJws(token);
            return true;
        } catch ( Exception e){
            LOGGER.debug("Error validating jwt ={}", e.getMessage());
        }
        return false;
    }



    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response) throws IOException {
        final JSONObject jsonObject = ErrorHttpServletResponseDto.produceErrorDto(ErrorCode.INVALID_PASSWORD.getCode(), ErrorCode.INVALID_PASSWORD.getMessage(), null);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write(jsonObject.toString());
        response.getWriter().flush();
    }

}
