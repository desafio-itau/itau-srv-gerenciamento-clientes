package com.itau.srv.gerenciamento.clientes.controller;

import com.itau.srv.gerenciamento.clientes.dto.contagrafica.ContaGraficaResponseDTO;
import com.itau.srv.gerenciamento.clientes.service.ContaGraficaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/contas-graficas")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Contas Gráficas", description = "API para consulta de informações de contas gráficas")
public class ContaGraficaController {

    private final ContaGraficaService contaGraficaService;

    @Operation(
            summary = "Buscar conta gráfica",
            description = "Retorna os detalhes de uma conta gráfica pelo seu ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Conta gráfica encontrada com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ContaGraficaResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Conta gráfica não encontrada", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<ContaGraficaResponseDTO> buscarConta(
            @Parameter(description = "ID da conta gráfica", required = true)
            @PathVariable Long id) {
        log.info("Buscando conta grafica: {}", id);

        return ResponseEntity.ok(contaGraficaService.buscarConta(id));
    }
}
