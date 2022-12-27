package ar.edu.itba.paw.webapp.config;

import ar.edu.itba.paw.webapp.auth.JwtAuthorizationFilter;
import ar.edu.itba.paw.webapp.auth.LoginAuthorizationFilter;
import ar.edu.itba.paw.webapp.auth.PawUserDetailsService;
import ar.edu.itba.paw.webapp.exceptions.SimpleAccessDeniedHandler;
import ar.edu.itba.paw.webapp.exceptions.SimpleAuthenticationEntryPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.ws.rs.HttpMethod;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.NoSuchFileException;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableWebSecurity
@ComponentScan("ar.edu.itba.paw.webapp.auth")
public class WebAuthConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private PawUserDetailsService userDetailsService;

    @Bean
    public LoginAuthorizationFilter loginFilter() throws Exception {
        final LoginAuthorizationFilter loginFilter = new LoginAuthorizationFilter();
        loginFilter.setAuthenticationManager(authenticationManagerBean());
        loginFilter.setRequiresAuthenticationRequestMatcher(new AntPathRequestMatcher("/api/login", HttpMethod.POST.toString()));
        return loginFilter;
    }

    @Bean
    public JwtAuthorizationFilter jwtFilter() {
        return new JwtAuthorizationFilter();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return new SimpleAccessDeniedHandler();
    }

    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return new SimpleAuthenticationEntryPoint();
    }
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }

    @Override
    protected void configure(final HttpSecurity http) throws Exception {
        String security_key = readKeyFromFile();

        http.sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    .invalidSessionUrl("/api/login")
                .and()
                    .authorizeRequests()
                        //.antMatchers("/api/login").anonymous()
                        .antMatchers("/api/questions/{id}/votes/users/{idUser}").hasRole("USER")
                        .antMatchers("/api/questions/{id}/verify/").hasRole("USER")
                        .antMatchers("/api/answers/{id}/verify/").hasRole("USER")
                        .antMatchers("/api/answers/{id}/votes/users/{idUser}").hasRole("USER")
                        .antMatchers(HttpMethod.POST,"/api/**").hasRole("USER")
                        .antMatchers(HttpMethod.PUT,"/api/**").hasRole("USER")
                        .antMatchers(HttpMethod.DELETE,"/api/**").hasRole("USER") //TODO: SE PUEDEN HACER MENSAJES DE ERROR CUSTOMS?
                        .antMatchers("/api/community/create").hasRole("USER")
                        .antMatchers("/api/dashboard/community/{communityId}/view/*").hasRole("MODERATOR")
                        .antMatchers("/api/dashboard/**").hasRole("USER")
                        .antMatchers("/api/**").permitAll()
                        .and()
                        .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                    .csrf()
                    .disable();


                http.addFilterBefore(jwtFilter(), UsernamePasswordAuthenticationFilter.class);
                http.addFilterBefore(loginFilter(), JwtAuthorizationFilter.class);
                http.headers().cacheControl().disable();
                http.exceptionHandling().authenticationEntryPoint(authenticationEntryPoint());

    }
    @Override
    public void configure(final WebSecurity web) throws Exception {
        web.ignoring()
                .antMatchers("/styles/**", "/js/**", "/images/** , /static/** , /resources/**");
    }

    private String readKeyFromFile() throws IOException {
        StringBuilder builder = new StringBuilder();
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        InputStream is = classloader.getResourceAsStream("security_key.txt");
        if (is == null)
            throw new NoSuchFileException("Cannot open input stream on security key");
        InputStreamReader streamReader = new InputStreamReader(is, StandardCharsets.UTF_8);
        BufferedReader reader = new BufferedReader(streamReader);
        for (String line; (line = reader.readLine()) != null;) {
            builder.append(line);
        }
        return builder.toString();
    }
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}

