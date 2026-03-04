package com.itau.srv.gerenciamento.clientes.dto.carteira;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "Informações de um ativo na carteira")
public record AtivoResponseDTO(
        @Schema(description = "Código do ativo (ticker)", example = "PETR4")
        String ticker,

        @Schema(description = "Quantidade de ações", example = "100")
        Integer quantidade,

        @Schema(description = "Preço médio de compra", example = "35.50")
        BigDecimal precoMedio,

        @Schema(description = "Cotação atual do ativo", example = "38.00")
        BigDecimal cotacaoAtual,

        @Schema(description = "Lucro/Prejuízo do ativo", example = "250.00")
        BigDecimal pl,

        @Schema(description = "Rentabilidade percentual do ativo", example = "7.04")
        BigDecimal plPercentual,

        @Schema(description = "Percentual de composição na carteira", example = "0.35")
        BigDecimal composicaoCarteira
) {
}
