package com.pablo.driveflow_api.service;

import com.pablo.driveflow_api.dto.CustomerRequestDTO;
import com.pablo.driveflow_api.dto.CustomerResponseDTO;
import com.pablo.driveflow_api.exception.DuplicateResourceException;
import com.pablo.driveflow_api.exception.ResourceNotFoundException;
import com.pablo.driveflow_api.model.Customer;
import com.pablo.driveflow_api.repository.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerService customerService;

    @Test
    void createCustomer_shouldThrowDuplicateResourceException_whenCpfAlreadyExists() {
        CustomerRequestDTO request = buildCustomerRequest();

        when(customerRepository.existsByCpf(request.getCpf())).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> customerService.createCustomer(request));
        verify(customerRepository, never()).saveAndFlush(any(Customer.class));
    }

    @Test
    void createCustomer_shouldCreateCustomer_whenDataIsValid() {
        CustomerRequestDTO request = buildCustomerRequest();

        when(customerRepository.existsByCpf(request.getCpf())).thenReturn(false);
        when(customerRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(customerRepository.saveAndFlush(any(Customer.class))).thenAnswer(invocation -> {
            Customer customer = invocation.getArgument(0);
            customer.setId(1L);
            return customer;
        });

        CustomerResponseDTO response = customerService.createCustomer(request);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals(request.getName(), response.getName());
        assertEquals(request.getCpf(), response.getCpf());
        assertEquals(request.getEmail(), response.getEmail());
        verify(customerRepository).saveAndFlush(any(Customer.class));
    }

    @Test
    void updateCustomer_shouldThrowResourceNotFoundException_whenCustomerDoesNotExist() {
        CustomerRequestDTO request = buildCustomerRequest();
        when(customerRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> customerService.updateCustomer(999L, request));
        verify(customerRepository, never()).saveAndFlush(any(Customer.class));
    }

    @Test
    void updateCustomer_shouldThrowDuplicateResourceException_whenEmailChangesToExistingOne() {
        Customer existing = buildCustomerEntity();
        CustomerRequestDTO updateRequest = CustomerRequestDTO.builder()
                .name("Maria Souza")
                .cpf(existing.getCpf())
                .email("other@example.com")
                .phone("11999999999")
                .registrationDate(LocalDate.of(2024, 2, 1))
                .build();

        when(customerRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(customerRepository.existsByEmail(updateRequest.getEmail())).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> customerService.updateCustomer(1L, updateRequest));
        verify(customerRepository, never()).saveAndFlush(any(Customer.class));
    }

    @Test
    void deleteCustomer_shouldMarkDeletedAtAndSaveCustomer() {
        Customer customer = buildCustomerEntity();
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));

        customerService.deleteCustomer(1L);

        assertNotNull(customer.getDeletedAt());
        verify(customerRepository).save(customer);
    }

    private CustomerRequestDTO buildCustomerRequest() {
        return CustomerRequestDTO.builder()
                .name("Joao Silva")
                .cpf("12345678901")
                .email("joao@example.com")
                .phone("11987654321")
                .registrationDate(LocalDate.of(2024, 1, 15))
                .build();
    }

    private Customer buildCustomerEntity() {
        return Customer.builder()
                .id(1L)
                .name("Joao Silva")
                .cpf("12345678901")
                .email("joao@example.com")
                .phone("11987654321")
                .registrationDate(LocalDate.of(2024, 1, 15))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
}

