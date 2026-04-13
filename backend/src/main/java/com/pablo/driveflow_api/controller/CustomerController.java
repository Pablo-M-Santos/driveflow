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
@Tag(name = "Customers", description = "Operações de gestão de clientes - CRUD completo com validação de CPF, email único e soft delete")
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping
    @Operation(summary = "Criar novo cliente", description = "Cadastra um novo cliente no sistema. CPF e email devem ser únicos.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Cliente criado com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CustomerResponseDTO.class))),
            @ApiResponse(responseCode = "409", description = "CPF ou email já cadastrado no sistema"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos - Validação falhou")
    })
    public ResponseEntity<CustomerResponseDTO> createCustomer(@Valid @RequestBody CustomerRequestDTO requestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(customerService.createCustomer(requestDTO));
    }

    @GetMapping
    @Operation(summary = "Listar todos os clientes", description = "Retorna uma lista paginada de todos os clientes cadastrados (excluindo deletados)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de clientes recuperada com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = PageResponse.class)))
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
    @Operation(summary = "Buscar cliente por ID", description = "Retorna os detalhes de um cliente específico pelo ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cliente encontrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CustomerResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    })
    public ResponseEntity<CustomerResponseDTO> getCustomerById(
            @Parameter(description = "ID do cliente")
            @PathVariable Long id) {
        return ResponseEntity.ok(customerService.getCustomerById(id));
    }

    @GetMapping("/cpf/{cpf}")
    @Operation(summary = "Buscar cliente por CPF", description = "Retorna os detalhes de um cliente pelo seu CPF")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cliente encontrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CustomerResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Cliente com esse CPF não encontrado")
    })
    public ResponseEntity<CustomerResponseDTO> getCustomerByCpf(
            @Parameter(description = "CPF do cliente (11 dígitos)")
            @PathVariable String cpf) {
        return ResponseEntity.ok(customerService.getCustomerByCpf(cpf));
    }

    @GetMapping("/email/{email}")
    @Operation(summary = "Buscar cliente por email", description = "Retorna os detalhes de um cliente pelo seu email")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cliente encontrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CustomerResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Cliente com esse email não encontrado")
    })
    public ResponseEntity<CustomerResponseDTO> getCustomerByEmail(
            @Parameter(description = "Email do cliente")
            @PathVariable String email) {
        return ResponseEntity.ok(customerService.getCustomerByEmail(email));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar cliente completo", description = "Atualiza todos os dados de um cliente existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cliente atualizado com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CustomerResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado"),
            @ApiResponse(responseCode = "409", description = "CPF ou email já cadastrado em outro cliente"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos - Validação falhou")
    })
    public ResponseEntity<CustomerResponseDTO> updateCustomer(
            @Parameter(description = "ID do cliente a atualizar")
            @PathVariable Long id,
            @Valid @RequestBody CustomerRequestDTO requestDTO) {
        return ResponseEntity.ok(customerService.updateCustomer(id, requestDTO));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar cliente", description = "Realiza soft delete do cliente (marca como deletado sem remover do banco)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Cliente deletado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    })
    public ResponseEntity<Void> deleteCustomer(
            @Parameter(description = "ID do cliente a deletar")
            @PathVariable Long id) {
        customerService.deleteCustomer(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/count/total")
    @Operation(summary = "Contar total de clientes", description = "Retorna o número total de clientes cadastrados e não deletados")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Total contado com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class)))
    })
    public ResponseEntity<Map<String, Long>> countCustomers() {
        Map<String, Long> response = new HashMap<>();
        response.put("total", customerService.countCustomers());
        return ResponseEntity.ok(response);
    }
}

