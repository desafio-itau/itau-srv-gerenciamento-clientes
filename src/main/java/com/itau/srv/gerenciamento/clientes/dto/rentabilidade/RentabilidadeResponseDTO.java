package com.itau.srv.gerenciamento.clientes.dto.rentabilidade;

import com.itau.srv.gerenciamento.clientes.dto.carteira.ResumoResponseDTO;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "Dados detalhados da rentabilidade do cliente")
public record RentabilidadeResponseDTO(
        @Schema(description = "ID do cliente", example = "1")
        Long clienteId,

        @Schema(description = "Nome do cliente", example = "João Silva")
        String nome,

        @Schema(description = "Data e hora da consulta", example = "2026-03-01T10:30:00")
        LocalDateTime dataConsulta,

        @Schema(description = "Resumo da rentabilidade")
        ResumoResponseDTO rentabilidade,

        @Schema(description = "Histórico de aportes realizados")
        List<HistoricoAportesResponseDTO> historicoAportes,

        @Schema(description = "Evolução da carteira ao longo do tempo")
        List<EvolucaoCarteiraResponseDTO> evolucaoCarteira
) {
}
