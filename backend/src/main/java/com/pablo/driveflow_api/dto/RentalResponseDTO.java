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
@Schema(name = "RentalResponse", description = "Rental response data")
public class RentalResponseDTO {

    @Schema(example = "1", description = "Rental ID")
    private Long id;

    @Schema(example = "3", description = "Customer ID")
    private Long customerId;

    @Schema(example = "Joao Silva", description = "Customer name")
    private String customerName;

    @Schema(example = "4", description = "Vehicle ID")
    private Long vehicleId;

    @Schema(example = "ABC1234", description = "Vehicle plate")
    private String vehiclePlate;

    @Schema(example = "2026-04-15", description = "Rental start date")
    private LocalDate startDate;

    @Schema(example = "2026-04-18", description = "Rental end date")
    private LocalDate endDate;

    @Schema(example = "600.00", description = "Total rental value")
    private BigDecimal totalValue;

    @Schema(example = "ACTIVE", description = "Rental status")
    private String status;

    @Schema(example = "2026-04-12T16:30:00", description = "Creation date and time")
    private LocalDateTime createdAt;

    @Schema(example = "2026-04-12T16:40:00", description = "Last update date and time")
    private LocalDateTime updatedAt;

    @Schema(example = "2026-04-13T10:00:00", description = "Cancellation date and time")
    private LocalDateTime canceledAt;
}

