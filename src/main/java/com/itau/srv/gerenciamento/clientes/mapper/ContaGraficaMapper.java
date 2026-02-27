package com.itau.srv.gerenciamento.clientes.mapper;

import com.itau.srv.gerenciamento.clientes.dto.contagrafica.ContaGraficaResponseDTO;
import com.itau.srv.gerenciamento.clientes.model.ContaGrafica;
import org.springframework.stereotype.Component;

@Component
public class ContaGraficaMapper {

    public ContaGraficaResponseDTO mapearParaContaGraficaResponseDTO(ContaGrafica contaGrafica) {
        return new ContaGraficaResponseDTO(
                contaGrafica.getId(),
                contaGrafica.getNumeroConta(),
                contaGrafica.getTipo(),
                contaGrafica.getDataCriacao()
        );
    }
}
