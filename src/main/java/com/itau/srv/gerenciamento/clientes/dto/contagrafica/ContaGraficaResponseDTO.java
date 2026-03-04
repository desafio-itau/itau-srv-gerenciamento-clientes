package com.itau.srv.gerenciamento.clientes.dto.contagrafica;

import com.itau.srv.gerenciamento.clientes.model.enums.TipoConta;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Informações da conta gráfica do cliente")
public record ContaGraficaResponseDTO(
        @Schema(description = "ID da conta gráfica", example = "1")
        Long id,

        @Schema(description = "Número da conta gráfica", example = "ITAUFL00001")
        String numeroConta,

        @Schema(description = "Tipo da conta", example = "FILHOTE")
        TipoConta tipoConta,

        @Schema(description = "Data e hora de criação da conta", example = "2026-03-01T10:30:00")
        LocalDateTime dataCriacao
) {
}
