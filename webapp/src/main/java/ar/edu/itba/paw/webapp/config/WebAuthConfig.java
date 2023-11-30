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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.NoSuchFileException;

@Configuration
@EnableWebSecurity
@ComponentScan("ar.edu.itba.paw.webapp.auth")
public class WebAuthConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private PawUserDetailsService userDetailsService;

    @Autowired
    private AccessControl accessControl;


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
    public ExceptionTranslationFilter exceptionTranslationFilter() { return new ExceptionTranslationFilter(authenticationEntryPoint());}

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
                        .antMatchers("/api/login").anonymous() //TODO: delete this


                        //Questions

                        .antMatchers(HttpMethod.GET ,"/api/questions/{questionId:\\d+}/votes/users/{userId:\\d+}/**").access("@questionAccessControl.canAccess(#questionId)")

                        .antMatchers(HttpMethod.GET , "/api/questions/{questionId:\\d+}").access("@questionAccessControl.canAccess(#questionId)")


                        .antMatchers("/api/questions/{id:\\d+}/votes/users/{userId:\\d+}/**").access("@questionAccessControl.canAccess(#userId, #id)")

                        .antMatchers("/api/questions/{id:\\d+}/**").access("@questionAccessControl.canAccess(#id)")
                        .antMatchers(HttpMethod.POST,"/api/questions").access("@accessControl.checkUserOrPublicParam(request)")
                        .antMatchers(HttpMethod.POST,"/api/questions/**").hasAuthority("USER")

                        //Answers
                        .antMatchers(HttpMethod.GET, "/api/answers/{id:\\d+}/votes/users/{userId:\\d+}/**").access("@answerAccessControl.canAccess(#id)")

                        .antMatchers("/api/answers/{id:\\d+}/votes/users/{userId:\\d+}/**").access("@answerAccessControl.canAccess(#userId,#id)")
                        .antMatchers("/api/answers/{id:\\d+}/verification/**").access("@answerAccessControl.canVerify(#id)")
                        .antMatchers(HttpMethod.GET,"/api/answers/{id:\\d+}/**").access("@answerAccessControl.canAccess(#id)")
                        .antMatchers(HttpMethod.POST,"/api/answers/{id:\\d+}/**").access("@questionAccessControl.canAccess(#id) and hasAuthority('USER')")
                        .antMatchers("/api/answers/owner/**").access("@accessControl.checkUserParam(request)")
                        .antMatchers("/api/answers/top/**").access("@accessControl.checkUserParam(request)")
                        .antMatchers(HttpMethod.POST ,"/api/answers").access("@answerAccessControl.canAsk(request)")
                        .antMatchers("/api/answers/").access("@answerAccessControl.canAccess(request)")


                        //Community
                        //TODO: POR AHI LO QUIERE ACCEDER UN MODERATOR!
                        .antMatchers(HttpMethod.GET,"/api/communities/{communityId:\\d+}/user/{userId:\\d+}").access("@accessControl.checkUserEqual(#userId)")
                       //TODO: RESTRINGIR AL PUT?? , EL GET DEBERIA SER PERMIT ALL??
                        .antMatchers("/api/communities/{communityId:\\d+}/user/{userId:\\d+}").permitAll()

                      //.access("@accessControl.checkUserCanAccessToCommunity(authentication,#idUser, #communityId)")
                        .antMatchers(HttpMethod.GET, "/api/communities/moderated").permitAll()

                        .antMatchers(HttpMethod.GET, "/api/communities").permitAll()
                        .antMatchers(HttpMethod.GET, "/api/communities/{communityId:\\d+}").permitAll()
                        .antMatchers(HttpMethod.GET, "/api/communities/askable").access(" @accessControl.checkUserOrPublicParam(request)")
                        .antMatchers(HttpMethod.GET, "/api/communities/*").access(" @accessControl.checkUserSameAsParam(request) and hasAuthority('USER')")
                        .antMatchers(HttpMethod.POST,"/api/communities/**").hasAuthority("USER")
                //Notifications
                        .antMatchers("/api/notifications/{userId:\\d+}**").access("@accessControl.checkUserEqual( #userId)")
                        .antMatchers("/api/notifications/communities/{communityId:\\d+}**").access("@accessControl.checkUserModerator( #communityId)")



                        //users -> pasarlo todo a uno con /**

                        .antMatchers(HttpMethod.GET, "/api/users").permitAll()
                        .antMatchers(HttpMethod.POST, "/api/users").permitAll()
                        .antMatchers(HttpMethod.GET, "/api/users/{id:\\d+}**").permitAll()
                        .antMatchers(HttpMethod.PUT,"/api/users/{id:\\d+}**").access("@accessControl.checkUserEqual(#id)")

                        //son metodos con community
                        .antMatchers("/api/users/**").access("@communityAccessControl.canModerate(request)")
//                        .antMatchers("/api/users/admitted/**").access("@communityAccessControl.canModerate(request)")
//                        .antMatchers("/api/users/requested/**").access("@communityAccessControl.canModerate(request)")
//                        .antMatchers("/api/users/request-rejected/**").access("@communityAccessControl.canModerate(request)")
//                        .antMatchers("/api/users/invited/**").access("@communityAccessControl.canModerate(request)")
//                        .antMatchers("/api/users/invite-rejected/**").access("@communityAccessControl.canModerate(request)")
//                        .antMatchers("/api/users/left/**").access("@communityAccessControl.canModerate(request)")
//                        .antMatchers("/api/users/blocked/**").access("@communityAccessControl.canModerate(request)")
//                        .antMatchers("/api/users/kicked/**").access("@communityAccessControl.canModerate(request)")
//                        .antMatchers("/api/users/banned/**").access("@communityAccessControl.canModerate(request)")


                        .antMatchers(HttpMethod.PUT,"/api/**").hasAuthority("USER")
                        .antMatchers(HttpMethod.DELETE,"/api/**").hasAuthority("USER")
                        .antMatchers("/api/**").permitAll()
                    .and()
                    .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    .and().csrf().disable();


                http.addFilterBefore(jwtFilter(), UsernamePasswordAuthenticationFilter.class);
                http.addFilterBefore(cachedBodyFilter(), JwtAuthorizationFilter.class);
                http.addFilterBefore(new CustomSecurityExceptionFilter(), CachedBodyFilter.class);

                http.headers().cacheControl().disable();
                http.exceptionHandling().authenticationEntryPoint(authenticationEntryPoint()).accessDeniedHandler(accessDeniedHandler());

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

//
//    @Bean
//    public ExceptionHandlerExceptionResolver exceptionHandlerExceptionResolver() {
//        ExceptionHandlerExceptionResolver resolver = new ExceptionHandlerExceptionResolver();
//        resolver.setOrder(Ordered.HIGHEST_PRECEDENCE);
//        resolver.setExceptionMappings(exceptionMappings());
//        return resolver;
//    }
//
//    // Define the exception mappings for your mappers
//    private Properties exceptionMappings() {
//        Properties mappings = new Properties();
//        mappings.setProperty(NoSuchElementException.class.getName(), "noSuchElementExceptionMapper");
//        // Add other exception mappings if needed
//        return mappings;
//    }
//
//    // Create your NoSuchElementExceptionMapper as a Spring bean
//    @Bean(name = "noSuchElementExceptionMapper")
//    public ExceptionMapper<NoSuchElementException> noSuchElementExceptionMapper() {
//        return new NoSuchElementExceptionMapper();
//    }
}

