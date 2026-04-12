package com.pablo.driveflow_api.service;

import com.pablo.driveflow_api.dto.VehicleRequestDTO;
import com.pablo.driveflow_api.dto.VehicleResponseDTO;
import com.pablo.driveflow_api.exception.DuplicateResourceException;
import com.pablo.driveflow_api.exception.ResourceNotFoundException;
import com.pablo.driveflow_api.mapper.VehicleMapper;
import com.pablo.driveflow_api.model.Vehicle;
import com.pablo.driveflow_api.model.VehicleStatus;
import com.pablo.driveflow_api.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class VehicleService {

    private final VehicleRepository vehicleRepository;

    public VehicleResponseDTO createVehicle(VehicleRequestDTO requestDTO) {
        if (vehicleRepository.existsByPlate(requestDTO.getPlate())) {
            throw new DuplicateResourceException("Veículo", "placa", requestDTO.getPlate());
        }

        Vehicle vehicle = VehicleMapper.toEntity(requestDTO);
        return VehicleMapper.toDTO(vehicleRepository.save(vehicle));
    }

    @Transactional(readOnly = true)
    public VehicleResponseDTO getVehicleById(Long id) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Veículo", "ID", id));
        return VehicleMapper.toDTO(vehicle);
    }

    @Transactional(readOnly = true)
    public VehicleResponseDTO getVehicleByPlate(String plate) {
        Vehicle vehicle = vehicleRepository.findByPlate(plate)
                .orElseThrow(() -> new ResourceNotFoundException("Veículo", "placa", plate));
        return VehicleMapper.toDTO(vehicle);
    }

    @Transactional(readOnly = true)
    public Page<VehicleResponseDTO> getAllVehicles(Pageable pageable) {
        return vehicleRepository.findAll(pageable)
                .map(VehicleMapper::toDTO);
    }

    public VehicleResponseDTO updateVehicle(Long id, VehicleRequestDTO requestDTO) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Veículo", "ID", id));

        if (!vehicle.getPlate().equals(requestDTO.getPlate()) &&
                vehicleRepository.existsByPlate(requestDTO.getPlate())) {
            throw new DuplicateResourceException("Veículo", "placa", requestDTO.getPlate());
        }

        vehicle.setBrand(requestDTO.getBrand());
        vehicle.setModel(requestDTO.getModel());
        vehicle.setPlate(requestDTO.getPlate());
        vehicle.setYear(requestDTO.getYear());
        vehicle.setDailyValue(requestDTO.getDailyValue());

        return VehicleMapper.toDTO(vehicleRepository.save(vehicle));
    }

    public void deleteVehicle(Long id) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Veículo", "ID", id));
        
        vehicle.setDeletedAt(LocalDateTime.now());
        vehicleRepository.save(vehicle);
    }

    public VehicleResponseDTO updateVehicleStatus(Long id, VehicleStatus status) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Veículo", "ID", id));

        vehicle.setStatus(status);
        return VehicleMapper.toDTO(vehicleRepository.save(vehicle));
    }

    @Transactional(readOnly = true)
    public long countVehicles() {
        return vehicleRepository.count();
    }
}






