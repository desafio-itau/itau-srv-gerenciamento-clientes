package com.itau.srv.gerenciamento.clientes.dto.valor;

import java.math.BigDecimal;

public record ValoresResponseDTO(
        BigDecimal valorInvestido,
        BigDecimal valorVendido
) {
}
