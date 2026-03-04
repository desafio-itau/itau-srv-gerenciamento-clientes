package com.itau.srv.gerenciamento.clientes.dto.rentabilidade;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(description = "Snapshot da evolução da carteira em uma data específica")
public record EvolucaoCarteiraResponseDTO(
        @Schema(description = "Data do snapshot", example = "2026-01-05")
        LocalDate data,

        @Schema(description = "Valor da carteira na data", example = "5500.00")
        BigDecimal valorCarteira,

        @Schema(description = "Valor investido até a data", example = "5000.00")
        BigDecimal valorInvestido,

        @Schema(description = "Rentabilidade percentual até a data", example = "10.00")
        BigDecimal rentabilidade
) {
}
