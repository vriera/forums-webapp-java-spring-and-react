package ar.edu.itba.paw.webapp.auth;

import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

@Component
public class PawAuthenticationProvider implements AuthenticationProvider {

	@Autowired
	PasswordEncoder encoder;

	@Autowired
	UserService userService;

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		String email = authentication.getPrincipal().toString();
		String password = authentication.getCredentials().toString();

		User user = userService.findByEmail(email).orElseThrow(() -> new BadCredentialsException("No hay usuarios con email "+email));

		if(!encoder.matches(password, user.getPassword()))
			throw new BadCredentialsException("Email o contraseña incorrectos");

		final Collection<GrantedAuthority> roles = Collections.singletonList(new SimpleGrantedAuthority("USER"));
		return new UsernamePasswordAuthenticationToken(email, password, roles);
	}

	@Override
	public boolean supports(Class<?> aClass) {
		return aClass == UsernamePasswordAuthenticationToken.class;
	}
}
