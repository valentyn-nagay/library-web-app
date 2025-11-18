package io.github.valentyn.nagay.service;

import io.github.valentyn.nagay.model.LibraryUser;
import io.github.valentyn.nagay.repository.LibraryUserRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final LibraryUserRepository userRepository;

    public CustomUserDetailsService(LibraryUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        LibraryUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        String role = user.getRole() != null ? user.getRole() : "USER";

        return User.withUsername(user.getUsername())
                .password(user.getPassword())
                .roles(role)
                .build();
    }
}
