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

@Component
public class PawUserDetailsService implements UserDetailsService {
    @Autowired
    private UserService us;
    @Override
    public UserDetails loadUserByUsername(final String email)
            throws UsernameNotFoundException {

        final User user = us.findByEmail(email).
                orElseThrow( () -> new UsernameNotFoundException("No user with email" + email));

        int moderatedCommunities = us.getModeratedCommunities(user.getId(), 0).size();

        final Collection<? extends GrantedAuthority> authorities;

        if(moderatedCommunities > 0) {
            authorities = Arrays.asList(
                    new SimpleGrantedAuthority("USER"),
                    new SimpleGrantedAuthority("MODERATOR")
            );
        }
        else{
            authorities = Arrays.asList(
                    new SimpleGrantedAuthority("USER")
            );
        }

        return new org.springframework.security.core.userdetails.User(email, user.getPassword(), authorities);
    }
}

