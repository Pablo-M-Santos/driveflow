package com.pablo.driveflow_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(name = "CustomerRequest", description = "Data for creating or updating a customer")
public class CustomerRequestDTO {

    @NotBlank(message = "Customer name cannot be blank")
    @Size(min = 3, max = 120, message = "Name must be between 3 and 120 characters")
    @Schema(example = "João Silva", description = "Customer full name")
    private String name;

    @NotBlank(message = "CPF cannot be blank")
    @Pattern(regexp = "^\\d{11}$", message = "CPF must contain exactly 11 digits")
    @Schema(example = "12345678901", description = "Customer CPF (11 digits)")
    private String cpf;

    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Email must be valid")
    @Schema(example = "joao@example.com", description = "Unique customer email")
    private String email;

    @NotBlank(message = "Phone cannot be blank")
    @Pattern(regexp = "^\\d{10,11}$", message = "Phone must contain 10 or 11 digits")
    @Schema(example = "11987654321", description = "Customer phone number (10 or 11 digits)")
    private String phone;

    @NotNull(message = "Registration date cannot be null")
    @PastOrPresent(message = "Registration date cannot be in the future")
    @Schema(example = "2024-01-15", description = "Customer registration date")
    private LocalDate registrationDate;
}

