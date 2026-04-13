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
@Schema(name = "VehicleRequest", description = "Data for creating or updating a vehicle")
public class VehicleRequestDTO {

    @NotBlank(message = "Vehicle brand cannot be blank")
    @Schema(example = "Toyota", description = "Vehicle brand")
    private String brand;

    @NotBlank(message = "Vehicle model cannot be blank")
    @Schema(example = "Corolla", description = "Vehicle model")
    private String model;

    @NotBlank(message = "Vehicle plate cannot be blank")
    @Schema(example = "ABC1234", description = "Unique vehicle plate in the system")
    private String plate;

    @Schema(example = "2023", description = "Vehicle manufacturing year")
    private Integer year;

    @NotNull(message = "Daily rate cannot be null")
    @Positive(message = "Daily rate must be greater than zero")
    @Schema(example = "150.00", description = "Daily rate in BRL")
    private BigDecimal dailyValue;


}