package io.github.valentyn.nagay.controller;

import io.github.valentyn.nagay.model.Book;
import io.github.valentyn.nagay.model.LibraryUser;
import io.github.valentyn.nagay.model.Loan;
import io.github.valentyn.nagay.repository.BookRepository;
import io.github.valentyn.nagay.repository.LibraryUserRepository;
import io.github.valentyn.nagay.service.LoanService;
import org.junit.jupiter.api.Test;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class LoanControllerTest {

    @Test
    void list_ShouldPutLoansAndReturnListView() {
        LoanService loanService = mock(LoanService.class);
        BookRepository bookRepo = mock(BookRepository.class);
        LibraryUserRepository userRepo = mock(LibraryUserRepository.class);

        List<Loan> loans = Arrays.asList(new Loan(), new Loan());
        when(loanService.findAll()).thenReturn(loans);

        LoanController controller = new LoanController(loanService, bookRepo, userRepo);
        Model model = new ExtendedModelMap();

        String view = controller.list(model);

        assertThat(view).isEqualTo("loans/list");
        assertThat(model.getAttribute("loans")).isSameAs(loans);
    }

    @Test
    void createForm_ShouldPutBooksAndUsersAndReturnFormView() {
        LoanService loanService = mock(LoanService.class);
        BookRepository bookRepo = mock(BookRepository.class);
        LibraryUserRepository userRepo = mock(LibraryUserRepository.class);

        List<Book> books = Arrays.asList(new Book(), new Book());
        List<LibraryUser> users = Arrays.asList(new LibraryUser(), new LibraryUser());

        when(bookRepo.findAll()).thenReturn(books);
        when(userRepo.findAll()).thenReturn(users);

        LoanController controller = new LoanController(loanService, bookRepo, userRepo);
        Model model = new ExtendedModelMap();

        String view = controller.createForm(model);

        assertThat(view).isEqualTo("loans/form");
        assertThat(model.getAttribute("books")).isSameAs(books);
        assertThat(model.getAttribute("users")).isSameAs(users);
    }

    @Test
    void issue_ShouldCallServiceAndRedirect() {
        LoanService loanService = mock(LoanService.class);
        BookRepository bookRepo = mock(BookRepository.class);
        LibraryUserRepository userRepo = mock(LibraryUserRepository.class);

        LoanController controller = new LoanController(loanService, bookRepo, userRepo);

        String view = controller.issue(1L, 2L, 14);

        assertThat(view).isEqualTo("redirect:/loans");
        verify(loanService).issueBook(1L, 2L, 14);
    }

    @Test
    void returnBook_ShouldCallServiceAndRedirect() {
        LoanService loanService = mock(LoanService.class);
        BookRepository bookRepo = mock(BookRepository.class);
        LibraryUserRepository userRepo = mock(LibraryUserRepository.class);

        LoanController controller = new LoanController(loanService, bookRepo, userRepo);

        String view = controller.returnBook(5L);

        assertThat(view).isEqualTo("redirect:/loans");
        verify(loanService).returnBook(5L);
    }
}
