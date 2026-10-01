package com.rafetcelik.accounts.service.customer;

import com.rafetcelik.accounts.dto.CustomerDetailsDto;

public interface ICustomerService {
    CustomerDetailsDto fetchCustomerDetails(String correlationId, String mobileNumber);
}
