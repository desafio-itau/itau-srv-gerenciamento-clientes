package com.itau.srv.gerenciamento.clientes.dto.custodia;

import java.math.BigDecimal;

public record CustodiaResponseDTO(
        String ticker,
        Integer quantidade,
        BigDecimal precoMedio,
        BigDecimal valorAtual,
        String origem
) {
}
