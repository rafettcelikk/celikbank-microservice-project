package com.rafetcelik.loans.service.loans;

import com.rafetcelik.loans.constants.LoansConstants;
import com.rafetcelik.loans.dto.LoansDto;
import com.rafetcelik.loans.entity.Loans;
import com.rafetcelik.loans.exception.LoanAlreadyExistsException;
import com.rafetcelik.loans.exception.ResourceNotFoundException;
import com.rafetcelik.loans.mapper.LoansMapper;
import com.rafetcelik.loans.repository.LoansRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class LoansService implements ILoansService{
    private final LoansRepository loansRepository;

    @Override
    public void createLoan(String mobileNumber) {
        Optional<Loans> optionalLoans = loansRepository.findByMobileNumber(mobileNumber);
        if (optionalLoans.isPresent()) {
            throw new LoanAlreadyExistsException("Loan already exists for the given mobile number: " + mobileNumber);
        }
        loansRepository.save(createNewLoan(mobileNumber));
    }

    public Loans createNewLoan(String mobileNumber) {
        Loans newLoan = new Loans();
        long randomLoanNumber = 100000000000L + new Random().nextInt(900000000);
        newLoan.setMobileNumber(mobileNumber);
        newLoan.setLoanNumber(Long.toString(randomLoanNumber));
        newLoan.setLoanType(LoansConstants.HOME_LOAN);
        newLoan.setTotalLoan(LoansConstants.NEW_LOAN_LIMIT);
        newLoan.setAmountPaid(0);
        newLoan.setOutstandingAmount(LoansConstants.NEW_LOAN_LIMIT);
        return newLoan;
    }

    @Override
    public LoansDto fetchLoan(String mobileNumber) {
        Loans loans = loansRepository.findByMobileNumber(mobileNumber).orElseThrow(
                () -> new ResourceNotFoundException("Loan not found for the given mobile number: " + mobileNumber)
        );
        return LoansMapper.mapToLoansDto(loans, new LoansDto());
    }

    @Override
    public boolean updateLoan(LoansDto loansDto) {
        Loans loans = loansRepository.findByLoanNumber(loansDto.getLoanNumber()).orElseThrow(
                () -> new ResourceNotFoundException("Loan not found for the given loan number: " + loansDto.getLoanNumber())
        );
        LoansMapper.mapToLoans(loansDto, loans);
        loansRepository.save(loans);
        return true;
    }

    @Override
    public boolean deleteLoan(String mobileNumber) {
        Loans loans = loansRepository.findByMobileNumber(mobileNumber).orElseThrow(
                () -> new ResourceNotFoundException("Loan not found for the given mobile number: " + mobileNumber)
        );
        loansRepository.deleteById(loans.getLoanId());
        return true;
    }
}
