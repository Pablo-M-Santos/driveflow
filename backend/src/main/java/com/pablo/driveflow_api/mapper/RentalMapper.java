package com.pablo.driveflow_api.mapper;

import com.pablo.driveflow_api.dto.RentalResponseDTO;
import com.pablo.driveflow_api.model.Rental;

public class RentalMapper {

    public static RentalResponseDTO toDTO(Rental rental) {
        return RentalResponseDTO.builder()
                .id(rental.getId())
                .customerId(rental.getCustomer().getId())
                .customerName(rental.getCustomer().getName())
                .vehicleId(rental.getVehicle().getId())
                .vehiclePlate(rental.getVehicle().getPlate())
                .startDate(rental.getStartDate())
                .endDate(rental.getEndDate())
                .totalValue(rental.getTotalValue())
                .status(rental.getStatus().name())
                .createdAt(rental.getCreatedAt())
                .updatedAt(rental.getUpdatedAt())
                .canceledAt(rental.getCanceledAt())
                .build();
    }
}

