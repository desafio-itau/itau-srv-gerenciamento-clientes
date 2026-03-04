package com.itau.srv.gerenciamento.clientes.dto.rentabilidade;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(description = "Informações de um aporte realizado")
public record HistoricoAportesResponseDTO(
        @Schema(description = "Data do aporte", example = "2026-01-05")
        LocalDate data,

        @Schema(description = "Valor do aporte", example = "1000.00")
        BigDecimal valor,

        @Schema(description = "Indicação da parcela", example = "1/12")
        String parcela
) {
}
