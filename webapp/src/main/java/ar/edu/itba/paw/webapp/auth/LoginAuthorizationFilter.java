package ar.edu.itba.paw.webapp.auth;

import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.User;

import ar.edu.itba.paw.webapp.controller.dto.errors.ErrorCode;

import ar.edu.itba.paw.webapp.controller.dto.errors.ErrorHttpServletResponseDto;



import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Optional;

@Component
public class  LoginAuthorizationFilter extends UsernamePasswordAuthenticationFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoginAuthorizationFilter.class);

    @Autowired
    private UserService us;

    private Optional<User> user;
    private boolean hasEntity;

    public LoginAuthorizationFilter(final AuthenticationManager authenticationManager) {
        super.setAuthenticationManager(authenticationManager);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        hasEntity = false;

        //intento obtener el reader, si falla 500? o no hago la auth

        BufferedReader reader = null;
        try {
            reader = request.getReader();
        } catch (IOException e) {
            e.printStackTrace();
            //thorw
        //    throw new AuthenticationException("");
        }


        final StringBuffer jb = new StringBuffer();
        String line;
        try {
            // TODO : y si el reader permanece null?
            while ((line = reader.readLine()) != null) {
                jb.append(line);
            }
        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
        } finally {
            try {
                reader.close();
            } catch (IOException | NullPointerException e) {
                e.printStackTrace();
            }

        }



        String email = "", password = "";
        try {
            final JSONObject jsonObject = new JSONObject(jb.toString());
            email = jsonObject.getString("email");
            password = jsonObject.getString("password");
        } catch (JSONException jsonException) {
            final JSONObject jsonObject = ErrorHttpServletResponseDto.produceErrorDto(ErrorCode.INVALID_JSON_FIELD.getCode(), ErrorCode.INVALID_JSON_FIELD.getMessage(), null);
            try {
                response.getWriter().write(jsonObject.toString());
                response.getWriter().flush();
            } catch (IOException e) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                e.printStackTrace();
            }
            return null;
        }



        String entity = "";
        JSONObject jsonObject1 = null;
        try {
            user = us.findByEmail(email);
            if (!user.isPresent())
                throw new UsernameNotFoundException("User not found");
        } catch (UsernameNotFoundException e) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            jsonObject1 = ErrorHttpServletResponseDto.produceErrorDto(ErrorCode.USER_NOT_FOUND.getCode(), ErrorCode.USER_NOT_FOUND.getMessage(), null);
            entity = "User.not.found";
        }

        if (entity.length() > 0) {
            hasEntity = true;
            try {
                response.getWriter().write(jsonObject1.toString());
                response.getWriter().flush();
                return null;
            } catch (IOException ex) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                return null;
            }
        }
        final UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(email, password);
        return this.getAuthenticationManager().authenticate(token);
    }

    @Override
    @Autowired
    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
        super.setAuthenticationManager(authenticationManager);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain
            chain, Authentication authResult) throws IOException {
        final String uriInfo = request.getRequestURL().substring(0, request.getRequestURL().lastIndexOf("/"));
        //TODO si el usuario esta verificado
        final String token = TokenProvider.generateToken(user.get());

        response.setStatus(HttpServletResponse.SC_OK);
        response.setHeader("Authorization", "Bearer " + token);
        response.getWriter().write(new JSONObject()
                .put("user_url", new StringBuffer(uriInfo).append("/users/").append(user.get().getId()))
//                .put("order_stats_url", new StringBuffer(uriInfo).append("users/").append(user.get().getId()).append("/order-stats"))
//                .put("verification_url", new StringBuffer(""))
                .toString());
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException {
        if (hasEntity) return;

        final JSONObject jsonObject = ErrorHttpServletResponseDto.produceErrorDto(ErrorCode.INVALID_PASSWORD.getCode(), ErrorCode.INVALID_PASSWORD.getMessage(), null);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write(jsonObject.toString());
        response.getWriter().flush();
    }


}
