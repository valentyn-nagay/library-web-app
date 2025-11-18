package io.github.valentyn.nagay.repository;

import io.github.valentyn.nagay.model.LibraryUser;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class LibraryUserRepositoryTest {

    @Autowired
    private LibraryUserRepository userRepository;

    @Test
    void findByUsername() {
        LibraryUser user = new LibraryUser();
        user.setFullName("Repo User");
        user.setEmail("repo@test.com");
        user.setUsername("repouser");
        user.setPassword("pwd");
        user.setRole("USER");

        userRepository.save(user);

        Optional<LibraryUser> found = userRepository.findByUsername("repouser");
        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("repo@test.com");
    }
}
