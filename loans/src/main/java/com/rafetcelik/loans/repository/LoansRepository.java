package com.rafetcelik.loans.repository;

import com.rafetcelik.loans.entity.Loans;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LoansRepository extends JpaRepository<Loans, Long> {
    Optional<Loans> findByMobileNumber(String mobileNumber);

    Optional<Loans> findByLoanNumber( String loanNumber);
}
