package com.rafetcelik.accounts.functions;

import com.rafetcelik.accounts.service.accounts.IAccountsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;

@Configuration
public class AccountsFunctions {
    private static final Logger log =  LoggerFactory.getLogger(AccountsFunctions.class);

    public Consumer<Long> updateCommunication(IAccountsService accountsService) {
        return accountNumber -> {
            log.info("Updating communication for account: {}", accountNumber.toString());
            accountsService.updateCommunicationStatus(accountNumber);
        };
    }
}
