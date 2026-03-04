package com.itau.srv.gerenciamento.clientes.dto.valormensal;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "Dados de confirmação da alteração do valor mensal")
public record AlterarValorMensalResponseDTO(
        @Schema(description = "ID do cliente", example = "1")
        Long clienteId,

        @Schema(description = "Valor mensal anterior", example = "150.00")
        BigDecimal valorMensalAnterior,

        @Schema(description = "Novo valor mensal", example = "200.00")
        BigDecimal valorMensalNovo,

        @Schema(description = "Data e hora da alteração", example = "2026-03-01T14:30:00")
        LocalDateTime dataAlteracao,

        @Schema(description = "Mensagem de confirmação", example = "Valor mensal alterado com sucesso")
        String mensagem
) {
}
