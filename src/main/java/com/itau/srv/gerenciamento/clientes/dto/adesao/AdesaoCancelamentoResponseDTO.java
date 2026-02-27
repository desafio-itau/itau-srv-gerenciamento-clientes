package com.itau.srv.gerenciamento.clientes.dto.adesao;

import java.time.LocalDateTime;

public record AdesaoCancelamentoResponseDTO(
        Long clienteId,
        String nome,
        Boolean ativo,
        LocalDateTime dataSaida,
        String mensagem
) {
}
