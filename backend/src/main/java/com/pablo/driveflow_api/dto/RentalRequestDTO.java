package com.pablo.driveflow_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(name = "RentalRequest", description = "Dados para registrar um novo aluguel")
public class RentalRequestDTO {

    @NotNull(message = "ID do cliente é obrigatório")
    @Schema(example = "1", description = "ID do cliente")
    private Long customerId;

    @NotNull(message = "ID do veículo é obrigatório")
    @Schema(example = "2", description = "ID do veículo")
    private Long vehicleId;

    @NotNull(message = "Data inicial do aluguel é obrigatória")
    @Schema(example = "2026-04-15", description = "Data de início do aluguel")
    private LocalDate startDate;

    @NotNull(message = "Data final do aluguel é obrigatória")
    @Schema(example = "2026-04-18", description = "Data de término do aluguel")
    private LocalDate endDate;
}

