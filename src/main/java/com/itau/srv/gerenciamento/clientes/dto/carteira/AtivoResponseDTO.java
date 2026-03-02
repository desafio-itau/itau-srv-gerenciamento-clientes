package com.itau.srv.gerenciamento.clientes.dto.carteira;

import java.math.BigDecimal;

public record AtivoResponseDTO(
        String ticker,
        Integer quantidade,
        BigDecimal precoMedio,
        BigDecimal cotacaoAtual,
        BigDecimal pl,
        BigDecimal plPercentual,
        BigDecimal composicaoCarteira
) {
}
