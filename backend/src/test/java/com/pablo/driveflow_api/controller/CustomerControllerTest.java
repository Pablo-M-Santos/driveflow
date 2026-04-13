package com.pablo.driveflow_api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pablo.driveflow_api.dto.CustomerRequestDTO;
import com.pablo.driveflow_api.dto.CustomerResponseDTO;
import com.pablo.driveflow_api.exception.DuplicateResourceException;
import com.pablo.driveflow_api.exception.ResourceNotFoundException;
import com.pablo.driveflow_api.service.CustomerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CustomerController.class)
class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CustomerService customerService;

    @Test
    void createCustomer_shouldReturn201_whenRequestIsValid() throws Exception {
        CustomerRequestDTO request = buildValidRequest();
        CustomerResponseDTO response = CustomerResponseDTO.builder()
                .id(1L)
                .name("Joao Silva")
                .cpf("12345678901")
                .email("joao@example.com")
                .phone("11987654321")
                .registrationDate(LocalDate.of(2024, 1, 15))
                .build();

        when(customerService.createCustomer(any(CustomerRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Joao Silva"));
    }

    @Test
    void createCustomer_shouldReturn400_whenBodyIsInvalid() throws Exception {
        String invalidPayload = "{}";

        mockMvc.perform(post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidPayload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Validation Failed"))
                .andExpect(jsonPath("$.fieldErrors").exists());
    }

    @Test
    void createCustomer_shouldReturn409_whenCpfAlreadyExists() throws Exception {
        CustomerRequestDTO request = buildValidRequest();
        when(customerService.createCustomer(any(CustomerRequestDTO.class)))
                .thenThrow(new DuplicateResourceException("Customer", "CPF", request.getCpf()));

        mockMvc.perform(post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("Duplicate Resource"));
    }

    @Test
    void getCustomerById_shouldReturn404_whenCustomerNotFound() throws Exception {
        when(customerService.getCustomerById(99L))
                .thenThrow(new ResourceNotFoundException("Customer", "ID", 99L));

        mockMvc.perform(get("/api/v1/customers/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Resource Not Found"));
    }

    private CustomerRequestDTO buildValidRequest() {
        return CustomerRequestDTO.builder()
                .name("Joao Silva")
                .cpf("12345678901")
                .email("joao@example.com")
                .phone("11987654321")
                .registrationDate(LocalDate.of(2024, 1, 15))
                .build();
    }
}
