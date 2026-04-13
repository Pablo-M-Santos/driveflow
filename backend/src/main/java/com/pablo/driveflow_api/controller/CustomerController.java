package com.pablo.driveflow_api.controller;

import com.pablo.driveflow_api.dto.CustomerRequestDTO;
import com.pablo.driveflow_api.dto.CustomerResponseDTO;
import com.pablo.driveflow_api.dto.PageResponse;
import com.pablo.driveflow_api.service.CustomerService;
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
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
@Tag(name = "Customers", description = "Customer management operations - Complete CRUD with CPF validation, unique email and soft delete")
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping
    @Operation(summary = "Create new customer", description = "Registers a new customer in the system. CPF and email must be unique.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Customer created successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CustomerResponseDTO.class))),
            @ApiResponse(responseCode = "409", description = "CPF or email already registered in the system"),
            @ApiResponse(responseCode = "400", description = "Invalid data - Validation failed")
    })
    public ResponseEntity<CustomerResponseDTO> createCustomer(@Valid @RequestBody CustomerRequestDTO requestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(customerService.createCustomer(requestDTO));
    }

    @GetMapping
    @Operation(summary = "List all customers", description = "Returns a paginated list of all registered customers (excluding deleted)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Customer list retrieved successfully",
                    content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<PageResponse<CustomerResponseDTO>> getAllCustomers(
            @ParameterObject Pageable pageable) {
        Page<CustomerResponseDTO> page = customerService.getAllCustomers(pageable);
        return ResponseEntity.ok(PageResponse.of(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalPages(),
                page.getTotalElements()
        ));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Search customer by ID", description = "Returns the details of a specific customer by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Customer found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CustomerResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Customer not found")
    })
    public ResponseEntity<CustomerResponseDTO> getCustomerById(
            @Parameter(description = "Customer ID")
            @PathVariable Long id) {
        return ResponseEntity.ok(customerService.getCustomerById(id));
    }

    @GetMapping("/cpf/{cpf}")
    @Operation(summary = "Search customer by CPF", description = "Returns the details of a customer by their CPF")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Customer found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CustomerResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Customer with this CPF not found")
    })
    public ResponseEntity<CustomerResponseDTO> getCustomerByCpf(
            @Parameter(description = "Customer CPF (11 digits)")
            @PathVariable String cpf) {
        return ResponseEntity.ok(customerService.getCustomerByCpf(cpf));
    }

    @GetMapping("/email/{email}")
    @Operation(summary = "Search customer by email", description = "Returns the details of a customer by their email")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Customer found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CustomerResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Customer with this email not found")
    })
    public ResponseEntity<CustomerResponseDTO> getCustomerByEmail(
            @Parameter(description = "Customer email")
            @PathVariable String email) {
        return ResponseEntity.ok(customerService.getCustomerByEmail(email));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update complete customer", description = "Updates all data of an existing customer")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Customer updated successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CustomerResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Customer not found"),
            @ApiResponse(responseCode = "409", description = "CPF or email already registered to another customer"),
            @ApiResponse(responseCode = "400", description = "Invalid data - Validation failed")
    })
    public ResponseEntity<CustomerResponseDTO> updateCustomer(
            @Parameter(description = "Customer ID to update")
            @PathVariable Long id,
            @Valid @RequestBody CustomerRequestDTO requestDTO) {
        return ResponseEntity.ok(customerService.updateCustomer(id, requestDTO));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete customer", description = "Performs soft delete of the customer (marks as deleted without removing from database)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Customer deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Customer not found")
    })
    public ResponseEntity<Void> deleteCustomer(
            @Parameter(description = "Customer ID to delete")
            @PathVariable Long id) {
        customerService.deleteCustomer(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/count/total")
    @Operation(summary = "Count total customers", description = "Returns the total number of registered and non-deleted customers")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Total counted successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class)))
    })
    public ResponseEntity<Map<String, Long>> countCustomers() {
        Map<String, Long> response = new HashMap<>();
        response.put("total", customerService.countCustomers());
        return ResponseEntity.ok(response);
    }
}

