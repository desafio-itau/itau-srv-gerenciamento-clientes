package com.itau.srv.gerenciamento.clientes.dto.carteira;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "Resumo financeiro da carteira de investimentos")
public record ResumoResponseDTO(
        @Schema(description = "Valor total investido pelo cliente", example = "5000.00")
        BigDecimal valorTotalInvestido,

        @Schema(description = "Valor atual da carteira", example = "5500.00")
        BigDecimal valorAtualCarteira,

        @Schema(description = "Lucro/Prejuízo total (P/L)", example = "500.00")
        BigDecimal plTotal,

        @Schema(description = "Rentabilidade percentual", example = "10.00")
        BigDecimal rentabilidadePercentual
) {
}
