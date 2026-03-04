package com.itau.srv.gerenciamento.clientes.dto.adesao;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.br.CPF;

import java.math.BigDecimal;

@Schema(description = "Dados necessários para adesão de um cliente ao produto de investimento")
public record AdesaoRequestDTO(
        @Schema(description = "Nome completo do cliente", example = "João Silva", minLength = 1, maxLength = 200)
        @Size(min = 1, max = 200, message = "Nome deve ter entre 1 e 200 caracteres")
        @NotBlank(message = "Nome não pode ser em branco")
        String nome,

        @Schema(description = "CPF do cliente (somente números)", example = "12345678901")
        @CPF
        @NotBlank(message = "CPF não pode ser em branco")
        String cpf,

        @Schema(description = "Email do cliente", example = "joao@email.com")
        @Email(message = "Email deve ser válido")
        @NotBlank(message = "Email não pode ser em branco")
        String email,

        @Schema(description = "Valor mensal que o cliente deseja investir", example = "150.00", minimum = "100.00")
        @DecimalMin(value = "100.0", message = "Valor mensal deve ser no mínimo R$100,00")
        @NotNull(message = "Valor mensal não pode ser em branco")
        BigDecimal valorMensal
) {
}
