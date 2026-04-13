package com.pablo.driveflow_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(name = "VehicleRequest", description = "Dados para criar ou atualizar um veículo")
public class VehicleRequestDTO {

    @NotBlank(message = "Marca do veículo não pode ser vazia")
    @Schema(example = "Toyota", description = "Marca do veículo")
    private String brand;

    @NotBlank(message = "Modelo do veículo não pode ser vazio")
    @Schema(example = "Corolla", description = "Modelo do veículo")
    private String model;

    @NotBlank(message = "Placa do veículo não pode ser vazia")
    @Schema(example = "ABC1234", description = "Placa única do veículo (deve ser única no sistema)")
    private String plate;

    @Schema(example = "2023", description = "Ano de fabricação do veículo")
    private Integer year;

    @NotNull(message = "Valor da diária não pode ser nulo")
    @Positive(message = "Valor da diária deve ser maior que zero")
    @Schema(example = "150.00", description = "Valor da diária em reais")
    private BigDecimal dailyValue;


}