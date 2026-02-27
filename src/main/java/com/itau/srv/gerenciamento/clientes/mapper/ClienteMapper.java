package com.itau.srv.gerenciamento.clientes.mapper;

import com.itau.srv.gerenciamento.clientes.dto.adesao.AdesaoRequestDTO;
import com.itau.srv.gerenciamento.clientes.dto.adesao.AdesaoResponseDTO;
import com.itau.srv.gerenciamento.clientes.model.Cliente;
import com.itau.srv.gerenciamento.clientes.model.ContaGrafica;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ClienteMapper {

    private final ContaGraficaMapper contaGraficaMapper;

    public Cliente mapearParaCliente(AdesaoRequestDTO dto) {
        Cliente cliente = new Cliente();

        cliente.setNome(dto.nome());
        cliente.setCpf(dto.cpf());
        cliente.setEmail(dto.email());
        cliente.setValorMensal(dto.valorMensal());

        return cliente;
    }

    public AdesaoResponseDTO mapearParaAdesaoResponseDTO(Cliente cliente, ContaGrafica contaGrafica) {
        return new AdesaoResponseDTO(
                cliente.getId(),
                cliente.getNome(),
                cliente.getCpf(),
                cliente.getEmail(),
                cliente.getValorMensal(),
                cliente.getAtivo(),
                cliente.getDataAdesao(),
                contaGraficaMapper.mapearParaContaGraficaResponseDTO(contaGrafica)
        );
    }
}
