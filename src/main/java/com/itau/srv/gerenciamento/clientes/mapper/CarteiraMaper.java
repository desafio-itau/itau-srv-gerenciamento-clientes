package com.itau.srv.gerenciamento.clientes.mapper;

import com.itau.srv.gerenciamento.clientes.dto.carteira.AtivoResponseDTO;
import com.itau.srv.gerenciamento.clientes.dto.carteira.CarteiraResponseDTO;
import com.itau.srv.gerenciamento.clientes.dto.carteira.ResumoResponseDTO;
import com.itau.srv.gerenciamento.clientes.model.Cliente;
import com.itau.srv.gerenciamento.clientes.model.ContaGrafica;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class CarteiraMaper {

    public CarteiraResponseDTO mapearParaCarteiraResponseDTO(Cliente cliente, ContaGrafica contaGrafica, ResumoResponseDTO resumo, List<AtivoResponseDTO> ativos) {
        return new CarteiraResponseDTO(
                cliente.getId(),
                cliente.getNome(),
                contaGrafica.getNumeroConta(),
                LocalDateTime.now(),
                resumo,
                ativos
        );
    }
}
