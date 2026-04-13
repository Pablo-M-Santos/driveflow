package com.pablo.driveflow_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(name = "RentalResponse", description = "Dados retornados de um aluguel")
public class RentalResponseDTO {

    @Schema(example = "1", description = "ID do aluguel")
    private Long id;

    @Schema(example = "3", description = "ID do cliente")
    private Long customerId;

    @Schema(example = "Joao Silva", description = "Nome do cliente")
    private String customerName;

    @Schema(example = "4", description = "ID do veículo")
    private Long vehicleId;

    @Schema(example = "ABC1234", description = "Placa do veículo")
    private String vehiclePlate;

    @Schema(example = "2026-04-15", description = "Data inicial do aluguel")
    private LocalDate startDate;

    @Schema(example = "2026-04-18", description = "Data final do aluguel")
    private LocalDate endDate;

    @Schema(example = "600.00", description = "Valor total do aluguel")
    private BigDecimal totalValue;

    @Schema(example = "ACTIVE", description = "Status do aluguel")
    private String status;

    @Schema(example = "2026-04-12T16:30:00", description = "Data e hora de criação")
    private LocalDateTime createdAt;

    @Schema(example = "2026-04-12T16:40:00", description = "Data e hora da última atualização")
    private LocalDateTime updatedAt;

    @Schema(example = "2026-04-13T10:00:00", description = "Data e hora de cancelamento")
    private LocalDateTime canceledAt;
}

