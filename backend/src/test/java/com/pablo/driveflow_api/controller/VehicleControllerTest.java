package com.pablo.driveflow_api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pablo.driveflow_api.dto.VehicleRequestDTO;
import com.pablo.driveflow_api.dto.VehicleResponseDTO;
import com.pablo.driveflow_api.exception.DuplicateResourceException;
import com.pablo.driveflow_api.exception.ResourceNotFoundException;
import com.pablo.driveflow_api.model.VehicleStatus;
import com.pablo.driveflow_api.service.VehicleService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(VehicleController.class)
class VehicleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private VehicleService vehicleService;

    @Test
    void createVehicle_shouldReturn201_whenRequestIsValid() throws Exception {
        VehicleRequestDTO request = buildValidRequest();
        VehicleResponseDTO response = VehicleResponseDTO.builder()
                .id(1L)
                .brand("Toyota")
                .model("Corolla")
                .plate("ABC1234")
                .year(2023)
                .dailyValue(BigDecimal.valueOf(150))
                .status("AVAILABLE")
                .build();

        when(vehicleService.createVehicle(any(VehicleRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/vehicles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.plate").value("ABC1234"));
    }

    @Test
    void createVehicle_shouldReturn400_whenBodyIsInvalid() throws Exception {
        String invalidPayload = "{}";

        mockMvc.perform(post("/api/v1/vehicles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidPayload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Validation Failed"));
    }

    @Test
    void createVehicle_shouldReturn409_whenPlateAlreadyExists() throws Exception {
        VehicleRequestDTO request = buildValidRequest();
        when(vehicleService.createVehicle(any(VehicleRequestDTO.class)))
                .thenThrow(new DuplicateResourceException("Vehicle", "plate", request.getPlate()));

        mockMvc.perform(post("/api/v1/vehicles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("Duplicate Resource"));
    }

    @Test
    void getVehicleById_shouldReturn404_whenVehicleNotFound() throws Exception {
        when(vehicleService.getVehicleById(99L))
                .thenThrow(new ResourceNotFoundException("Vehicle", "ID", 99L));

        mockMvc.perform(get("/api/v1/vehicles/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Resource Not Found"));
    }

    @Test
    void updateVehicleStatus_shouldReturn200_whenStatusParamIsValid() throws Exception {
        VehicleResponseDTO response = VehicleResponseDTO.builder()
                .id(1L)
                .brand("Toyota")
                .model("Corolla")
                .plate("ABC1234")
                .status("UNAVAILABLE")
                .dailyValue(BigDecimal.valueOf(150))
                .build();

        when(vehicleService.updateVehicleStatus(1L, VehicleStatus.UNAVAILABLE)).thenReturn(response);

        mockMvc.perform(patch("/api/v1/vehicles/1/status").param("status", "UNAVAILABLE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UNAVAILABLE"));
    }

    private VehicleRequestDTO buildValidRequest() {
        return VehicleRequestDTO.builder()
                .brand("Toyota")
                .model("Corolla")
                .plate("ABC1234")
                .year(2023)
                .dailyValue(BigDecimal.valueOf(150))
                .build();
    }
}
