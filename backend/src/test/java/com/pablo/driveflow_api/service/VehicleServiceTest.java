package com.pablo.driveflow_api.service;

import com.pablo.driveflow_api.dto.VehicleRequestDTO;
import com.pablo.driveflow_api.dto.VehicleResponseDTO;
import com.pablo.driveflow_api.exception.DuplicateResourceException;
import com.pablo.driveflow_api.model.Vehicle;
import com.pablo.driveflow_api.model.VehicleStatus;
import com.pablo.driveflow_api.repository.VehicleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VehicleServiceTest {

    @Mock
    private VehicleRepository vehicleRepository;

    @InjectMocks
    private VehicleService vehicleService;

    @Test
    void createVehicle_shouldThrowDuplicateResourceException_whenPlateAlreadyExists() {
        VehicleRequestDTO request = buildVehicleRequest();
        when(vehicleRepository.existsByPlate(request.getPlate())).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> vehicleService.createVehicle(request));
        verify(vehicleRepository, never()).saveAndFlush(any(Vehicle.class));
    }

    @Test
    void createVehicle_shouldCreateVehicle_whenDataIsValid() {
        VehicleRequestDTO request = buildVehicleRequest();
        when(vehicleRepository.existsByPlate(request.getPlate())).thenReturn(false);
        when(vehicleRepository.saveAndFlush(any(Vehicle.class))).thenAnswer(invocation -> {
            Vehicle vehicle = invocation.getArgument(0);
            vehicle.setId(1L);
            return vehicle;
        });

        VehicleResponseDTO response = vehicleService.createVehicle(request);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals(request.getPlate(), response.getPlate());
        assertEquals("AVAILABLE", response.getStatus());
    }

    @Test
    void updateVehicle_shouldThrowDuplicateResourceException_whenPlateChangesToExistingOne() {
        Vehicle existing = buildVehicleEntity();
        VehicleRequestDTO request = VehicleRequestDTO.builder()
                .brand("Toyota")
                .model("Yaris")
                .plate("ZZZ9999")
                .year(2024)
                .dailyValue(BigDecimal.valueOf(300))
                .build();

        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(vehicleRepository.existsByPlate(request.getPlate())).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> vehicleService.updateVehicle(1L, request));
        verify(vehicleRepository, never()).saveAndFlush(any(Vehicle.class));
    }

    @Test
    void updateVehicleStatus_shouldUpdateStatusAndPersist() {
        Vehicle vehicle = buildVehicleEntity();
        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(vehicle));
        when(vehicleRepository.save(any(Vehicle.class))).thenAnswer(invocation -> invocation.getArgument(0));

        VehicleResponseDTO response = vehicleService.updateVehicleStatus(1L, VehicleStatus.UNAVAILABLE);

        assertEquals("UNAVAILABLE", response.getStatus());
        verify(vehicleRepository).save(vehicle);
    }

    private VehicleRequestDTO buildVehicleRequest() {
        return VehicleRequestDTO.builder()
                .brand("Toyota")
                .model("Corolla")
                .plate("ABC1234")
                .year(2023)
                .dailyValue(BigDecimal.valueOf(150))
                .build();
    }

    private Vehicle buildVehicleEntity() {
        return Vehicle.builder()
                .id(1L)
                .brand("Toyota")
                .model("Corolla")
                .plate("ABC1234")
                .year(2023)
                .dailyValue(BigDecimal.valueOf(150))
                .status(VehicleStatus.AVAILABLE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
}
