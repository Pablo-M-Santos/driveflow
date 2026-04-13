package com.pablo.driveflow_api.service;

import com.pablo.driveflow_api.dto.RentalRequestDTO;
import com.pablo.driveflow_api.dto.RentalResponseDTO;
import com.pablo.driveflow_api.dto.VehicleResponseDTO;
import com.pablo.driveflow_api.exception.DuplicateResourceException;
import com.pablo.driveflow_api.exception.ValidationException;
import com.pablo.driveflow_api.model.*;
import com.pablo.driveflow_api.repository.CustomerRepository;
import com.pablo.driveflow_api.repository.RentalRepository;
import com.pablo.driveflow_api.repository.VehicleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RentalServiceTest {

    @Mock
    private RentalRepository rentalRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private VehicleRepository vehicleRepository;

    @InjectMocks
    private RentalService rentalService;

    @Test
    void createRental_shouldThrowValidationException_whenEndDateBeforeStartDate() {
        RentalRequestDTO request = RentalRequestDTO.builder()
                .customerId(1L)
                .vehicleId(2L)
                .startDate(LocalDate.of(2026, 4, 20))
                .endDate(LocalDate.of(2026, 4, 18))
                .build();

        assertThrows(ValidationException.class, () -> rentalService.createRental(request));
        verify(rentalRepository, never()).saveAndFlush(any(Rental.class));
    }

    @Test
    void createRental_shouldThrowValidationException_whenVehicleIsUnavailable() {
        Customer customer = buildCustomer();
        Vehicle vehicle = buildVehicle(VehicleStatus.UNAVAILABLE);
        RentalRequestDTO request = validRentalRequest();

        when(customerRepository.findById(request.getCustomerId())).thenReturn(Optional.of(customer));
        when(vehicleRepository.findById(request.getVehicleId())).thenReturn(Optional.of(vehicle));

        assertThrows(ValidationException.class, () -> rentalService.createRental(request));
        verify(rentalRepository, never()).existsConflictForVehicle(anyLong(), any(), any(), any());
    }

    @Test
    void createRental_shouldThrowDuplicateResourceException_whenDateConflictExists() {
        Customer customer = buildCustomer();
        Vehicle vehicle = buildVehicle(VehicleStatus.AVAILABLE);
        RentalRequestDTO request = validRentalRequest();

        when(customerRepository.findById(request.getCustomerId())).thenReturn(Optional.of(customer));
        when(vehicleRepository.findById(request.getVehicleId())).thenReturn(Optional.of(vehicle));
        when(rentalRepository.existsConflictForVehicle(vehicle.getId(), request.getStartDate(), request.getEndDate(), RentalStatus.ACTIVE))
                .thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> rentalService.createRental(request));
        verify(rentalRepository, never()).saveAndFlush(any(Rental.class));
    }

    @Test
    void createRental_shouldCalculateTotalUsingInclusiveDays_whenRequestIsValid() {
        Customer customer = buildCustomer();
        Vehicle vehicle = buildVehicle(VehicleStatus.AVAILABLE);
        RentalRequestDTO request = validRentalRequest();

        when(customerRepository.findById(request.getCustomerId())).thenReturn(Optional.of(customer));
        when(vehicleRepository.findById(request.getVehicleId())).thenReturn(Optional.of(vehicle));
        when(rentalRepository.existsConflictForVehicle(vehicle.getId(), request.getStartDate(), request.getEndDate(), RentalStatus.ACTIVE))
                .thenReturn(false);
        when(rentalRepository.saveAndFlush(any(Rental.class))).thenAnswer(invocation -> {
            Rental rental = invocation.getArgument(0);
            rental.setId(1L);
            return rental;
        });

        RentalResponseDTO response = rentalService.createRental(request);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals(BigDecimal.valueOf(600), response.getTotalValue());
        assertEquals("ACTIVE", response.getStatus());
    }

    @Test
    void cancelRental_shouldThrowValidationException_whenRentalAlreadyCanceled() {
        Rental rental = Rental.builder()
                .id(1L)
                .customer(buildCustomer())
                .vehicle(buildVehicle(VehicleStatus.AVAILABLE))
                .status(RentalStatus.CANCELED)
                .build();
        when(rentalRepository.findById(1L)).thenReturn(Optional.of(rental));

        assertThrows(ValidationException.class, () -> rentalService.cancelRental(1L));
        verify(rentalRepository, never()).save(any(Rental.class));
    }

    @Test
    void cancelRental_shouldCancelActiveRental() {
        Rental rental = Rental.builder()
                .id(1L)
                .customer(buildCustomer())
                .vehicle(buildVehicle(VehicleStatus.AVAILABLE))
                .startDate(LocalDate.of(2026, 4, 15))
                .endDate(LocalDate.of(2026, 4, 17))
                .totalValue(BigDecimal.valueOf(600))
                .status(RentalStatus.ACTIVE)
                .build();
        when(rentalRepository.findById(1L)).thenReturn(Optional.of(rental));
        when(rentalRepository.save(any(Rental.class))).thenAnswer(invocation -> invocation.getArgument(0));

        RentalResponseDTO response = rentalService.cancelRental(1L);

        assertEquals("CANCELED", response.getStatus());
        assertNotNull(response.getCanceledAt());
        verify(rentalRepository).save(rental);
    }

    @Test
    void getAvailableVehicles_shouldReturnAvailableVehiclesForPeriod() {
        LocalDate startDate = LocalDate.of(2026, 4, 15);
        LocalDate endDate = LocalDate.of(2026, 4, 18);
        Pageable pageable = PageRequest.of(0, 10);

        Vehicle availableVehicle = buildVehicle(VehicleStatus.AVAILABLE);
        Page<Vehicle> page = new PageImpl<>(List.of(availableVehicle), pageable, 1);

        when(vehicleRepository.findAvailableByPeriod(startDate, endDate, VehicleStatus.AVAILABLE, RentalStatus.ACTIVE, pageable))
                .thenReturn(page);

        Page<VehicleResponseDTO> response = rentalService.getAvailableVehicles(startDate, endDate, pageable);

        assertEquals(1, response.getTotalElements());
        assertEquals("ABC1234", response.getContent().get(0).getPlate());
    }

    private RentalRequestDTO validRentalRequest() {
        return RentalRequestDTO.builder()
                .customerId(1L)
                .vehicleId(2L)
                .startDate(LocalDate.of(2026, 4, 15))
                .endDate(LocalDate.of(2026, 4, 17))
                .build();
    }

    private Customer buildCustomer() {
        return Customer.builder()
                .id(1L)
                .name("Joao Silva")
                .cpf("12345678901")
                .email("joao@example.com")
                .phone("11987654321")
                .registrationDate(LocalDate.of(2024, 1, 15))
                .build();
    }

    private Vehicle buildVehicle(VehicleStatus status) {
        return Vehicle.builder()
                .id(2L)
                .brand("Toyota")
                .model("Corolla")
                .plate("ABC1234")
                .year(2023)
                .dailyValue(BigDecimal.valueOf(200))
                .status(status)
                .build();
    }
}
