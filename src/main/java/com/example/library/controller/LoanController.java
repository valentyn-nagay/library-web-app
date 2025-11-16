package com.example.library.controller;

import com.example.library.repository.BookRepository;
import com.example.library.repository.LibraryUserRepository;
import com.example.library.service.LoanService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/loans")
public class LoanController {

    private final LoanService loanService;
    private final BookRepository bookRepository;
    private final LibraryUserRepository userRepository;

    public LoanController(LoanService loanService,
                          BookRepository bookRepository,
                          LibraryUserRepository userRepository) {
        this.loanService = loanService;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("loans", loanService.findAll());
        return "loans/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("books", bookRepository.findAll());
        model.addAttribute("users", userRepository.findAll());
        return "loans/form";
    }

    @PostMapping
    public String issue(@RequestParam Long bookId,
                        @RequestParam Long userId,
                        @RequestParam(defaultValue = "14") int days) {
        loanService.issueBook(bookId, userId, days);
        return "redirect:/loans";
    }

    @PostMapping("/{id}/return")
    public String returnBook(@PathVariable Long id) {
        loanService.returnBook(id);
        return "redirect:/loans";
    }
}
