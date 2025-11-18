package io.github.valentyn.nagay.service;

import io.github.valentyn.nagay.model.Book;
import io.github.valentyn.nagay.model.LibraryUser;
import io.github.valentyn.nagay.model.Loan;
import io.github.valentyn.nagay.repository.BookRepository;
import io.github.valentyn.nagay.repository.LibraryUserRepository;
import io.github.valentyn.nagay.repository.LoanRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoanServiceTest {

    @Mock
    private LoanRepository loanRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private LibraryUserRepository userRepository;

    @InjectMocks
    private LoanService loanService;

    @Test
    void findAll_ShouldDelegateToRepository() {
        List<Loan> loans = Arrays.asList(new Loan(), new Loan());
        when(loanRepository.findAll()).thenReturn(loans);

        List<Loan> result = loanService.findAll();

        assertThat(result).isSameAs(loans);
        verify(loanRepository).findAll();
    }

    @Test
    void issueBook_ShouldCreateLoanAndDecreaseAvailableCopies() {
        Long bookId = 1L;
        Long userId = 2L;
        int days = 7;

        Book book = new Book();
        book.setId(bookId);
        book.setTitle("Test book");
        book.setAvailableCopies(5);

        LibraryUser user = new LibraryUser();
        user.setId(userId);
        user.setUsername("user");

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(loanRepository.save(any(Loan.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Loan result = loanService.issueBook(bookId, userId, days);

        ArgumentCaptor<Loan> captor = ArgumentCaptor.forClass(Loan.class);
        verify(loanRepository).save(captor.capture());
        Loan savedLoan = captor.getValue();

        LocalDate today = LocalDate.now();

        assertThat(savedLoan.getBook()).isSameAs(book);
        assertThat(savedLoan.getUser()).isSameAs(user);
        assertThat(savedLoan.getStartDate()).isEqualTo(today);
        assertThat(savedLoan.getDueDate()).isEqualTo(today.plusDays(days));
        assertThat(book.getAvailableCopies()).isEqualTo(4); // 5 - 1

        assertThat(result).isSameAs(savedLoan);
        verify(bookRepository).save(book);
    }

    @Test
    void issueBook_WhenBookNotFound_ShouldThrowException() {
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> loanService.issueBook(1L, 2L, 7));
    }

    @Test
    void issueBook_WhenNoAvailableCopies_ShouldThrowException() {
        Book book = new Book();
        book.setId(1L);
        book.setTitle("Test");
        book.setAvailableCopies(0);

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        assertThrows(IllegalStateException.class,
                () -> loanService.issueBook(1L, 2L, 7));
    }

    @Test
    void returnBook_WhenNotReturned_ShouldSetReturnedAtAndIncreaseCopies() {
        Long loanId = 10L;
        Book book = new Book();
        book.setAvailableCopies(3);

        Loan loan = new Loan();
        loan.setId(loanId);
        loan.setBook(book);
        loan.setReturnedAt(null);

        when(loanRepository.findById(loanId)).thenReturn(Optional.of(loan));

        loanService.returnBook(loanId);

        assertThat(loan.getReturnedAt()).isEqualTo(LocalDate.now());
        assertThat(book.getAvailableCopies()).isEqualTo(4);
        verify(bookRepository).save(book);
        verify(loanRepository).save(loan);
    }

    @Test
    void returnBook_WhenAlreadyReturned_ShouldDoNothing() {
        Long loanId = 10L;
        Book book = new Book();
        book.setAvailableCopies(3);

        Loan loan = new Loan();
        loan.setId(loanId);
        loan.setBook(book);
        loan.setReturnedAt(LocalDate.now().minusDays(1));

        when(loanRepository.findById(loanId)).thenReturn(Optional.of(loan));

        loanService.returnBook(loanId);

        assertThat(loan.getReturnedAt()).isNotNull();
        assertThat(book.getAvailableCopies()).isEqualTo(3);
        verify(bookRepository, never()).save(any());
        verify(loanRepository, never()).save(any());
    }
}
