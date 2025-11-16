package com.example.library.controller;

import com.example.library.model.LibraryUser;
import com.example.library.repository.LibraryUserRepository;
import com.example.library.web.RegistrationForm;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthController {

    private final LibraryUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(LibraryUserRepository userRepository,
                          PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new RegistrationForm());
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute("user") RegistrationForm form, Model model) {
        if (userRepository.findByUsername(form.getUsername()).isPresent()) {
            model.addAttribute("error", "Пользователь с таким логином уже существует");
            return "auth/register";
        }

        LibraryUser user = new LibraryUser();
        user.setFullName(form.getFullName());
        user.setEmail(form.getEmail());
        user.setUsername(form.getUsername());
        user.setPassword(passwordEncoder.encode(form.getPassword()));
        user.setRole("USER");

        userRepository.save(user);

        return "redirect:/login?registered";
    }
}
