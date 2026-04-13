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
@Tag(name = "Rentals", description = "Operacoes de aluguel de veiculos com validacao de conflitos e calculo de valor total")
public class RentalController {

    private final RentalService rentalService;

    @PostMapping
    @Operation(summary = "Registrar aluguel", description = "Cria um novo aluguel validando conflito de periodo e disponibilidade do veiculo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Aluguel registrado com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = RentalResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Dados invalidos ou periodo em conflito"),
            @ApiResponse(responseCode = "404", description = "Cliente ou veiculo nao encontrado")
    })
    public ResponseEntity<RentalResponseDTO> createRental(@Valid @RequestBody RentalRequestDTO requestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(rentalService.createRental(requestDTO));
    }

    @GetMapping
    @Operation(summary = "Listar alugueis", description = "Retorna uma lista paginada de alugueis")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de alugueis recuperada",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = PageResponse.class)))
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
    @Operation(summary = "Buscar aluguel por ID", description = "Retorna os detalhes de um aluguel especifico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Aluguel encontrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = RentalResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Aluguel nao encontrado")
    })
    public ResponseEntity<RentalResponseDTO> getRentalById(
            @Parameter(description = "ID do aluguel")
            @PathVariable Long id) {
        return ResponseEntity.ok(rentalService.getRentalById(id));
    }

    @GetMapping("/customer/{customerId}")
    @Operation(summary = "Historico por cliente", description = "Retorna o historico paginado de alugueis de um cliente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Historico recuperado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = PageResponse.class))),
            @ApiResponse(responseCode = "404", description = "Cliente nao encontrado")
    })
    public ResponseEntity<PageResponse<RentalResponseDTO>> getRentalsByCustomer(
            @Parameter(description = "ID do cliente")
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
    @Operation(summary = "Cancelar aluguel", description = "Cancela um aluguel ativo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Aluguel cancelado com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = RentalResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Aluguel ja cancelado ou finalizado"),
            @ApiResponse(responseCode = "404", description = "Aluguel nao encontrado")
    })
    public ResponseEntity<RentalResponseDTO> cancelRental(
            @Parameter(description = "ID do aluguel")
            @PathVariable Long id) {
        return ResponseEntity.ok(rentalService.cancelRental(id));
    }

    @GetMapping("/vehicles/available")
    @Operation(summary = "Veiculos disponiveis por periodo", description = "Lista os veiculos disponiveis para um periodo especifico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Veiculos disponiveis recuperados",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = PageResponse.class))),
            @ApiResponse(responseCode = "400", description = "Periodo invalido")
    })
    public ResponseEntity<PageResponse<VehicleResponseDTO>> getAvailableVehicles(
            @Parameter(description = "Data inicial no formato yyyy-MM-dd", example = "2026-04-15")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "Data final no formato yyyy-MM-dd", example = "2026-04-18")
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
    @Operation(summary = "Contar alugueis", description = "Retorna o total de alugueis cadastrados")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Total de alugueis recuperado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class)))
    })
    public ResponseEntity<Map<String, Long>> countRentals() {
        Map<String, Long> response = new HashMap<>();
        response.put("total", rentalService.countRentals());
        return ResponseEntity.ok(response);
    }
}

