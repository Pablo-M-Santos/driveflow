package com.pablo.driveflow_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(name = "CustomerResponse", description = "Customer response data")
public class CustomerResponseDTO {

    @Schema(example = "1", description = "Unique customer ID")
    private Long id;

    @Schema(example = "João Silva", description = "Customer full name")
    private String name;

    @Schema(example = "12345678901", description = "Customer CPF")
    private String cpf;

    @Schema(example = "joao@example.com", description = "Customer email")
    private String email;

    @Schema(example = "11987654321", description = "Customer phone number")
    private String phone;

    @Schema(example = "2024-01-15", description = "Customer registration date")
    private LocalDate registrationDate;

    @Schema(example = "2024-01-15T10:30:00", description = "Creation date and time")
    private LocalDateTime createdAt;

    @Schema(example = "2024-01-20T14:45:00", description = "Last update date and time")
    private LocalDateTime updatedAt;
}

