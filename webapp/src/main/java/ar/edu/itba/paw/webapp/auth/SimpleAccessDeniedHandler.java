package ar.edu.itba.paw.webapp.auth;



import org.json.JSONObject;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author dax
 * @since 2019/11/6 22:19
 * @link https://www.toptal.com/java/rest-security-with-jwt-spring-security-and-java
 */
public class SimpleAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        JSONObject json = new JSONObject();
        json.put("code" , "User.without.permission");
        response.getWriter().write(json.toString());
        response.getWriter().flush();
    }
}
