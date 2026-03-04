package com.itau.srv.gerenciamento.clientes.dto.valormensal;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record AlterarValorMensalResponseDTO(
        Long clienteId,
        BigDecimal valorMensalAnterior,
        BigDecimal valorMensalNovo,
        LocalDateTime dataAlteracao,
        String mensagem
) {
}
