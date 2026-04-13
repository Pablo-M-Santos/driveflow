package com.pablo.driveflow_api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pablo.driveflow_api.dto.RentalRequestDTO;
import com.pablo.driveflow_api.dto.RentalResponseDTO;
import com.pablo.driveflow_api.exception.DuplicateResourceException;
import com.pablo.driveflow_api.exception.ResourceNotFoundException;
import com.pablo.driveflow_api.service.RentalService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RentalController.class)
class RentalControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private RentalService rentalService;

    @Test
    void createRental_shouldReturn201_whenRequestIsValid() throws Exception {
        RentalRequestDTO request = buildValidRequest();
        RentalResponseDTO response = RentalResponseDTO.builder()
                .id(1L)
                .customerId(1L)
                .customerName("Joao Silva")
                .vehicleId(2L)
                .vehiclePlate("ABC1234")
                .startDate(LocalDate.of(2026, 4, 15))
                .endDate(LocalDate.of(2026, 4, 17))
                .totalValue(BigDecimal.valueOf(600))
                .status("ACTIVE")
                .build();

        when(rentalService.createRental(any(RentalRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/rentals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    void createRental_shouldReturn400_whenBodyIsInvalid() throws Exception {
        String invalidPayload = "{}";

        mockMvc.perform(post("/api/v1/rentals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidPayload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Validation Failed"));
    }

    @Test
    void createRental_shouldReturn409_whenPeriodConflictExists() throws Exception {
        RentalRequestDTO request = buildValidRequest();
        when(rentalService.createRental(any(RentalRequestDTO.class)))
                .thenThrow(new DuplicateResourceException("Vehicle is already reserved for the requested period"));

        mockMvc.perform(post("/api/v1/rentals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("Duplicate Resource"));
    }

    @Test
    void cancelRental_shouldReturn404_whenRentalNotFound() throws Exception {
        when(rentalService.cancelRental(77L)).thenThrow(new ResourceNotFoundException("Rental", "ID", 77L));

        mockMvc.perform(patch("/api/v1/rentals/77/cancel"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Resource Not Found"));
    }

    private RentalRequestDTO buildValidRequest() {
        return RentalRequestDTO.builder()
                .customerId(1L)
                .vehicleId(2L)
                .startDate(LocalDate.of(2026, 4, 15))
                .endDate(LocalDate.of(2026, 4, 17))
                .build();
    }
}
