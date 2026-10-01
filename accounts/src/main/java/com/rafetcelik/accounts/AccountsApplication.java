package com.rafetcelik.accounts;

import com.rafetcelik.accounts.dto.AccountsContactInfoDto;
import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableFeignClients
@EnableDiscoveryClient
@EnableJpaAuditing(auditorAwareRef = "auditAwareImpl")
@EnableConfigurationProperties(value = {AccountsContactInfoDto.class})
@OpenAPIDefinition(
		info = @Info(
				title = "Accounts microservice REST API Documentation",
				description = "Rafet Celik Accounts microservice REST API Documentation",
				version = "v1",
				contact = @Contact(
						name = "Rafet Celik",
						email = "rafet.celik789@gmail.com",
						url = "https://www.rafetcelik.com"
				),
				license = @License(
						name = "Apache 2.0",
						url = "https://www.rafetcelik.com"
				)
		),
		externalDocs = @ExternalDocumentation(
				description = "Rafet Celik Accounts microservice REST API Documentation",
				url = "https://www.rafetcelik.com"
		)
)
public class AccountsApplication {

	public static void main(String[] args) {
		SpringApplication.run(AccountsApplication.class, args);
	}

}
