package io.github.valentyn.nagay.repository;

import io.github.valentyn.nagay.model.Book;
import io.github.valentyn.nagay.model.LibraryUser;
import io.github.valentyn.nagay.model.Loan;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class LoanRepositoryTest {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private LibraryUserRepository userRepository;

    @Autowired
    private LoanRepository loanRepository;

    @Test
    void saveAndFindLoan() {
        Book book = new Book();
        book.setTitle("Loan Book");
        book.setAuthor("Author");
        book.setIsbn("0987654321");
        book.setTotalCopies(3);
        book.setAvailableCopies(3);
        book = bookRepository.save(book);

        LibraryUser user = new LibraryUser();
        user.setFullName("Loan User");
        user.setEmail("loan@test.com");
        user.setUsername("loanuser");
        user.setPassword("pwd");
        user.setRole("USER");
        user = userRepository.save(user);

        Loan loan = new Loan();
        loan.setBook(book);
        loan.setUser(user);
        loan.setStartDate(LocalDate.now());
        loan.setDueDate(LocalDate.now().plusDays(7));

        loanRepository.save(loan);

        List<Loan> all = loanRepository.findAll();
        assertThat(all).isNotEmpty();
        assertThat(all.get(0).getBook().getTitle()).isEqualTo("Loan Book");
        assertThat(all.get(0).getUser().getUsername()).isEqualTo("loanuser");
    }
}
