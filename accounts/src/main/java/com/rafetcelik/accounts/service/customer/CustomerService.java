package com.rafetcelik.accounts.service.customer;

import com.rafetcelik.accounts.dto.AccountsDto;
import com.rafetcelik.accounts.dto.CardsDto;
import com.rafetcelik.accounts.dto.CustomerDetailsDto;
import com.rafetcelik.accounts.dto.LoansDto;
import com.rafetcelik.accounts.entity.Accounts;
import com.rafetcelik.accounts.entity.Customer;
import com.rafetcelik.accounts.exception.ResourceNotFoundException;
import com.rafetcelik.accounts.mapper.AccountsMapper;
import com.rafetcelik.accounts.mapper.CustomerMapper;
import com.rafetcelik.accounts.repository.AccountsRepository;
import com.rafetcelik.accounts.repository.CustomerRepository;
import com.rafetcelik.accounts.service.client.CardsFeignClient;
import com.rafetcelik.accounts.service.client.LoansFeignClient;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CustomerService implements ICustomerService{

    private final AccountsRepository  accountsRepository;

    private final CustomerRepository  customerRepository;

    private final CardsFeignClient  cardsFeignClient;

    private final LoansFeignClient  loansFeignClient;

    @Override
    public CustomerDetailsDto fetchCustomerDetails(String correlationId, String mobileNumber) {
        Customer customer = customerRepository.findByMobileNumber(mobileNumber).orElseThrow(
                () -> new ResourceNotFoundException("Customer", "mobileNumber", mobileNumber)
        );
        Accounts accounts = accountsRepository.findByCustomerId(customer.getCustomerId()).orElseThrow(
                () -> new ResourceNotFoundException("Account", "customerId", customer.getCustomerId().toString())
        );

        CustomerDetailsDto customerDetailsDto = CustomerMapper.mapToCustomerDetailsDto(customer, new CustomerDetailsDto());
        customerDetailsDto.setAccountsDto(AccountsMapper.mapToAccountsDto(accounts, new AccountsDto()));

        ResponseEntity<CardsDto> cardsDtoResponseEntity = cardsFeignClient.fetchCardDetails(correlationId, mobileNumber);
        if (null != cardsDtoResponseEntity) {
            customerDetailsDto.setCardsDto(cardsDtoResponseEntity.getBody());
        }

        ResponseEntity<LoansDto> loansDtoResponseEntity = loansFeignClient.fetchLoanDetails(correlationId, mobileNumber);
        if (null != loansDtoResponseEntity) {
            customerDetailsDto.setLoansDto(loansDtoResponseEntity.getBody());
        }

        return customerDetailsDto;
    }
}
