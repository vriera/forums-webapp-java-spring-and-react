package ar.edu.itba.paw.webapp.config;

import ar.edu.itba.paw.webapp.auth.AccessControl;
import ar.edu.itba.paw.webapp.auth.JwtAuthorizationFilter;
import ar.edu.itba.paw.webapp.auth.LoginAuthorizationFilter;
import ar.edu.itba.paw.webapp.auth.PawUserDetailsService;
import ar.edu.itba.paw.webapp.exceptions.SimpleAccessDeniedHandler;
import ar.edu.itba.paw.webapp.exceptions.SimpleAuthenticationEntryPoint;
import ch.qos.logback.core.boolex.Matcher;
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
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

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

    //TODO vuela
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
                        .antMatchers("/api/login").anonymous() //TODO: delete this

                        //Questions
                        .antMatchers("/api/questions/{id:\\d+}/votes/users/{idUser:\\d+}/**").access("@accessControl.checkUserCanAccessToQuestion(authentication,#idUser, #id)")
                        .antMatchers("/api/questions/{id:\\d+}/verify/**").access("@accessControl.checkCanAccessToQuestion(authentication, #id)")
                        .antMatchers("/api/questions/{id:\\d+}/**").access("@accessControl.checkCanAccessToQuestion(authentication,#id)") //TODO: TESTEAR CON COMUNIDADES PUBLICAS
                        .antMatchers(HttpMethod.POST,"/api/questions/**").hasAuthority("USER")

                        //Answers
                        .antMatchers("/api/answers/{id:\\d+}/votes/users/{idUser:\\d+}/**").access("@accessControl.checkUserCanAccessToQuestion(authentication,#idUser, #id)")
                        .antMatchers("/api/answers/{id:\\d+}/verify/**").access("@accessControl.checkCanAccessToQuestion(authentication, #id)")
                        .antMatchers(HttpMethod.GET,"/api/answers/{id:\\d+}/**").access("@accessControl.checkCanAccessToAnswer(authentication, #id)")
                        .antMatchers(HttpMethod.POST,"/api/answers/{id:\\d+}/**").access("@accessControl.checkCanAccessToQuestion(authentication, #id)")
                        .antMatchers("/api/answers/owner/**").access("@accessControl.checkUserParam(request)")
                        .antMatchers("/api/answers/top/**").access("@accessControl.checkUserParam(request)")
                        .antMatchers("/api/answers/").access("@accessControl.checkCanAccessToQuestion(request)")


                        //Community
                        .antMatchers("/api/community/{communityId:\\d+}/user/{userId:\\d+}**").access("@accessControl.checkUserCanAccessToCommunity(authentication,#idUser, #communityId)")
                        .antMatchers(HttpMethod.POST,"/api/community/**").hasAuthority("USER")
                        .antMatchers("/api/community/create").hasAuthority("USER")

                        //Notifications
                        .antMatchers("/api/notifications/{userId:\\d+}**").access("@accessControl.checkUser( #userId)") //"clase
                        .antMatchers("/api/notifications/communities/{communityId:\\d+}**").access("@accessControl.checkUserModerator(authentication, #communityId)")



                        //users
                        .antMatchers("/api/users/admitted/**").access("@accessControl.checkUserModeratorParam(request)")
                        .antMatchers("/api/users/requested/**").access("@accessControl.checkUserModeratorParam(request)")
                        .antMatchers("/api/users/request-rejected/**").access("@accessControl.checkUserModeratorParam(request)")
                        .antMatchers("/api/users/invited/**").access("@accessControl.checkUserModeratorParam(request)")
                        .antMatchers("/api/users/invite-rejected/**").access("@accessControl.checkUserModeratorParam(request)")
                        .antMatchers("/api/users/left/**").access("@accessControl.checkUserModeratorParam(request)")
                        .antMatchers("/api/users/blocked/**").access("@accessControl.checkUserModeratorParam(request)")
                        .antMatchers("/api/users/kicked/**").access("@accessControl.checkUserModeratorParam(request)")
                        .antMatchers("/api/users/banned/**").access("@accessControl.checkUserModeratorParam(request)")

                        .antMatchers(HttpMethod.PUT,"/api/users/{id:\\d+}**").access("@accessControl.checkUser(#id)")


                        .antMatchers("/api/dashboard/community/{communityId}/view/*").hasAuthority("MODERATOR")//TODO: DELETE DASHBOARD URL
                        .antMatchers("/api/dashboard/**").hasAuthority("USER") //TODO: DELETE DASHBOARD URL


                        .antMatchers(HttpMethod.PUT,"/api/**").hasAuthority("USER")
                        .antMatchers(HttpMethod.DELETE,"/api/**").hasAuthority("USER")
                        .antMatchers("/api/**").permitAll()
                    .and()
                    .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    .and().csrf().disable();



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

