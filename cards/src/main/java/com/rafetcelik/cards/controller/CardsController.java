package com.rafetcelik.cards.controller;

import com.rafetcelik.cards.constants.CardsConstants;
import com.rafetcelik.cards.dto.CardsContactInfoDto;
import com.rafetcelik.cards.dto.CardsDto;
import com.rafetcelik.cards.dto.ResponseDto;
import com.rafetcelik.cards.service.cards.ICardsService;
import com.rafetcelik.common.dto.ErrorResponseDto;
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
        name = "CRUD REST APIs for Cards in CelikBank",
        description = "CRUD REST APIs in CelikBank to CREATE, UPDATE, FETCH and DELETE card details"
)
@RestController
@RequestMapping(value = "/api", produces = {MediaType.APPLICATION_JSON_VALUE})
@RequiredArgsConstructor
@Validated
public class CardsController {
    private static final Logger logger = LoggerFactory.getLogger(CardsController.class);

    private final ICardsService cardsService;

    @Value("${build.version}")
    private String buildVersion;

    @Autowired
    private Environment  environment;

    @Autowired
    private CardsContactInfoDto  cardsContactInfoDto;

    @Operation(
            summary = "Create Card REST API",
            description = "REST API to create new Card inside CelikBank"
    )
    @ApiResponse(
            responseCode = "201",
            description = "HTTP Status 201 CREATED"
    )
    @PostMapping("/create")
    public ResponseEntity<ResponseDto> createCard(@RequestBody @Valid CardsDto cardsDto) {
        cardsService.createCard(cardsDto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ResponseDto(CardsConstants.STATUS_201, CardsConstants.MESSAGE_201));
    }

    @Operation(
            summary = "Fetch Card REST API",
            description = "REST API to fetch Card details inside CelikBank"
    )
    @ApiResponse(
            responseCode = "200",
            description = "HTTP Status 200 OK"
    )
    @GetMapping("/fetch")
    public ResponseEntity<CardsDto> fetchCard(@RequestHeader("celikbank-correlation-id") String correlationId, @RequestParam("mobileNumber")
                                                 @Pattern(regexp = "(^$|[0-9]{10})", message = "Mobile number must be 10 digits")
                                                 String mobileNumber) {
        logger.debug("fetchCard method started");
        CardsDto cardsDto = cardsService.fetchCard(mobileNumber);
        logger.debug("fetchCard method finished");
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(cardsDto);
    }

    @Operation(
            summary = "Update Card REST API",
            description = "REST API to update Card details inside CelikBank"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "HTTP Status 200 OK"),
            @ApiResponse(responseCode = "417", description = "HTTP Status 417 EXPECTATION FAILED"),
            @ApiResponse(responseCode = "500", description = "HTTP Status 500 INTERNAL SERVER ERROR", content =
            @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
        }
    )
    @PutMapping("/update")
    public ResponseEntity<ResponseDto> updateCard(@RequestBody @Valid CardsDto cardsDto) {
        boolean isUpdated = cardsService.updateCard(cardsDto);
        if (isUpdated) {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new ResponseDto(CardsConstants.STATUS_200, CardsConstants.MESSAGE_200));
        } else {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ResponseDto(CardsConstants.STATUS_417, CardsConstants.MESSAGE_417_UPDATE));
        }
    }

    @Operation(
            summary = "Delete Card REST API",
            description = "REST API to delete Card details inside CelikBank"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "HTTP Status 200 OK"),
            @ApiResponse(responseCode = "417", description = "HTTP Status 417 EXPECTATION FAILED"),
            @ApiResponse(responseCode = "500", description = "HTTP Status 500 INTERNAL SERVER ERROR", content =
            @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    }
    )
    @DeleteMapping("/delete")
    public ResponseEntity<ResponseDto> deleteCard(@RequestParam("mobileNumber")
                                                     @Pattern(regexp = "(^$|[0-9]{10})", message = "Mobile number must be 10 digits")
                                                     String mobileNumber) {
        boolean isDeleted = cardsService.deleteCard(mobileNumber);
        if (isDeleted) {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new ResponseDto(CardsConstants.STATUS_200, CardsConstants.MESSAGE_200));
        } else {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ResponseDto(CardsConstants.STATUS_417, CardsConstants.MESSAGE_417_DELETE));
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
    public ResponseEntity<CardsContactInfoDto> getContactInfo() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(cardsContactInfoDto);
    }
}