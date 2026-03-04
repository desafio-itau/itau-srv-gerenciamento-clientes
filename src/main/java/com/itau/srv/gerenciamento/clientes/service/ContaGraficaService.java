package com.itau.srv.gerenciamento.clientes.service;

import com.itau.srv.gerenciamento.clientes.dto.contagrafica.ContaGraficaResponseDTO;
import com.itau.srv.gerenciamento.clientes.mapper.ContaGraficaMapper;
import com.itau.srv.gerenciamento.clientes.model.Cliente;
import com.itau.srv.gerenciamento.clientes.model.ContaGrafica;
import com.itau.srv.gerenciamento.clientes.model.enums.TipoConta;
import com.itau.srv.gerenciamento.clientes.repository.ContaGraficaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ContaGraficaService {

    private final ContaGraficaRepository contaGraficaRepository;
    private final ContaGraficaMapper contaGraficaMapper;

    private static final String PREFIXO_CONTA = "ITAUFL";
    private static final int TAMANHO_NUMERO = 5;

    @Transactional
    public ContaGrafica criarContaGrafica(Cliente cliente) {
        log.info("Criando conta grafica para cliente: {}", cliente.getCpf());

        ContaGrafica contaGrafica = new ContaGrafica();

        contaGrafica.setCliente(cliente);
        contaGrafica.setTipo(TipoConta.FILHOTE);

        String numeroConta = gerarNumeroConta(cliente.getId());
        contaGrafica.setNumeroConta(numeroConta);

        log.info("Conta grafica criada com sucesso: {}", contaGrafica.getNumeroConta());

        return contaGraficaRepository.save(contaGrafica);
    }

    @Transactional(readOnly = true)
    public ContaGraficaResponseDTO buscarConta(Long id) {
        ContaGrafica contaGrafica = contaGraficaRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Conta grafica não encontrada com ID: {}", id);
                    return new RuntimeException("Conta grafica não encontrada");
                });

        return contaGraficaMapper.mapearParaContaGraficaResponseDTO(contaGrafica);
    }

    private String gerarNumeroConta(Long id) {
        String numeroFormatado = String.format("%0" + TAMANHO_NUMERO + "d", id);
        return PREFIXO_CONTA + numeroFormatado;
    }
}
