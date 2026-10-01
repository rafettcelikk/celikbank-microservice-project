package com.rafetcelik.accounts.controller;

import com.rafetcelik.accounts.constants.AccountsConstants;
import com.rafetcelik.accounts.dto.AccountsContactInfoDto;
import com.rafetcelik.accounts.dto.CustomerDto;
import com.rafetcelik.accounts.dto.ResponseDto;
import com.rafetcelik.accounts.service.accounts.IAccountsService;
import com.rafetcelik.common.dto.ErrorResponseDto;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(
        name = "CRUD REST APIs for Accounts in CelikBank",
        description = "CRUD REST APIs in CelikBank to CREATE, UPDATE, FETCH and DELETE account details"
)
@RestController
@RequestMapping(value = "/api", produces = {MediaType.APPLICATION_JSON_VALUE})
@RequiredArgsConstructor
@Validated
public class AccountsController {
    private final IAccountsService accountsService;

    private Logger logger = LoggerFactory.getLogger(AccountsController.class);

    @Value("${build.version}")
    private String buildVersion;

    @Autowired
    private Environment environment;

    @Autowired
    private AccountsContactInfoDto accountsContactInfoDto;

    @Operation(
            summary = "Create Account REST API",
            description = "REST API to create new Customer and Account inside CelikBank"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "HTTP Status 201 CREATED"),
            @ApiResponse(
                    responseCode = "500",
                    description = "HTTP Status 500 INTERNAL SERVER ERROR",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @PostMapping("/create")
    public ResponseEntity<ResponseDto> createAccount(@RequestBody @Valid CustomerDto customerDto) {
        accountsService.createAccount(customerDto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ResponseDto(AccountsConstants.STATUS_201, AccountsConstants.MESSAGE_201));

    }

    @Operation(
            summary = "Fetch Account REST API",
            description = "REST API to fetch Customer and Account details inside CelikBank"
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
    @GetMapping("/fetch")
    public ResponseEntity<CustomerDto> fetchAccount(@RequestParam("mobileNumber")
                                                        @Pattern(regexp = "(^$|[0-9]{10})", message = "Mobile number must be 10 digits")
                                                        String mobileNumber) {
        CustomerDto customerDto = accountsService.fetchAccount(mobileNumber);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(customerDto);
    }

    @Operation(
            summary = "Update Account REST API",
            description = "REST API to update Customer and Account details inside CelikBank"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "HTTP Status 200 OK"),
            @ApiResponse(responseCode = "417", description = "HTTP Status 417 EXPECTATION FAILED"),
            @ApiResponse(responseCode = "500", description = "HTTP Status 500 INTERNAL SERVER ERROR", content =
            @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @PutMapping("/update")
    public ResponseEntity<ResponseDto> updateAccount(@RequestBody @Valid CustomerDto customerDto) {
        boolean isUpdated = accountsService.updateAccount(customerDto);
        if (isUpdated) {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new ResponseDto(AccountsConstants.STATUS_200, AccountsConstants.MESSAGE_200));
        } else {
            return ResponseEntity
                    .status(HttpStatus.EXPECTATION_FAILED)
                    .body(new ResponseDto(AccountsConstants.STATUS_417, AccountsConstants.MESSAGE_417_UPDATE));
        }
    }

    @Operation(
            summary = "Delete Account REST API",
            description = "REST API to delete Customer and Account details inside CelikBank"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "HTTP Status 200 OK"),
            @ApiResponse(responseCode = "417", description = "HTTP Status 417 EXPECTATION FAILED"),
            @ApiResponse(responseCode = "500", description = "HTTP Status 500 INTERNAL SERVER ERROR", content =
            @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @DeleteMapping("/delete")
    public ResponseEntity<ResponseDto> deleteAccount(@RequestParam("mobileNumber")
                                                         @Pattern(regexp = "(^$|[0-9]{10})", message = "Mobile number must be 10 digits")
                                                         String mobileNumber) {
        boolean isDeleted = accountsService.deleteAccount(mobileNumber);
        if (isDeleted) {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new ResponseDto(AccountsConstants.STATUS_200, AccountsConstants.MESSAGE_200));
        } else {
            return ResponseEntity
                    .status(HttpStatus.EXPECTATION_FAILED)
                    .body(new ResponseDto(AccountsConstants.STATUS_417, AccountsConstants.MESSAGE_417_DELETE));
        }
    }

    @Operation(
            summary = "Get Build Info REST API",
            description = "REST API to get the build version of the application"
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
    @Retry(name = "getBuildInfo", fallbackMethod = "getBuildInfoFallback")
    @GetMapping("/build-info")
    public ResponseEntity<String> getBuildInfo() {
        logger.debug("getBuildInfo() method called");
        return ResponseEntity.status(HttpStatus.OK).body("Build Version: " + buildVersion);
    }

    public ResponseEntity<String> getBuildInfoFallback(Throwable throwable) {
        logger.debug("getBuildInfoFallback() method called", throwable);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body("0.9");
    }

    @Operation(
            summary = "Get Java Version REST API",
            description = "REST API to get the Java version of the application"
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
    @RateLimiter(name = "getJavaVersion", fallbackMethod = "getJavaVersionFallback")
    @GetMapping("/java-version")
    public ResponseEntity<String> getJavaVersion() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body("Java Version: " + environment.getProperty("java.version"));
    }

    public ResponseEntity<String> getJavaVersionFallback(Throwable throwable) {
        logger.debug("getJavaVersionFallback() method called", throwable);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body("Java Version: 21");
    }

    @Operation(
            summary = "Get Contact Info REST API",
            description = "REST API to get the contact information of the application"
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
    @GetMapping("/contact-info")
    public ResponseEntity<AccountsContactInfoDto> getContactInfo() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(accountsContactInfoDto);
    }
}
