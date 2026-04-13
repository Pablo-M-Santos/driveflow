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
@Schema(name = "CustomerRequest", description = "Dados para criar ou atualizar um cliente")
public class CustomerRequestDTO {

    @NotBlank(message = "Nome do cliente não pode ser vazio")
    @Size(min = 3, max = 120, message = "Nome deve ter entre 3 e 120 caracteres")
    @Schema(example = "João Silva", description = "Nome completo do cliente")
    private String name;

    @NotBlank(message = "CPF não pode ser vazio")
    @Pattern(regexp = "^\\d{11}$", message = "CPF deve conter exatamente 11 dígitos")
    @Schema(example = "12345678901", description = "CPF do cliente (11 dígitos)")
    private String cpf;

    @NotBlank(message = "Email não pode ser vazio")
    @Email(message = "Email deve ser válido")
    @Schema(example = "joao@example.com", description = "Email único do cliente")
    private String email;

    @NotBlank(message = "Telefone não pode ser vazio")
    @Pattern(regexp = "^\\d{10,11}$", message = "Telefone deve conter 10 ou 11 dígitos")
    @Schema(example = "11987654321", description = "Telefone do cliente (10 ou 11 dígitos)")
    private String phone;

    @NotNull(message = "Data de cadastro não pode ser nula")
    @PastOrPresent(message = "Data de cadastro não pode ser no futuro")
    @Schema(example = "2024-01-15", description = "Data de cadastro do cliente")
    private LocalDate registrationDate;
}

