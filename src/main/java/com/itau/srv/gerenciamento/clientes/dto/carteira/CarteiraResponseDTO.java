package com.itau.srv.gerenciamento.clientes.dto.carteira;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "Dados completos da carteira de investimentos do cliente")
public record CarteiraResponseDTO(
        @Schema(description = "ID do cliente", example = "1")
        Long clienteId,

        @Schema(description = "Nome do cliente", example = "João Silva")
        String nome,

        @Schema(description = "Número da conta gráfica", example = "ITAUFL00001")
        String contaGrafica,

        @Schema(description = "Data e hora da consulta", example = "2026-03-01T10:30:00")
        LocalDateTime dataConsulta,

        @Schema(description = "Resumo financeiro da carteira")
        ResumoResponseDTO resumo,

        @Schema(description = "Lista de ativos na carteira")
        List<AtivoResponseDTO> ativos
) {
}
