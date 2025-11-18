package io.github.valentyn.nagay.repository;

import io.github.valentyn.nagay.model.Book;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class BookRepositoryTest {

    @Autowired
    private BookRepository bookRepository;

    @Test
    void saveAndFindBook() {
        Book book = new Book();
        book.setTitle("Test Book");
        book.setAuthor("Author");
        book.setIsbn("1234567890");
        book.setTotalCopies(5);
        book.setAvailableCopies(5);

        bookRepository.save(book);

        List<Book> all = bookRepository.findAll();
        assertThat(all).isNotEmpty();
        assertThat(all.get(0).getTitle()).isEqualTo("Test Book");
    }
}
