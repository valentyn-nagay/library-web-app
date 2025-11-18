package io.github.valentyn.nagay.service;

import io.github.valentyn.nagay.model.LibraryUser;
import io.github.valentyn.nagay.repository.LibraryUserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private LibraryUserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService service;

    @Test
    void loadUserByUsername_WhenUserExists_ShouldBuildSpringUser() {
        LibraryUser user = new LibraryUser();
        user.setUsername("test");
        user.setPassword("encoded");
        user.setRole("ADMIN");

        when(userRepository.findByUsername("test")).thenReturn(Optional.of(user));

        UserDetails details = service.loadUserByUsername("test");

        assertThat(details.getUsername()).isEqualTo("test");
        assertThat(details.getPassword()).isEqualTo("encoded");
        assertThat(details.getAuthorities())
                .extracting("authority")
                .containsExactly("ROLE_ADMIN");
    }

    @Test
    void loadUserByUsername_WhenUserNotFound_ShouldThrow() {
        when(userRepository.findByUsername("missing")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,
                () -> service.loadUserByUsername("missing"));
    }
}
