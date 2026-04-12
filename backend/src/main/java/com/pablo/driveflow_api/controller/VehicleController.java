package com.pablo.driveflow_api.controller;

import com.pablo.driveflow_api.dto.VehicleRequestDTO;
import com.pablo.driveflow_api.dto.VehicleResponseDTO;
import com.pablo.driveflow_api.model.VehicleStatus;
import com.pablo.driveflow_api.service.VehicleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/vehicles")
@RequiredArgsConstructor
public class VehicleController {

    private final VehicleService vehicleService;

    @PostMapping
    public ResponseEntity<VehicleResponseDTO> createVehicle(@Valid @RequestBody VehicleRequestDTO requestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(vehicleService.createVehicle(requestDTO));
    }

    @GetMapping
    public ResponseEntity<Page<VehicleResponseDTO>> getAllVehicles(Pageable pageable) {
        return ResponseEntity.ok(vehicleService.getAllVehicles(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<VehicleResponseDTO> getVehicleById(@PathVariable Long id) {
        return ResponseEntity.ok(vehicleService.getVehicleById(id));
    }

    @GetMapping("/plate/{plate}")
    public ResponseEntity<VehicleResponseDTO> getVehicleByPlate(@PathVariable String plate) {
        return ResponseEntity.ok(vehicleService.getVehicleByPlate(plate));
    }

    @PutMapping("/{id}")
    public ResponseEntity<VehicleResponseDTO> updateVehicle(
            @PathVariable Long id,
            @Valid @RequestBody VehicleRequestDTO requestDTO) {
        return ResponseEntity.ok(vehicleService.updateVehicle(id, requestDTO));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<VehicleResponseDTO> updateVehicleStatus(
            @PathVariable Long id,
            @RequestParam VehicleStatus status) {
        return ResponseEntity.ok(vehicleService.updateVehicleStatus(id, status));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVehicle(@PathVariable Long id) {
        vehicleService.deleteVehicle(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/count/total")
    public ResponseEntity<Map<String, Long>> countVehicles() {
        Map<String, Long> response = new HashMap<>();
        response.put("total", vehicleService.countVehicles());
        return ResponseEntity.ok(response);
    }
}



