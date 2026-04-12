package com.pablo.driveflow_api.mapper;

import com.pablo.driveflow_api.dto.VehicleRequestDTO;
import com.pablo.driveflow_api.dto.VehicleResponseDTO;
import com.pablo.driveflow_api.model.Vehicle;
import com.pablo.driveflow_api.model.VehicleStatus;

public class VehicleMapper {

    public static Vehicle toEntity(VehicleRequestDTO dto) {
        return Vehicle.builder()
                .brand(dto.getBrand())
                .model(dto.getModel())
                .plate(dto.getPlate())
                .year(dto.getYear())
                .dailyValue(dto.getDailyValue())
                .status(VehicleStatus.AVAILABLE)
                .build();
    }

    public static VehicleResponseDTO toDTO(Vehicle entity) {
        VehicleResponseDTO dto = new VehicleResponseDTO();
        dto.setId(entity.getId());
        dto.setBrand(entity.getBrand());
        dto.setModel(entity.getModel());
        dto.setPlate(entity.getPlate());
        dto.setYear(entity.getYear());
        dto.setDailyValue(entity.getDailyValue());
        dto.setStatus(entity.getStatus().name());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }
}
