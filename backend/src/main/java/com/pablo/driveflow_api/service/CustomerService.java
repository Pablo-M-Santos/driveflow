package com.pablo.driveflow_api.service;

import com.pablo.driveflow_api.dto.CustomerRequestDTO;
import com.pablo.driveflow_api.dto.CustomerResponseDTO;
import com.pablo.driveflow_api.exception.DuplicateResourceException;
import com.pablo.driveflow_api.exception.ResourceNotFoundException;
import com.pablo.driveflow_api.mapper.CustomerMapper;
import com.pablo.driveflow_api.model.Customer;
import com.pablo.driveflow_api.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerResponseDTO createCustomer(CustomerRequestDTO requestDTO) {
        if (customerRepository.existsByCpf(requestDTO.getCpf())) {
            throw new DuplicateResourceException("Customer", "CPF", requestDTO.getCpf());
        }

        if (customerRepository.existsByEmail(requestDTO.getEmail())) {
            throw new DuplicateResourceException("Customer", "email", requestDTO.getEmail());
        }

        Customer customer = CustomerMapper.toEntity(requestDTO);
        return CustomerMapper.toDTO(customerRepository.saveAndFlush(customer));
    }

    @Transactional(readOnly = true)
    public CustomerResponseDTO getCustomerById(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "ID", id));
        return CustomerMapper.toDTO(customer);
    }

    @Transactional(readOnly = true)
    public CustomerResponseDTO getCustomerByCpf(String cpf) {
        Customer customer = customerRepository.findByCpf(cpf)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "CPF", cpf));
        return CustomerMapper.toDTO(customer);
    }

    @Transactional(readOnly = true)
    public CustomerResponseDTO getCustomerByEmail(String email) {
        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "email", email));
        return CustomerMapper.toDTO(customer);
    }

    @Transactional(readOnly = true)
    public Page<CustomerResponseDTO> getAllCustomers(Pageable pageable) {
        return customerRepository.findAll(pageable)
                .map(CustomerMapper::toDTO);
    }

    public CustomerResponseDTO updateCustomer(Long id, CustomerRequestDTO requestDTO) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "ID", id));

        if (!customer.getCpf().equals(requestDTO.getCpf()) &&
                customerRepository.existsByCpf(requestDTO.getCpf())) {
            throw new DuplicateResourceException("Customer", "CPF", requestDTO.getCpf());
        }

        if (!customer.getEmail().equals(requestDTO.getEmail()) &&
                customerRepository.existsByEmail(requestDTO.getEmail())) {
            throw new DuplicateResourceException("Customer", "email", requestDTO.getEmail());
        }

        customer.setName(requestDTO.getName());
        customer.setCpf(requestDTO.getCpf());
        customer.setEmail(requestDTO.getEmail());
        customer.setPhone(requestDTO.getPhone());
        customer.setRegistrationDate(requestDTO.getRegistrationDate());

        return CustomerMapper.toDTO(customerRepository.saveAndFlush(customer));
    }

    public void deleteCustomer(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "ID", id));

        customer.setDeletedAt(LocalDateTime.now());
        customerRepository.save(customer);
    }

    @Transactional(readOnly = true)
    public long countCustomers() {
        return customerRepository.count();
    }
}

