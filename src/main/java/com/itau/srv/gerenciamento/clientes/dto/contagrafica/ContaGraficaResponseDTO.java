package com.itau.srv.gerenciamento.clientes.dto.contagrafica;

import com.itau.srv.gerenciamento.clientes.model.enums.TipoConta;

import java.time.LocalDateTime;

public record ContaGraficaResponseDTO(
        Long id,
        String numeroConta,
        TipoConta tipoConta,
        LocalDateTime dataCriacao
) {
}
