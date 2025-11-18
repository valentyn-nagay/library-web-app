package io.github.valentyn.nagay.controller;

import io.github.valentyn.nagay.model.Book;
import io.github.valentyn.nagay.model.Loan;
import io.github.valentyn.nagay.repository.BookRepository;
import io.github.valentyn.nagay.repository.LoanRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {

    private final BookRepository bookRepository;
    private final LoanRepository loanRepository;

    public HomeController(BookRepository bookRepository, LoanRepository loanRepository) {
        this.bookRepository = bookRepository;
        this.loanRepository = loanRepository;
    }

    @GetMapping({"/", "/dashboard"})
    public String dashboard(Model model) {
        long booksCount = bookRepository.count();

        List<Book> books = bookRepository.findAll();
        long totalAvailableCopies = books.stream()
                .mapToLong(b -> b.getAvailableCopies() == null ? 0 : b.getAvailableCopies())
                .sum();

        List<Loan> loans = loanRepository.findAll();
        long loansCount = loans.size();
        long activeLoansCount = loans.stream()
                .filter(l -> l.getReturnedAt() == null)
                .count();

        model.addAttribute("booksCount", booksCount);
        model.addAttribute("totalAvailableCopies", totalAvailableCopies);
        model.addAttribute("loansCount", loansCount);
        model.addAttribute("activeLoansCount", activeLoansCount);

        return "dashboard";
    }
}
