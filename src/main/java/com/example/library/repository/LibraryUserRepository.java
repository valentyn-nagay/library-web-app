package com.example.library.repository;

import com.example.library.model.LibraryUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LibraryUserRepository extends JpaRepository<LibraryUser, Long> {

    Optional<LibraryUser> findByUsername(String username);
}
