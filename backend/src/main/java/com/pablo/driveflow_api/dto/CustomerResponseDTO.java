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
@Schema(name = "CustomerResponse", description = "Dados retornados de um cliente")
public class CustomerResponseDTO {

    @Schema(example = "1", description = "ID único do cliente")
    private Long id;

    @Schema(example = "João Silva", description = "Nome completo do cliente")
    private String name;

    @Schema(example = "12345678901", description = "CPF do cliente")
    private String cpf;

    @Schema(example = "joao@example.com", description = "Email do cliente")
    private String email;

    @Schema(example = "11987654321", description = "Telefone do cliente")
    private String phone;

    @Schema(example = "2024-01-15", description = "Data de cadastro do cliente")
    private LocalDate registrationDate;

    @Schema(example = "2024-01-15T10:30:00", description = "Data e hora de criação")
    private LocalDateTime createdAt;

    @Schema(example = "2024-01-20T14:45:00", description = "Data e hora da última atualização")
    private LocalDateTime updatedAt;
}

