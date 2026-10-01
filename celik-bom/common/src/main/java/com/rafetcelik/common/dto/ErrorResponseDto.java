package com.rafetcelik.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Schema(
        name = "ErrorResponse",
        description = "Error response information"
)
public class ErrorResponseDto {
    @Schema(
            description = "API path that caused the error"
    )
    private String apiPath;

    @Schema(
            description = "HTTP status code of the error"
    )
    private HttpStatus errorCode;

    @Schema(
            description = "Error message describing the error"
    )
    private String errorMessage;

    @Schema(
            description = "Timestamp when the error occurred"
    )
    private LocalDateTime errorTime;
}
