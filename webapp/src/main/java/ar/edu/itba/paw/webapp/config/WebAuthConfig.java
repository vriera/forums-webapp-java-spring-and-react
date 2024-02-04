package ar.edu.itba.paw.webapp.config;

import ar.edu.itba.paw.webapp.auth.AccessControl;
import ar.edu.itba.paw.webapp.auth.JwtAuthorizationFilter;
import ar.edu.itba.paw.webapp.auth.PawUserDetailsService;
import ar.edu.itba.paw.webapp.exceptions.SimpleAccessDeniedHandler;
import ar.edu.itba.paw.webapp.exceptions.SimpleAuthenticationEntryPoint;
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

    @Autowired
    private JwtAuthorizationFilter jwtAuthorizationFilter;


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
        //String securityKey = readKeyFromFile();

        http.sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                //.accessDecisionManager(accessDecisionManager())
                //Questions
                .antMatchers("/api/questions/{id:\\d+}/votes/**").access("@accessControl.checkUserCanAccessToQuestion(authentication, #id)")
                .antMatchers("/api/questions/{id:\\d+}/**").access("@accessControl.checkCanAccessToQuestion(authentication,#id)") //TODO: TESTEAR CON COMUNIDADES PUBLICAS
                .antMatchers(HttpMethod.GET, "/api/questions").access("@accessControl.checkCanGetQuestions(authentication, request)")
                .antMatchers(HttpMethod.POST, "/api/questions/**").hasAuthority("USER")

                //Answers
                .antMatchers("/api/answers/{id:\\d+}/votes/**").access("@accessControl.checkUserCanAccessToAnswer(authentication, #id)")
                .antMatchers("/api/answers/{id:\\d+}/verification/**").access("@accessControl.checkAnswerQuestionOwner(authentication, #id)")
                .antMatchers(HttpMethod.GET, "/api/answers/{id:\\d+}/**").access("@accessControl.checkCanAccessToAnswer(authentication, #id)")
                .antMatchers(HttpMethod.GET, "/api/answers").access("@accessControl.checkCanGetAnswers(authentication, request)")
                .antMatchers(HttpMethod.POST, "/api/answers/").hasAuthority("USER")


                //Community
                .antMatchers("/api/communities/{communityId:\\d+}/user/{idUser:\\d+}**").access("@accessControl.checkUserCanAccessToCommunity(authentication,#idUser, #communityId)")
                .antMatchers(HttpMethod.GET, "/api/communities/moderated").hasAuthority("USER")
                .antMatchers(HttpMethod.GET, "/api/communities").access("@accessControl.checkCanGetCommunities(authentication, request)")
                .antMatchers(HttpMethod.POST, "/api/communities").hasAuthority("USER")
                .antMatchers(HttpMethod.PUT, "/api/communities/{communityId}/access/{userId}").access("@accessControl.canChangeAccess(authentication,request,#userId,#communityId)")

                //Notifications
                .antMatchers("/api/notifications/{userId:\\d+}**").access("hasAuthority('USER') and @accessControl.checkUser(#userId)")
                .antMatchers("/api/notifications/communities/{communityId:\\d+}**").access("@accessControl.checkUserModerator(authentication, #communityId)")


                //users
                .antMatchers(HttpMethod.PUT, "/api/users/{id:\\d+}**").access("@accessControl.checkUser(#id)")
                .antMatchers(HttpMethod.PUT, "/api/**").hasAuthority("USER")
                .antMatchers(HttpMethod.DELETE, "/api/**").hasAuthority("USER")
                .antMatchers("/api/**").permitAll()
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and().csrf().disable();


        // jwt FILTER AND BASIC
        http.addFilterBefore(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class);


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
        for (String line; (line = reader.readLine()) != null; ) {
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