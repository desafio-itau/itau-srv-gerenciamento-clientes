package com.itau.srv.gerenciamento.clientes.dto.carteira;

import java.math.BigDecimal;

public record ResumoResponseDTO(
        BigDecimal valorTotalInvestido,
        BigDecimal valorAtualCarteira,
        BigDecimal plTotal,
        BigDecimal rentabilidadePercentual
) {
}
