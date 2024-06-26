package ar.edu.itba.paw.webapp.auth;

import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collection;
import java.util.NoSuchElementException;

@Component
public class PawUserDetailsService implements UserDetailsService {
    @Autowired
    private UserService us;

    @Override
    public UserDetails loadUserByUsername(final String email)
            throws UsernameNotFoundException {
        User user;

        try {
            user = us.findByEmail(email);
        } catch (NoSuchElementException e) {
            throw new UsernameNotFoundException(e.getMessage());
        }

        final Collection<? extends GrantedAuthority> loggedUserAuthorities = Arrays
                .asList(new SimpleGrantedAuthority("USER"));

        return new org.springframework.security.core.userdetails.User(email, user.getPassword(), loggedUserAuthorities);
    }
}
