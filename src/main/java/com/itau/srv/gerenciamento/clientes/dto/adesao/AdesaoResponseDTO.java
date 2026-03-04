package com.itau.srv.gerenciamento.clientes.dto.adesao;

import com.itau.srv.gerenciamento.clientes.dto.contagrafica.ContaGraficaResponseDTO;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "Dados do cliente após a adesão ao produto")
public record AdesaoResponseDTO(
        @Schema(description = "ID único do cliente", example = "1")
        Long clienteId,

        @Schema(description = "Nome completo do cliente", example = "João Silva")
        String nome,

        @Schema(description = "CPF do cliente", example = "12345678901")
        String cpf,

        @Schema(description = "Email do cliente", example = "joao@email.com")
        String email,

        @Schema(description = "Valor mensal de investimento", example = "150.00")
        BigDecimal valorMensal,

        @Schema(description = "Indica se o cliente está ativo", example = "true")
        Boolean ativo,

        @Schema(description = "Data e hora da adesão", example = "2026-03-01T10:30:00")
        LocalDateTime dataAdesao,

        @Schema(description = "Dados da conta gráfica do cliente")
        ContaGraficaResponseDTO contaGrafica
) {
}
