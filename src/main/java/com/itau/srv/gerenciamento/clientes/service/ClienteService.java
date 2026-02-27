package com.itau.srv.gerenciamento.clientes.service;

import com.itau.srv.gerenciamento.clientes.dto.adesao.AdesaoRequestDTO;
import com.itau.srv.gerenciamento.clientes.dto.adesao.AdesaoResponseDTO;
import com.itau.srv.gerenciamento.clientes.mapper.ClienteMapper;
import com.itau.srv.gerenciamento.clientes.model.Cliente;
import com.itau.srv.gerenciamento.clientes.model.ContaGrafica;
import com.itau.srv.gerenciamento.clientes.repository.ClienteRepository;
import com.itau.srv.gerenciamento.clientes.validator.ClienteValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClienteService {

    private final ClienteValidator clienteValidator;
    private final ClienteRepository clienteRepository;
    private final ClienteMapper clienteMapper;
    private final ContaGraficaService contaGraficaService;

    @Transactional
    public AdesaoResponseDTO aderirAoProduto(AdesaoRequestDTO dto) {
        log.info("Aderindo cliente ao produto: {}", dto.cpf());

        Cliente cliente = clienteMapper.mapearParaCliente(dto);

        clienteValidator.validarCpf(cliente);

        log.info("Cliente validado: {}", cliente.getCpf());

        Cliente clienteSalvo = clienteRepository.save(cliente);

        ContaGrafica contaGrafica = contaGraficaService.criarContaGrafica(clienteSalvo);

        return clienteMapper.mapearParaAdesaoResponseDTO(clienteSalvo, contaGrafica);
    }
}
