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
@Schema(name = "VehicleResponse", description = "Vehicle response data")
public class VehicleResponseDTO {

    @Schema(example = "1", description = "Unique vehicle ID")
    private Long id;

    @Schema(example = "Toyota", description = "Vehicle brand")
    private String brand;

    @Schema(example = "Corolla", description = "Vehicle model")
    private String model;

    @Schema(example = "ABC1234", description = "Vehicle plate")
    private String plate;

    @Schema(example = "2023", description = "Vehicle manufacturing year")
    private Integer year;

    @Schema(example = "150.00", description = "Daily rate in BRL")
    private BigDecimal dailyValue;

    @Schema(example = "AVAILABLE", description = "Vehicle status: AVAILABLE or UNAVAILABLE")
    private String status;

    @Schema(example = "2024-01-15T10:30:00", description = "Creation date and time")
    private LocalDateTime createdAt;

    @Schema(example = "2024-01-20T14:45:00", description = "Last update date and time")
    private LocalDateTime updatedAt;

}