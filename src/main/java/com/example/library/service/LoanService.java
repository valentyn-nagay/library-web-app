package com.example.library.service;

import com.example.library.model.Book;
import com.example.library.model.LibraryUser;
import com.example.library.model.Loan;
import com.example.library.repository.BookRepository;
import com.example.library.repository.LibraryUserRepository;
import com.example.library.repository.LoanRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class LoanService {

    private final LoanRepository loanRepository;
    private final BookRepository bookRepository;
    private final LibraryUserRepository userRepository;

    public LoanService(LoanRepository loanRepository,
                       BookRepository bookRepository,
                       LibraryUserRepository userRepository) {
        this.loanRepository = loanRepository;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
    }

    public List<Loan> findAll() {
        return loanRepository.findAll();
    }

    @Transactional
    public Loan issueBook(Long bookId, Long userId, int days) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("Book not found: " + bookId));
        if (book.getAvailableCopies() == null || book.getAvailableCopies() <= 0) {
            throw new IllegalStateException("No available copies for book: " + book.getTitle());
        }

        LibraryUser user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        Loan loan = new Loan();
        loan.setBook(book);
        loan.setUser(user);
        loan.setStartDate(LocalDate.now());
        loan.setDueDate(LocalDate.now().plusDays(days));

        book.setAvailableCopies(book.getAvailableCopies() - 1);
        bookRepository.save(book);

        return loanRepository.save(loan);
    }

    @Transactional
    public void returnBook(Long loanId) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new IllegalArgumentException("Loan not found: " + loanId));

        if (!loan.isReturned()) {
            loan.setReturnedAt(LocalDate.now());
            Book book = loan.getBook();
            book.setAvailableCopies(book.getAvailableCopies() + 1);
            bookRepository.save(book);
            loanRepository.save(loan);
        }
    }
}
