package com.itau.srv.gerenciamento.clientes.dto.rentabilidade;

import java.math.BigDecimal;
import java.time.LocalDate;

public record HistoricoAportesResponseDTO(
        LocalDate data,
        BigDecimal valor,
        String parcela
) {
}
