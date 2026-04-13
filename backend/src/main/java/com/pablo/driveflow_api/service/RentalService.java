package com.pablo.driveflow_api.service;

import com.pablo.driveflow_api.dto.RentalRequestDTO;
import com.pablo.driveflow_api.dto.RentalResponseDTO;
import com.pablo.driveflow_api.dto.VehicleResponseDTO;
import com.pablo.driveflow_api.exception.DuplicateResourceException;
import com.pablo.driveflow_api.exception.ResourceNotFoundException;
import com.pablo.driveflow_api.exception.ValidationException;
import com.pablo.driveflow_api.mapper.RentalMapper;
import com.pablo.driveflow_api.mapper.VehicleMapper;
import com.pablo.driveflow_api.model.*;
import com.pablo.driveflow_api.repository.CustomerRepository;
import com.pablo.driveflow_api.repository.RentalRepository;
import com.pablo.driveflow_api.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
@Transactional
public class RentalService {

    private final RentalRepository rentalRepository;
    private final CustomerRepository customerRepository;
    private final VehicleRepository vehicleRepository;

    public RentalResponseDTO createRental(RentalRequestDTO requestDTO) {
        validateDates(requestDTO.getStartDate(), requestDTO.getEndDate());

        Customer customer = customerRepository.findById(requestDTO.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "ID", requestDTO.getCustomerId()));

        Vehicle vehicle = vehicleRepository.findById(requestDTO.getVehicleId())
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle", "ID", requestDTO.getVehicleId()));

        if (vehicle.getStatus() != VehicleStatus.AVAILABLE) {
            throw new ValidationException("Vehicle is unavailable for rental");
        }

        boolean hasConflict = rentalRepository.existsConflictForVehicle(
                vehicle.getId(),
                requestDTO.getStartDate(),
                requestDTO.getEndDate(),
                RentalStatus.ACTIVE
        );

        if (hasConflict) {
            throw new DuplicateResourceException("Vehicle is already reserved for the requested period");
        }

        Rental rental = Rental.builder()
                .customer(customer)
                .vehicle(vehicle)
                .startDate(requestDTO.getStartDate())
                .endDate(requestDTO.getEndDate())
                .totalValue(calculateTotalValue(vehicle.getDailyValue(), requestDTO.getStartDate(), requestDTO.getEndDate()))
                .status(RentalStatus.ACTIVE)
                .build();

        return RentalMapper.toDTO(rentalRepository.saveAndFlush(rental));
    }

    @Transactional(readOnly = true)
    public RentalResponseDTO getRentalById(Long id) {
        Rental rental = rentalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rental", "ID", id));
        return RentalMapper.toDTO(rental);
    }

    @Transactional(readOnly = true)
    public Page<RentalResponseDTO> getAllRentals(Pageable pageable) {
        return rentalRepository.findAll(pageable).map(RentalMapper::toDTO);
    }

    @Transactional(readOnly = true)
    public Page<RentalResponseDTO> getRentalsByCustomer(Long customerId, Pageable pageable) {
        customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "ID", customerId));

        return rentalRepository.findAllByCustomerId(customerId, pageable)
                .map(RentalMapper::toDTO);
    }

    public RentalResponseDTO cancelRental(Long id) {
        Rental rental = rentalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rental", "ID", id));

        if (rental.getStatus() == RentalStatus.CANCELED) {
            throw new ValidationException("Rental is already canceled");
        }

        if (rental.getStatus() == RentalStatus.FINISHED) {
            throw new ValidationException("Finished rental cannot be canceled");
        }

        rental.setStatus(RentalStatus.CANCELED);
        rental.setCanceledAt(LocalDateTime.now());

        return RentalMapper.toDTO(rentalRepository.save(rental));
    }

    @Transactional(readOnly = true)
    public Page<VehicleResponseDTO> getAvailableVehicles(LocalDate startDate, LocalDate endDate, Pageable pageable) {
        validateDates(startDate, endDate);

        return vehicleRepository.findAvailableByPeriod(
                        startDate,
                        endDate,
                        VehicleStatus.AVAILABLE,
                        RentalStatus.ACTIVE,
                        pageable
                )
                .map(VehicleMapper::toDTO);
    }

    @Transactional(readOnly = true)
    public long countRentals() {
        return rentalRepository.count();
    }

    private void validateDates(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            throw new ValidationException("Start and end dates are mandatory");
        }

        if (endDate.isBefore(startDate)) {
            throw new ValidationException("End date cannot be before start date");
        }
    }

    private BigDecimal calculateTotalValue(BigDecimal dailyValue, LocalDate startDate, LocalDate endDate) {
        long days = ChronoUnit.DAYS.between(startDate, endDate) + 1;
        return dailyValue.multiply(BigDecimal.valueOf(days));
    }
}

