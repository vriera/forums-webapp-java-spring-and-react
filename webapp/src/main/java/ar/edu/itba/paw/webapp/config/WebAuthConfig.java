package ar.edu.itba.paw.webapp.config;

import ar.edu.itba.paw.webapp.auth.*;
import ar.edu.itba.paw.webapp.exceptions.SimpleAccessDeniedHandler;
import ar.edu.itba.paw.webapp.exceptions.SimpleAuthenticationEntryPoint;
import ch.qos.logback.core.boolex.Matcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.vote.AuthenticatedVoter;
import org.springframework.security.access.vote.RoleVoter;
import org.springframework.security.access.vote.UnanimousBased;
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
import org.springframework.security.web.access.expression.WebExpressionVoter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.NoSuchFileException;
import java.util.Arrays;
import java.util.List;

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
    public AccessDecisionManager accessDecisionManager() {
        List<AccessDecisionVoter<? extends Object>> decisionVoters
                = Arrays.asList(
                new WebExpressionVoter(), //hasRole, hasAuthority
                new RoleVoter(), // rol específico para otorgar o denegar el acceso.
                new AuthenticatedVoter()); //si el usuario está autenticado
        return new UnanimousBased(decisionVoters);
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
                .and()
                .authorizeRequests()
                .accessDecisionManager(accessDecisionManager())
                //Questions
                .antMatchers("/api/questions/{id:\\d+}/votes/users/{idUser:\\d+}/**").access("@accessControl.checkUserCanAccessToQuestion(authentication,#idUser, #id)")
                .antMatchers("/api/questions/{id:\\d+}/verify/**").access("@accessControl.checkCanAccessToQuestion(authentication, #id)")
                .antMatchers("/api/questions/{id:\\d+}/**").access("@accessControl.checkCanAccessToQuestion(authentication,#id)") //TODO: TESTEAR CON COMUNIDADES PUBLICAS
                .antMatchers(HttpMethod.GET,"/api/questions").permitAll()
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
                .antMatchers("/api/communities/{communityId:\\d+}/user/{userId:\\d+}**").access("@accessControl.checkUserCanAccessToCommunity(authentication,#idUser, #communityId)")
                .antMatchers(HttpMethod.GET, "/api/communities/moderated").permitAll()
                .antMatchers(HttpMethod.POST,"/api/communities/**").hasAuthority("USER")
                .antMatchers(HttpMethod.GET, "/api/communities").permitAll()

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

                .antMatchers(HttpMethod.PUT,"/api/**").hasAuthority("USER")
                .antMatchers(HttpMethod.DELETE,"/api/**").hasAuthority("USER")
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