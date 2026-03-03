package com.itau.srv.gerenciamento.clientes.dto.rentabilidade;

import com.itau.srv.gerenciamento.clientes.dto.carteira.ResumoResponseDTO;

import java.time.LocalDateTime;
import java.util.List;

public record RentabilidadeResponseDTO(
        Long clienteId,
        String nome,
        LocalDateTime dataConsulta,
        ResumoResponseDTO rentabilidade,
        List<HistoricoAportesResponseDTO> historicoAportes,
        List<EvolucaoCarteiraResponseDTO> evolucaoCarteira
) {
}
