package com.itau.srv.gerenciamento.clientes.dto.adesao;

import com.itau.srv.gerenciamento.clientes.dto.contagrafica.ContaGraficaResponseDTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record AdesaoResponseDTO(
        Long clienteId,
        String nome,
        String cpf,
        String email,
        BigDecimal valorMensal,
        Boolean ativo,
        LocalDateTime dataAdesao,
        ContaGraficaResponseDTO contaGrafica
) {
}
