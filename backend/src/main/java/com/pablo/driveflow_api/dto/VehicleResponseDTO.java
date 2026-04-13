package com.pablo.driveflow_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(name = "VehicleResponse", description = "Dados retornados de um veículo")
public class VehicleResponseDTO {

    @Schema(example = "1", description = "ID único do veículo")
    private Long id;

    @Schema(example = "Toyota", description = "Marca do veículo")
    private String brand;

    @Schema(example = "Corolla", description = "Modelo do veículo")
    private String model;

    @Schema(example = "ABC1234", description = "Placa do veículo")
    private String plate;

    @Schema(example = "2023", description = "Ano de fabricação")
    private Integer year;

    @Schema(example = "150.00", description = "Valor da diária em reais")
    private BigDecimal dailyValue;

    @Schema(example = "AVAILABLE", description = "Status do veículo: AVAILABLE ou UNAVAILABLE")
    private String status;

    @Schema(example = "2024-01-15T10:30:00", description = "Data e hora de criação")
    private LocalDateTime createdAt;

    @Schema(example = "2024-01-20T14:45:00", description = "Data e hora da última atualização")
    private LocalDateTime updatedAt;

}