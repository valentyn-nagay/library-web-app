
package io.github.valentyn.nagay.controller;

import io.github.valentyn.nagay.model.LibraryUser;
import io.github.valentyn.nagay.repository.LibraryUserRepository;
import io.github.valentyn.nagay.web.RegistrationForm;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class AuthControllerTest {

    @Test
    void login_ShouldReturnLoginView() {
        AuthController controller = new AuthController(null, null);

        String view = controller.login();

        assertThat(view).isEqualTo("auth/login");
    }

    @Test
    void showRegistrationForm_ShouldAddEmptyFormAndReturnView() {
        AuthController controller = new AuthController(null, null);
        Model model = new ExtendedModelMap();

        String view = controller.showRegistrationForm(model);

        assertThat(view).isEqualTo("auth/register");
        assertThat(model.getAttribute("user")).isInstanceOf(RegistrationForm.class);
    }

    @Test
    void register_WhenUsernameAlreadyExists_ShouldReturnRegisterWithError() {
        LibraryUserRepository repo = mock(LibraryUserRepository.class);
        PasswordEncoder encoder = mock(PasswordEncoder.class);
        AuthController controller = new AuthController(repo, encoder);

        RegistrationForm form = new RegistrationForm();
        form.setUsername("user");

        when(repo.findByUsername("user")).thenReturn(Optional.of(new LibraryUser()));

        Model model = new ExtendedModelMap();

        String view = controller.register(form, model);

        assertThat(view).isEqualTo("auth/register");
        assertThat(model.getAttribute("error")).isNotNull();
        verify(repo, never()).save(any());
    }

    @Test
    void register_WhenUsernameFree_ShouldSaveUserAndRedirectToLogin() {
        LibraryUserRepository repo = mock(LibraryUserRepository.class);
        PasswordEncoder encoder = mock(PasswordEncoder.class);
        when(encoder.encode("pwd")).thenReturn("encoded");

        AuthController controller = new AuthController(repo, encoder);

        RegistrationForm form = new RegistrationForm();
        form.setFullName("User Name");
        form.setEmail("user@test.com");
        form.setUsername("user");
        form.setPassword("pwd");

        when(repo.findByUsername("user")).thenReturn(Optional.empty());

        Model model = new ExtendedModelMap();

        String view = controller.register(form, model);

        assertThat(view).isEqualTo("redirect:/login?registered");
        verify(repo).save(argThat(u ->
                "User Name".equals(u.getFullName())
                        && "user@test.com".equals(u.getEmail())
                        && "user".equals(u.getUsername())
                        && "encoded".equals(u.getPassword())
                        && "USER".equals(u.getRole())
        ));
    }
}
