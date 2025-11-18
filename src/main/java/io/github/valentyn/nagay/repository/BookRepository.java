package io.github.valentyn.nagay.repository;

import io.github.valentyn.nagay.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long> {
}
