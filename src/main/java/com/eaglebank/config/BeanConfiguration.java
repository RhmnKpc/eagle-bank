package com.eaglebank.config;

import com.eaglebank.domain.service.AccountDomainService;
import com.eaglebank.domain.service.TransactionDomainService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Bean Configuration
 * Wire up ports and adapters, instantiate domain services
 */
@Configuration
public class BeanConfiguration {

    @Bean
    public AccountDomainService accountDomainService() {
        return new AccountDomainService();
    }

    @Bean
    public TransactionDomainService transactionDomainService() {
        return new TransactionDomainService();
    }
}
