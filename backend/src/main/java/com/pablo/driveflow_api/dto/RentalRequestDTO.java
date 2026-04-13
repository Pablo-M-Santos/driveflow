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
@Schema(name = "RentalRequest", description = "Data for registering a new rental")
public class RentalRequestDTO {

    @NotNull(message = "Customer ID is required")
    @Schema(example = "1", description = "Customer ID")
    private Long customerId;

    @NotNull(message = "Vehicle ID is required")
    @Schema(example = "2", description = "Vehicle ID")
    private Long vehicleId;

    @NotNull(message = "Rental start date is required")
    @Schema(example = "2026-04-15", description = "Rental start date")
    private LocalDate startDate;

    @NotNull(message = "Rental end date is required")
    @Schema(example = "2026-04-18", description = "Rental end date")
    private LocalDate endDate;
}

