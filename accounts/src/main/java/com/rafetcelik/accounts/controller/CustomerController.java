package com.rafetcelik.accounts.controller;

import com.rafetcelik.accounts.dto.CustomerDetailsDto;
import com.rafetcelik.accounts.service.customer.ICustomerService;
import com.rafetcelik.common.dto.ErrorResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(
        name = "CRUD REST APIs for Customer in CelikBank",
        description = "CRUD REST APIs in CelikBank to CREATE, UPDATE, FETCH and DELETE customer details"
)
@RestController
@RequestMapping(value = "/api", produces = {MediaType.APPLICATION_JSON_VALUE})
@RequiredArgsConstructor
@Validated
public class CustomerController {

    private static final Logger logger = LoggerFactory.getLogger(CustomerController.class);

    private final ICustomerService customerService;

    @Operation(
            summary = "Fetch Customer Details REST API",
            description = "REST API to fetch Customer details inside CelikBank"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "HTTP Status 200 OK"),
            @ApiResponse(
                    responseCode = "500",
                    description = "HTTP Status 500 INTERNAL SERVER ERROR",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @GetMapping("fetchCustomerDetails")
    public ResponseEntity<CustomerDetailsDto>  fetchCustomerDetails(@RequestHeader("celikbank-correlation-id") String correlationId, @RequestParam("mobileNumber")
                                                                        @Pattern(regexp = "(^$|[0-9]{10})", message = "Mobile number must be 10 digits")
                                                                        String mobileNumber) {
        logger.debug("fetchCustomerDetails method started");
        CustomerDetailsDto customerDetailsDto = customerService.fetchCustomerDetails(correlationId, mobileNumber);
        logger.debug("fetchCustomerDetails method finished");
        return ResponseEntity.status(HttpStatus.OK).body(customerDetailsDto);
    }
}
