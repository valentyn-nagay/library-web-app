package io.github.valentyn.nagay.controller;

import io.github.valentyn.nagay.model.Book;
import io.github.valentyn.nagay.service.BookService;
import org.junit.jupiter.api.Test;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class BookControllerTest {

    @Test
    void list_ShouldPutBooksAndReturnListView() {
        BookService service = mock(BookService.class);
        List<Book> books = Arrays.asList(new Book(), new Book());
        when(service.findAll()).thenReturn(books);

        BookController controller = new BookController(service);
        Model model = new ExtendedModelMap();

        String view = controller.list(model);

        assertThat(view).isEqualTo("books/list");
        assertThat(model.getAttribute("books")).isSameAs(books);
    }

    @Test
    void createForm_ShouldPutEmptyBookAndReturnFormView() {
        BookService service = mock(BookService.class);
        BookController controller = new BookController(service);
        Model model = new ExtendedModelMap();

        String view = controller.createForm(model);

        assertThat(view).isEqualTo("books/form");
        assertThat(model.getAttribute("book")).isInstanceOf(Book.class);
    }

    @Test
    void save_ShouldCallServiceAndRedirect() {
        BookService service = mock(BookService.class);
        BookController controller = new BookController(service);

        Book book = new Book();
        String view = controller.save(book);

        assertThat(view).isEqualTo("redirect:/books");
        verify(service).save(book);
    }

    @Test
    void details_WhenBookExists_ShouldPutBookAndReturnDetailsView() {
        BookService service = mock(BookService.class);
        Book book = new Book();
        book.setId(1L);
        when(service.findById(1L)).thenReturn(Optional.of(book));

        BookController controller = new BookController(service);
        Model model = new ExtendedModelMap();

        String view = controller.details(1L, model);

        assertThat(view).isEqualTo("books/details");
        assertThat(model.getAttribute("book")).isSameAs(book);
    }
}
