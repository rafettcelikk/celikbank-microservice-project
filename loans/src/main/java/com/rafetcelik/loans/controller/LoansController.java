package com.rafetcelik.loans.controller;

import com.rafetcelik.common.dto.ErrorResponseDto;
import com.rafetcelik.loans.constants.LoansConstants;
import com.rafetcelik.loans.dto.LoansContactInfoDto;
import com.rafetcelik.loans.dto.LoansDto;
import com.rafetcelik.loans.dto.ResponseDto;
import com.rafetcelik.loans.service.loans.ILoansService;
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
        name = "CRUD REST APIs for Loans in CelikBank",
        description = "CRUD REST APIs in CelikBank to CREATE, UPDATE, FETCH and DELETE loan details"
)
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api", produces = {MediaType.APPLICATION_JSON_VALUE})
@Validated
public class LoansController {
    private static final Logger logger = LoggerFactory.getLogger(LoansController.class);

    private final ILoansService loansService;

    @Value("${build.version}")
    private String buildVersion;

    @Autowired
    private Environment environment;

    @Autowired
    private LoansContactInfoDto  loansContactInfoDto;

    @Operation(
            summary = "Create Loan REST API",
            description = "REST API to create new Loan inside CelikBank"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "HTTP Status 201 CREATED",
                    content = @Content(schema = @Schema(implementation = ResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "HTTP Status 500 INTERNAL SERVER ERROR",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))
            )
    })
    @PostMapping("/create")
    public ResponseEntity<ResponseDto> createLoan(@RequestParam("mobileNumber")
                                                  @Pattern(regexp = "(^$|[0-9]{10})", message = "Mobile number must be 10 digits")
                                                  String mobileNumber) {
        loansService.createLoan(mobileNumber);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ResponseDto(LoansConstants.STATUS_201, LoansConstants.MESSAGE_201));
    }

    @Operation(
            summary = "Fetch Loan REST API",
            description = "REST API to fetch Loan details inside CelikBank"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "HTTP Status 200 OK",
                    content = @Content(schema = @Schema(implementation = LoansDto.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "HTTP Status 500 INTERNAL SERVER ERROR",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))
            )
    })
    @GetMapping("/fetch")
    public ResponseEntity<LoansDto> fetchLoan(@RequestHeader("celikbank-correlation-id") String correlationId, @RequestParam("mobileNumber")
                                              @Pattern(regexp = "(^$|[0-9]{10})", message = "Mobile number must be 10 digits")
                                              String mobileNumber) {
        logger.debug("fetchLoan method started");
        LoansDto loansDto = loansService.fetchLoan(mobileNumber);
        logger.debug("fetchLoan method finished");
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(loansDto);
    }

    @Operation(
            summary = "Update Loan REST API",
            description = "REST API to update Loan details inside CelikBank"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "HTTP Status 200 OK",
                    content = @Content(schema = @Schema(implementation = ResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "417",
                    description = "HTTP Status 417 EXPECTATION FAILED",
                    content = @Content(schema = @Schema(implementation = ResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "HTTP Status 500 INTERNAL SERVER ERROR",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))
            )
    })
    @PutMapping("/update")
    public ResponseEntity<ResponseDto> updateLoan(@RequestBody @Valid LoansDto loansDto) {
        boolean isUpdated = loansService.updateLoan(loansDto);
        if (isUpdated) {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new ResponseDto(LoansConstants.STATUS_200, LoansConstants.MESSAGE_200));
        } else {
            return ResponseEntity
                    .status(HttpStatus.EXPECTATION_FAILED)
                    .body(new ResponseDto(LoansConstants.STATUS_417, LoansConstants.MESSAGE_417_UPDATE));
        }
    }

    @Operation(
            summary = "Delete Loan REST API",
            description = "REST API to delete Loan details from CelikBank"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "HTTP Status 200 OK",
                    content = @Content(schema = @Schema(implementation = ResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "417",
                    description = "HTTP Status 417 EXPECTATION FAILED",
                    content = @Content(schema = @Schema(implementation = ResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "HTTP Status 500 INTERNAL SERVER ERROR",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))
            )
    })
    @DeleteMapping("/delete")
    public ResponseEntity<ResponseDto> deleteLoan(@RequestParam("mobileNumber")
                                                  @Pattern(regexp = "(^$|[0-9]{10})", message = "Mobile number must be 10 digits")
                                                  String mobileNumber) {
        boolean isDeleted = loansService.deleteLoan(mobileNumber);
        if (isDeleted) {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new ResponseDto(LoansConstants.STATUS_200, LoansConstants.MESSAGE_200));
        } else {
            return ResponseEntity
                    .status(HttpStatus.EXPECTATION_FAILED)
                    .body(new ResponseDto(LoansConstants.STATUS_417, LoansConstants.MESSAGE_417_DELETE));
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
    @GetMapping("/build-info")
    public ResponseEntity<String> getBuildInfo() {
        return ResponseEntity.status(HttpStatus.OK).body("Build Version: " + buildVersion);
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
    @GetMapping("/java-version")
    public ResponseEntity<String> getJavaVersion() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body("Java Version: " + environment.getProperty("java.version"));
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
    public ResponseEntity<LoansContactInfoDto> getContactInfo() {
        logger.debug("Invoked Loans contact info REST API");
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(loansContactInfoDto);
    }
}