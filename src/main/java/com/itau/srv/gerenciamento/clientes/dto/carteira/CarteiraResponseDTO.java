package com.itau.srv.gerenciamento.clientes.dto.carteira;

import java.time.LocalDateTime;
import java.util.List;

public record CarteiraResponseDTO(
        Long clienteId,
        String nome,
        String contaGrafica,
        LocalDateTime dataConsulta,
        ResumoResponseDTO resumo,
        List<AtivoResponseDTO> ativos
) {
}
