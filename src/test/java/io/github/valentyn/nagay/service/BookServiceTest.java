package io.github.valentyn.nagay.service;

import io.github.valentyn.nagay.model.Book;
import io.github.valentyn.nagay.repository.BookRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookService bookService;

    @Test
    void findAll_ShouldDelegateToRepository() {
        List<Book> expected = Arrays.asList(new Book(), new Book());
        when(bookRepository.findAll()).thenReturn(expected);

        List<Book> actual = bookService.findAll();

        assertThat(actual).isSameAs(expected);
        verify(bookRepository).findAll();
    }

    @Test
    void findById_ShouldDelegateToRepository() {
        Book book = new Book();
        book.setId(1L);
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        Optional<Book> result = bookService.findById(1L);

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(1L);
        verify(bookRepository).findById(1L);
    }

    @Test
    void save_WhenAvailableCopiesNull_ShouldSetFromTotalCopies() {
        Book book = new Book();
        book.setTotalCopies(10);
        book.setAvailableCopies(null);

        Book saved = new Book();
        saved.setId(1L);
        when(bookRepository.save(any(Book.class))).thenReturn(saved);

        Book result = bookService.save(book);

        ArgumentCaptor<Book> captor = ArgumentCaptor.forClass(Book.class);
        verify(bookRepository).save(captor.capture());
        Book toSave = captor.getValue();

        assertThat(toSave.getAvailableCopies()).isEqualTo(10);
        assertThat(result).isSameAs(saved);
    }

    @Test
    void save_WhenAvailableCopiesAlreadySet_ShouldNotOverride() {
        Book book = new Book();
        book.setTotalCopies(10);
        book.setAvailableCopies(3);

        when(bookRepository.save(any(Book.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Book result = bookService.save(book);

        assertThat(result.getAvailableCopies()).isEqualTo(3);
        verify(bookRepository).save(book);
    }
}
