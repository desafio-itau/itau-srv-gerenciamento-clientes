package com.itau.srv.gerenciamento.clientes.dto.valor;

import java.time.LocalDate;

public record ValoresPorDataResponseDTO(
        LocalDate dataEvento,
        ValoresResponseDTO valores
) {
}