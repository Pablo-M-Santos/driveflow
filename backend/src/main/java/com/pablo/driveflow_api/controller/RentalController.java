package com.pablo.driveflow_api.controller;

import com.pablo.driveflow_api.dto.PageResponse;
import com.pablo.driveflow_api.dto.RentalRequestDTO;
import com.pablo.driveflow_api.dto.RentalResponseDTO;
import com.pablo.driveflow_api.dto.VehicleResponseDTO;
import com.pablo.driveflow_api.service.RentalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/rentals")
@RequiredArgsConstructor
@Tag(name = "Rentals", description = "Vehicle rental operations with conflict validation and total value calculation")
public class RentalController {

    private final RentalService rentalService;

    @PostMapping
    @Operation(summary = "Register rental", description = "Creates a new rental validating period conflict and vehicle availability")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Rental registered successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = RentalResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid data or conflicting period"),
            @ApiResponse(responseCode = "404", description = "Customer or vehicle not found")
    })
    public ResponseEntity<RentalResponseDTO> createRental(@Valid @RequestBody RentalRequestDTO requestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(rentalService.createRental(requestDTO));
    }

    @GetMapping
    @Operation(summary = "List rentals", description = "Returns a paginated list of rentals")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rental list retrieved",
                    content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<PageResponse<RentalResponseDTO>> getAllRentals(@ParameterObject Pageable pageable) {
        Page<RentalResponseDTO> page = rentalService.getAllRentals(pageable);
        return ResponseEntity.ok(PageResponse.of(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalPages(),
                page.getTotalElements()
        ));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Search rental by ID", description = "Returns the details of a specific rental")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rental found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = RentalResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Rental not found")
    })
    public ResponseEntity<RentalResponseDTO> getRentalById(
            @Parameter(description = "Rental ID")
            @PathVariable Long id) {
        return ResponseEntity.ok(rentalService.getRentalById(id));
    }

    @GetMapping("/customer/{customerId}")
    @Operation(summary = "Customer history", description = "Returns the paginated rental history for a customer")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "History retrieved",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Customer not found")
    })
    public ResponseEntity<PageResponse<RentalResponseDTO>> getRentalsByCustomer(
            @Parameter(description = "Customer ID")
            @PathVariable Long customerId,
            @ParameterObject Pageable pageable) {
        Page<RentalResponseDTO> page = rentalService.getRentalsByCustomer(customerId, pageable);
        return ResponseEntity.ok(PageResponse.of(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalPages(),
                page.getTotalElements()
        ));
    }

    @PatchMapping("/{id}/cancel")
    @Operation(summary = "Cancel rental", description = "Cancels an active rental")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rental canceled successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = RentalResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Rental already canceled or finished"),
            @ApiResponse(responseCode = "404", description = "Rental not found")
    })
    public ResponseEntity<RentalResponseDTO> cancelRental(
            @Parameter(description = "Rental ID")
            @PathVariable Long id) {
        return ResponseEntity.ok(rentalService.cancelRental(id));
    }

    @GetMapping("/vehicles/available")
    @Operation(summary = "Available vehicles by period", description = "Lists vehicles available for a specific period")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Available vehicles retrieved",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Invalid period")
    })
    public ResponseEntity<PageResponse<VehicleResponseDTO>> getAvailableVehicles(
            @Parameter(description = "Start date in yyyy-MM-dd format", example = "2026-04-15")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "End date in yyyy-MM-dd format", example = "2026-04-18")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @ParameterObject Pageable pageable) {
        Page<VehicleResponseDTO> page = rentalService.getAvailableVehicles(startDate, endDate, pageable);
        return ResponseEntity.ok(PageResponse.of(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalPages(),
                page.getTotalElements()
        ));
    }

    @GetMapping("/count/total")
    @Operation(summary = "Count rentals", description = "Returns the total number of registered rentals")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Total rentals retrieved",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class)))
    })
    public ResponseEntity<Map<String, Long>> countRentals() {
        Map<String, Long> response = new HashMap<>();
        response.put("total", rentalService.countRentals());
        return ResponseEntity.ok(response);
    }
}

