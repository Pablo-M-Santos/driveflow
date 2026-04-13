package com.pablo.driveflow_api.mapper;

import com.pablo.driveflow_api.dto.CustomerRequestDTO;
import com.pablo.driveflow_api.dto.CustomerResponseDTO;
import com.pablo.driveflow_api.model.Customer;

public class CustomerMapper {

    public static Customer toEntity(CustomerRequestDTO dto) {
        return Customer.builder()
                .name(dto.getName())
                .cpf(dto.getCpf())
                .email(dto.getEmail())
                .phone(dto.getPhone())
                .registrationDate(dto.getRegistrationDate())
                .build();
    }

    public static CustomerResponseDTO toDTO(Customer entity) {
        return CustomerResponseDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .cpf(entity.getCpf())
                .email(entity.getEmail())
                .phone(entity.getPhone())
                .registrationDate(entity.getRegistrationDate())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}

