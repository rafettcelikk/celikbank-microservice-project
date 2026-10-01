package com.rafetcelik.loans.service.loans;

import com.rafetcelik.loans.dto.LoansDto;

public interface ILoansService {
    void createLoan(String mobileNumber);

    LoansDto fetchLoan(String mobileNumber);

    boolean updateLoan(LoansDto loansDto);

    boolean deleteLoan(String mobileNumber);
}
