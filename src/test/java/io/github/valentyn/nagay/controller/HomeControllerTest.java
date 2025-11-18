package io.github.valentyn.nagay.controller;

import io.github.valentyn.nagay.model.Book;
import io.github.valentyn.nagay.model.Loan;
import io.github.valentyn.nagay.repository.BookRepository;
import io.github.valentyn.nagay.repository.LoanRepository;
import org.junit.jupiter.api.Test;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class HomeControllerTest {

    @Test
    void dashboard_ShouldCalculateStatisticsAndReturnDashboardView() {
        BookRepository bookRepo = mock(BookRepository.class);
        LoanRepository loanRepo = mock(LoanRepository.class);

        Book b1 = new Book();
        b1.setAvailableCopies(3);
        Book b2 = new Book();
        b2.setAvailableCopies(null);

        when(bookRepo.count()).thenReturn(2L);
        when(bookRepo.findAll()).thenReturn(Arrays.asList(b1, b2));

        Loan l1 = new Loan();
        l1.setReturnedAt(null);
        Loan l2 = new Loan();
        l2.setReturnedAt(LocalDate.now());

        List<Loan> loans = Arrays.asList(l1, l2);
        when(loanRepo.findAll()).thenReturn(loans);

        HomeController controller = new HomeController(bookRepo, loanRepo);
        Model model = new ExtendedModelMap();

        String view = controller.dashboard(model);

        assertThat(view).isEqualTo("dashboard");
        assertThat(model.getAttribute("booksCount")).isEqualTo(2L);
        assertThat(model.getAttribute("totalAvailableCopies")).isEqualTo(3L);
        assertThat(model.getAttribute("loansCount")).isEqualTo(2L);
        assertThat(model.getAttribute("activeLoansCount")).isEqualTo(1L);
    }
}
