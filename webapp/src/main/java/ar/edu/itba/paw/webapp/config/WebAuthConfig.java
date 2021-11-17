package ar.edu.itba.paw.webapp.config;

import ar.edu.itba.paw.webapp.auth.PawUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

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
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }

    @Override
    protected void configure(final HttpSecurity http) throws Exception {
        String security_key = readKeyFromFile();

        http.sessionManagement()
                .invalidSessionUrl("/credentials/login")
                .and().authorizeRequests()
                .antMatchers("/credentials/*").anonymous()
                .antMatchers("/question/ask/*").hasAuthority("USER")
                .antMatchers("/question/{id}/vote").hasAuthority("USER")
                .antMatchers("/question/answer/{id}/vote").hasAuthority("USER")
                .antMatchers("/question/*/answer").hasAuthority("USER")
                .antMatchers("/community/create").hasAuthority("USER")
                .antMatchers("/dashboard/community/{communityId}/view/*").hasAuthority("MODERATOR")
                .antMatchers("/dashboard/**").hasAuthority("USER")
                .and().formLogin()
                .usernameParameter("email")
                .passwordParameter("password")
                .defaultSuccessUrl("/", false)
                .loginPage("/credentials/login")
                .and().rememberMe()
                .rememberMeParameter("rememberme")
                .userDetailsService(userDetailsService)
                .key(security_key) // no hacer esto, crear una aleatoria segura suficientemente grande y colocarla bajo src/main/resources
                                .tokenValiditySeconds((int) TimeUnit.DAYS.
                                        toSeconds(30))
                                .and().logout()
                                .logoutUrl("/credentials/logout")
                                .logoutSuccessUrl("/credentials/login")
                                .and().exceptionHandling()
                                .accessDeniedPage("/403")
                                .and().csrf().disable();
    }
    @Override
    public void configure(final WebSecurity web) throws Exception {
        web.ignoring()
                .antMatchers("/styles/**", "/js/**", "/images/**");
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
}

