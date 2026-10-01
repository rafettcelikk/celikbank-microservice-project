package com.rafetcelik.loans.audit;

import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component("auditAwareImpl")
public class AuditAwareImpl implements AuditorAware<String> {

    @Override
    public java.util.Optional<String> getCurrentAuditor() {
        return Optional.of("LOANS_MS");
    }
}
