package com.pablo.driveflow_api.controller;

import com.pablo.driveflow_api.dto.PageResponse;
import com.pablo.driveflow_api.dto.VehicleRequestDTO;
import com.pablo.driveflow_api.dto.VehicleResponseDTO;
import com.pablo.driveflow_api.model.VehicleStatus;
import com.pablo.driveflow_api.service.VehicleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
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
@Tag(name = "Vehicles", description = "Vehicle management operations - Complete CRUD with duplication validation and status control")
public class VehicleController {

    private final VehicleService vehicleService;

    @PostMapping
    @Operation(summary = "Create new vehicle", description = "Registers a new vehicle in the system. Plate must be unique.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Vehicle created successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = VehicleResponseDTO.class))),
            @ApiResponse(responseCode = "409", description = "Plate already registered in the system"),
            @ApiResponse(responseCode = "400", description = "Invalid data - Validation failed")
    })
    public ResponseEntity<VehicleResponseDTO> createVehicle(@Valid @RequestBody VehicleRequestDTO requestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(vehicleService.createVehicle(requestDTO));
    }

    @GetMapping
    @Operation(summary = "List all vehicles", description = "Returns a paginated list of all registered vehicles (excluding deleted)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Vehicle list retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = PageResponse.class)))
    })
    public ResponseEntity<PageResponse<VehicleResponseDTO>> getAllVehicles(
            @ParameterObject Pageable pageable) {
        Page<VehicleResponseDTO> page = vehicleService.getAllVehicles(pageable);
        return ResponseEntity.ok(PageResponse.of(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalPages(),
                page.getTotalElements()
        ));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Search vehicle by ID", description = "Returns the details of a specific vehicle by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Vehicle found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = VehicleResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Vehicle not found")
    })
    public ResponseEntity<VehicleResponseDTO> getVehicleById(
            @Parameter(description = "Vehicle ID")
            @PathVariable Long id) {
        return ResponseEntity.ok(vehicleService.getVehicleById(id));
    }

    @GetMapping("/plate/{plate}")
    @Operation(summary = "Search vehicle by plate", description = "Returns the details of a vehicle by its plate")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Vehicle found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = VehicleResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Vehicle with this plate not found")
    })
    public ResponseEntity<VehicleResponseDTO> getVehicleByPlate(
            @Parameter(description = "Vehicle plate (e.g., ABC1234)")
            @PathVariable String plate) {
        return ResponseEntity.ok(vehicleService.getVehicleByPlate(plate));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update complete vehicle", description = "Updates all data of an existing vehicle")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Vehicle updated successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = VehicleResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Vehicle not found"),
            @ApiResponse(responseCode = "409", description = "Plate already registered on another vehicle"),
            @ApiResponse(responseCode = "400", description = "Invalid data - Validation failed")
    })
    public ResponseEntity<VehicleResponseDTO> updateVehicle(
            @Parameter(description = "Vehicle ID to update")
            @PathVariable Long id,
            @Valid @RequestBody VehicleRequestDTO requestDTO) {
        return ResponseEntity.ok(vehicleService.updateVehicle(id, requestDTO));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update vehicle status", description = "Changes only the vehicle status (AVAILABLE or UNAVAILABLE)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Status updated successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = VehicleResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Vehicle not found"),
            @ApiResponse(responseCode = "400", description = "Invalid status")
    })
    public ResponseEntity<VehicleResponseDTO> updateVehicleStatus(
            @Parameter(description = "Vehicle ID")
            @PathVariable Long id,
            @Parameter(description = "New status: AVAILABLE or UNAVAILABLE")
            @RequestParam VehicleStatus status) {
        return ResponseEntity.ok(vehicleService.updateVehicleStatus(id, status));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete vehicle", description = "Performs soft delete of the vehicle (marks as deleted without removing from database)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Vehicle deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Vehicle not found")
    })
    public ResponseEntity<Void> deleteVehicle(
            @Parameter(description = "Vehicle ID to delete")
            @PathVariable Long id) {
        vehicleService.deleteVehicle(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/count/total")
    @Operation(summary = "Count total vehicles", description = "Returns the total number of registered and non-deleted vehicles")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Total counted successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class)))
    })
    public ResponseEntity<Map<String, Long>> countVehicles() {
        Map<String, Long> response = new HashMap<>();
        response.put("total", vehicleService.countVehicles());
        return ResponseEntity.ok(response);
    }
}



