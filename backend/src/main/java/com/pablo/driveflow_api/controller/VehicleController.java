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
@Tag(name = "Vehicles", description = "Operações de gestão de veículos - CRUD completo com validação de duplicidade e controle de status")
public class VehicleController {

    private final VehicleService vehicleService;

    @PostMapping
    @Operation(summary = "Criar novo veículo", description = "Cadastra um novo veículo no sistema. A placa deve ser única.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Veículo criado com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = VehicleResponseDTO.class))),
            @ApiResponse(responseCode = "409", description = "Placa já cadastrada no sistema"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos - Validação falhou")
    })
    public ResponseEntity<VehicleResponseDTO> createVehicle(@Valid @RequestBody VehicleRequestDTO requestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(vehicleService.createVehicle(requestDTO));
    }

    @GetMapping
    @Operation(summary = "Listar todos os veículos", description = "Retorna uma lista paginada de todos os veículos cadastrados (excluindo deletados)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de veículos recuperada com sucesso",
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
    @Operation(summary = "Buscar veículo por ID", description = "Retorna os detalhes de um veículo específico pelo ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Veículo encontrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = VehicleResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Veículo não encontrado")
    })
    public ResponseEntity<VehicleResponseDTO> getVehicleById(
            @Parameter(description = "ID do veículo")
            @PathVariable Long id) {
        return ResponseEntity.ok(vehicleService.getVehicleById(id));
    }

    @GetMapping("/plate/{plate}")
    @Operation(summary = "Buscar veículo por placa", description = "Retorna os detalhes de um veículo pela sua placa")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Veículo encontrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = VehicleResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Veículo com essa placa não encontrado")
    })
    public ResponseEntity<VehicleResponseDTO> getVehicleByPlate(
            @Parameter(description = "Placa do veículo (ex: ABC1234)")
            @PathVariable String plate) {
        return ResponseEntity.ok(vehicleService.getVehicleByPlate(plate));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar veículo completo", description = "Atualiza todos os dados de um veículo existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Veículo atualizado com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = VehicleResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Veículo não encontrado"),
            @ApiResponse(responseCode = "409", description = "Placa já cadastrada em outro veículo"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos - Validação falhou")
    })
    public ResponseEntity<VehicleResponseDTO> updateVehicle(
            @Parameter(description = "ID do veículo a atualizar")
            @PathVariable Long id,
            @Valid @RequestBody VehicleRequestDTO requestDTO) {
        return ResponseEntity.ok(vehicleService.updateVehicle(id, requestDTO));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Atualizar status do veículo", description = "Altera apenas o status do veículo (AVAILABLE ou UNAVAILABLE)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Status atualizado com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = VehicleResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Veículo não encontrado"),
            @ApiResponse(responseCode = "400", description = "Status inválido")
    })
    public ResponseEntity<VehicleResponseDTO> updateVehicleStatus(
            @Parameter(description = "ID do veículo")
            @PathVariable Long id,
            @Parameter(description = "Novo status: AVAILABLE ou UNAVAILABLE")
            @RequestParam VehicleStatus status) {
        return ResponseEntity.ok(vehicleService.updateVehicleStatus(id, status));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar veículo", description = "Realiza soft delete do veículo (marca como deletado sem remover do banco)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Veículo deletado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Veículo não encontrado")
    })
    public ResponseEntity<Void> deleteVehicle(
            @Parameter(description = "ID do veículo a deletar")
            @PathVariable Long id) {
        vehicleService.deleteVehicle(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/count/total")
    @Operation(summary = "Contar total de veículos", description = "Retorna o número total de veículos cadastrados e não deletados")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Total contado com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class)))
    })
    public ResponseEntity<Map<String, Long>> countVehicles() {
        Map<String, Long> response = new HashMap<>();
        response.put("total", vehicleService.countVehicles());
        return ResponseEntity.ok(response);
    }
}



