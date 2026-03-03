package com.itau.srv.gerenciamento.clientes.dto.rentabilidade;

import java.math.BigDecimal;
import java.time.LocalDate;

public record EvolucaoCarteiraResponseDTO(
        LocalDate data,
        BigDecimal valorCarteira,
        BigDecimal valorInvestido,
        BigDecimal rentabilidade
) {
}
