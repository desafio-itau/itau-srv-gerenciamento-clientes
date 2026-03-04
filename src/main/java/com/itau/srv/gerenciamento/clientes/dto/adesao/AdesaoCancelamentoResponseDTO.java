package com.itau.srv.gerenciamento.clientes.dto.adesao;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Dados de confirmação do cancelamento da adesão")
public record AdesaoCancelamentoResponseDTO(
        @Schema(description = "ID do cliente", example = "1")
        Long clienteId,

        @Schema(description = "Nome do cliente", example = "João Silva")
        String nome,

        @Schema(description = "Status do cliente após cancelamento", example = "false")
        Boolean ativo,

        @Schema(description = "Data e hora do cancelamento", example = "2026-03-01T15:30:00")
        LocalDateTime dataSaida,

        @Schema(description = "Mensagem de confirmação", example = "Adesão cancelada com sucesso")
        String mensagem
) {
}
