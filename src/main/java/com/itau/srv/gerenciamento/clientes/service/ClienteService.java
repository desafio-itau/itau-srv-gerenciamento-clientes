package com.itau.srv.gerenciamento.clientes.service;

import com.itau.common.library.exception.RecursoNaoEncontradoException;
import com.itau.srv.gerenciamento.clientes.dto.adesao.AdesaoCancelamentoResponseDTO;
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

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClienteService {

    private final ClienteValidator clienteValidator;
    private final ClienteRepository clienteRepository;
    private final ClienteMapper clienteMapper;
    private final ContaGraficaService contaGraficaService;

    private static final String ADESAO_ENCERRADA = "Adesão encerrada. Sua posição em custodia foi mantida.";

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

    @Transactional
    public AdesaoCancelamentoResponseDTO cancelarAdesao(Long clienteId) {
        Cliente cliente = clienteRepository.findByIdAndAtivo(clienteId)
                .orElseThrow(() -> {
                    log.error("Cliente não encontrado para ID: {}", clienteId);
                    return new RecursoNaoEncontradoException("CLIENTE_NAO_ENCONTRADO");
                });

        cliente.setAtivo(false);

        clienteRepository.save(cliente);

        log.info("Cliente {} cancelado com sucesso", cliente.getCpf());

        return new AdesaoCancelamentoResponseDTO(
                cliente.getId(),
                cliente.getNome(),
                cliente.getAtivo(),
                LocalDateTime.now(),
                ADESAO_ENCERRADA
        );
    }
}
