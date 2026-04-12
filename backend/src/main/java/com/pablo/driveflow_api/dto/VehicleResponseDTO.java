package com.pablo.driveflow_api.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleResponseDTO {

    private Long id;
    private String brand;
    private String model;
    private String plate;
    private Integer year;
    private BigDecimal dailyValue;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}