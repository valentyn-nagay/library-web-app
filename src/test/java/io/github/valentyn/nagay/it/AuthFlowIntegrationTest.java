package io.github.valentyn.nagay.it;

import io.github.valentyn.nagay.LibraryApplication;
import io.github.valentyn.nagay.repository.LibraryUserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = LibraryApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class AuthFlowIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private LibraryUserRepository userRepository;

    @Test
    void registrationFormLoads() throws Exception {
        mockMvc.perform(get("/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/register"))
                .andExpect(model().attributeExists("user"));
    }

    @Test
    void userCanRegisterSuccessfully() throws Exception {
        long countBefore = userRepository.count();

        mockMvc.perform(post("/register")
                        .param("fullName", "Integration User")
                        .param("email", "integration@test.com")
                        .param("username", "integrationuser")
                        .param("password", "password")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?registered"));

        assertThat(userRepository.count()).isEqualTo(countBefore + 1);
        assertThat(userRepository.findByUsername("integrationuser")).isPresent();
    }
}
