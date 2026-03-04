package com.itau.srv.gerenciamento.clientes.dto.valormensal;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

@Schema(description = "Dados para alteração do valor mensal de investimento")
public record AlterarValorMensalRequestDTO(
        @Schema(description = "Novo valor mensal de investimento", example = "200.00", minimum = "100.00")
        @NotNull(message = "Novo valor mensal não pode ser nulo")
        BigDecimal novoValorMensal
) {
}
