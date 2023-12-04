package ar.edu.itba.paw.webapp.config.filters;

import ar.edu.itba.paw.webapp.dto.errors.ErrorDto;
import org.json.JSONObject;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.NoSuchElementException;

public class CustomSecurityExceptionFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (NoSuchElementException e) {
            // handle NoSuchElementException
            ErrorDto successDto = ErrorDto.exceptionToErrorDto(e);
            if(e.getMessage() == null || e.getMessage().isEmpty())
                successDto.setMessage("Resource not found");
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write(convertObjectToJson(
                    successDto)
            );
        } catch(AccessDeniedException e) {
            // handle AccessDeniedException
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write(convertObjectToJson(
                    ErrorDto.exceptionToErrorDto(e))
            );
        } catch (RuntimeException e ){
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(convertObjectToJson(
                    ErrorDto.exceptionToErrorDto(e))
            );
        }
//        catch (AuthenticationException e) {
//            // handle AuthenticationException
//            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//            response.getWriter().write(convertObjectToJson(
//                    SuccessDto.exceptionToSuccessDto(e))
//            );
//        }


    }


    private String convertObjectToJson(Object object) throws IOException {
        return new JSONObject(object).toString();
    }
}