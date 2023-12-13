package ar.edu.itba.paw.webapp.config;

import ar.edu.itba.paw.webapp.auth.accessControl.AccessControl;
import ar.edu.itba.paw.webapp.auth.JwtAuthorizationFilter;
import ar.edu.itba.paw.webapp.auth.PawUserDetailsService;
import ar.edu.itba.paw.webapp.auth.SimpleAccessDeniedHandler;
import ar.edu.itba.paw.webapp.auth.accessControl.SimpleAuthenticationEntryPoint;
import ar.edu.itba.paw.webapp.config.filters.CachedBodyFilter;
import ar.edu.itba.paw.webapp.config.filters.CustomSecurityExceptionFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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
import org.springframework.security.web.access.ExceptionTranslationFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.NoSuchFileException;
import java.util.Arrays;

@Configuration
@EnableWebSecurity
@ComponentScan("ar.edu.itba.paw.webapp.auth")
public class WebAuthConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private PawUserDetailsService userDetailsService;

    // FIXME: REMOVE THIS BEFORE DEPLOYMENT
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000/", "https://localhost:3000"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public JwtAuthorizationFilter jwtFilter() {
        return new JwtAuthorizationFilter();
    }

    @Bean
    public CachedBodyFilter cachedBodyFilter() {
        return new CachedBodyFilter();
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

    @Bean
    public ExceptionTranslationFilter exceptionTranslationFilter() {
        return new ExceptionTranslationFilter(authenticationEntryPoint());
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
                .and()
                .authorizeRequests()

                //Question Votes
                .antMatchers(HttpMethod.PUT, "/api/questions/{id:\\d+}/votes/{userId:\\d+}").access( "@questionAccessControl.canAccess(#id , #userId)")
                .antMatchers(HttpMethod.DELETE, "/api/questions/{id:\\d+}/votes/{userId:\\d+}").access( "@questionAccessControl.canAccess(#id , #userId)")
                .antMatchers(HttpMethod.GET, "/api/questions/{id:\\d+}/votes**").access("@questionAccessControl.canAccess(#id)")

//                // Questions
//
                .antMatchers(HttpMethod.GET , "/api/questions/{id:\\d+}").access("@questionAccessControl.canAccess(#id)")

                .antMatchers(HttpMethod.POST, "/api/questions").access("@questionAccessControl.canAsk(request)")
                .antMatchers(HttpMethod.GET , "/api/questions").access("@questionAccessControl.canSearch(request)")

//
                // Answers votes
                .antMatchers(HttpMethod.PUT, "/api/answers/{id:\\d+}/votes/{userId:\\d+}").access( "@answerAccessControl.canAccess(#id , #userId)")
                .antMatchers(HttpMethod.DELETE, "/api/answers/{id:\\d+}/votes/{userId:\\d+}").access( "@answerAccessControl.canAccess(#id , #userId)")
                .antMatchers(HttpMethod.GET, "/api/answers/{id:\\d+}/votes**").access("@answerAccessControl.canAccess(#id)")
                // Answers

                .antMatchers("/api/answers/{id:\\d+}/verification").access("@answerAccessControl.canVerify(#id)")
                .antMatchers(HttpMethod.GET, "/api/answers/{id:\\d+}").access("@answerAccessControl.canAccess(#id)")
                .antMatchers(HttpMethod.POST, "/api/answers").access("@answerAccessControl.canAnswer(request)")
                .antMatchers("/api/answers").access("@answerAccessControl.canSearch(request)")

                // Community
                .antMatchers(HttpMethod.GET, "/api/communities/{communityId:\\d+}/users/{userId:\\d+}").permitAll()
                .antMatchers(HttpMethod.PUT, "/api/communities/{communityId:\\d+}/users/{userId:\\d+}").access("@communityAccessControl.checkUserCanModifyAccess(#userId, #communityId, request)")
                .antMatchers(HttpMethod.GET, "/api/communities/{communityId:\\d+}/notifications").access("@communityAccessControl.canCurrentUserModerate(communityId)")
                .antMatchers(HttpMethod.GET, "/api/communities/{communityId:\\d+}").permitAll()

                .antMatchers(HttpMethod.POST, "/api/communities").hasAuthority("USER")
                .antMatchers(HttpMethod.GET, "/api/communities").permitAll()

                // Users
                .antMatchers(HttpMethod.GET, "/api/users").permitAll()
                .antMatchers(HttpMethod.POST, "/api/users").permitAll()
                .antMatchers(HttpMethod.GET, "/api/users/{id:\\d+}").permitAll()
                .antMatchers(HttpMethod.PUT, "/api/users/{id:\\d+}**").access("@accessControl.checkUserEqual(#id)")
                .antMatchers(HttpMethod.GET, "/api/users/{id:\\d+}/notifications").access("@accessControl.checkUserEqual(#id)")

                // The rest
                .antMatchers(HttpMethod.PUT, "/api/**").hasAuthority("USER")
                .antMatchers(HttpMethod.DELETE, "/api/**").hasAuthority("USER")
                .antMatchers("/api/**").permitAll()
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and().csrf().disable();

        http.addFilterBefore(jwtFilter(), UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(cachedBodyFilter(), JwtAuthorizationFilter.class);
        http.addFilterBefore(new CustomSecurityExceptionFilter(), CachedBodyFilter.class);

        http.headers().cacheControl().disable();
        http.exceptionHandling().authenticationEntryPoint(authenticationEntryPoint())
                .accessDeniedHandler(accessDeniedHandler());

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
