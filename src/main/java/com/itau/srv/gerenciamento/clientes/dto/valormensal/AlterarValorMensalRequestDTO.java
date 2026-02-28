package com.itau.srv.gerenciamento.clientes.dto.valormensal;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record AlterarValorMensalRequestDTO(
        @NotNull(message = "Novo valor mensal não pode ser nulo")
        BigDecimal novoValorMensal
) {
}

