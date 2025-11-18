package io.github.valentyn.nagay.repository;

import io.github.valentyn.nagay.model.Loan;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoanRepository extends JpaRepository<Loan, Long> {
}
