package ar.edu.itba.paw.webapp.auth;

import ar.edu.itba.paw.webapp.controller.dto.errors.ErrorCode;
import ar.edu.itba.paw.webapp.controller.dto.errors.ErrorHttpServletResponseDto;
import io.jsonwebtoken.*;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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
import java.io.IOException;
import java.util.Collections;


@Component
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final static String HEADER_AUTHORIZATION = "Authorization";
    private final static String TOKEN_PREFIX = "Bearer ";

    @Autowired
    private UserDetailsService pawUserDetailsService;

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

            if (StringUtils.isEmpty(header) || !header.startsWith("Bearer ")) {
                chain.doFilter(request, response);
                return;
            }

            // Get jwt token and validate
            final String token = parseToken(header);

            if(!validateJwtToken(token)) {
                chain.doFilter(request, response);
                return;
            }

            final String email = TokenProvider.getUsername(token);

            // Get user identity and set it on the spring security context
            executeFilter(email, chain, request, response);
        } catch (ServletException | IOException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
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
}
