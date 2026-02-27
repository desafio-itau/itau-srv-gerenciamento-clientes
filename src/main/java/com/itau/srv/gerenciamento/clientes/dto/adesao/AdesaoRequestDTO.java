package com.itau.srv.gerenciamento.clientes.dto.adesao;

import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.br.CPF;

import java.math.BigDecimal;

public record AdesaoRequestDTO(
        @Size(min = 1, max = 200, message = "Nome deve ter entre 1 e 200 caracteres")
        @NotBlank(message = "Nome não pode ser em branco")
        String nome,
        @CPF
        @NotBlank(message = "CPF não pode ser em branco")
        String cpf,
        @Email(message = "Email deve ser válido")
        @NotBlank(message = "Email não pode ser em branco")
        String email,
        @DecimalMin(value = "100.0", message = "Valor mensal deve ser no mínimo R$100,00")
        @NotNull(message = "Valor mensal não pode ser em branco")
        BigDecimal valorMensal
) {
}
