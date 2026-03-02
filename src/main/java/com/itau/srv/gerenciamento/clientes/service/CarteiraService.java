package com.itau.srv.gerenciamento.clientes.service;

import com.itau.common.library.exception.RecursoNaoEncontradoException;
import com.itau.srv.gerenciamento.clientes.dto.carteira.AtivoResponseDTO;
import com.itau.srv.gerenciamento.clientes.dto.carteira.CarteiraResponseDTO;
import com.itau.srv.gerenciamento.clientes.dto.carteira.ResumoResponseDTO;
import com.itau.srv.gerenciamento.clientes.dto.custodia.CustodiaResponseDTO;
import com.itau.srv.gerenciamento.clientes.dto.valor.ValoresResponseDTO;
import com.itau.srv.gerenciamento.clientes.feign.CustodiasFeignClient;
import com.itau.srv.gerenciamento.clientes.feign.ValoresFeignClient;
import com.itau.srv.gerenciamento.clientes.mapper.CarteiraMaper;
import com.itau.srv.gerenciamento.clientes.model.Cliente;
import com.itau.srv.gerenciamento.clientes.model.ContaGrafica;
import com.itau.srv.gerenciamento.clientes.repository.ClienteRepository;
import com.itau.srv.gerenciamento.clientes.repository.ContaGraficaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CarteiraService {

    private final ContaGraficaRepository contaGraficaRepository;
    private final ClienteRepository clienteRepository;
    private final CustodiasFeignClient custodiasFeignClient;
    private final CarteiraMaper carteiraMaper;
    private final ValoresFeignClient valoresFeignClient;

    @Transactional(readOnly = true)
    public CarteiraResponseDTO consultarCarteiraCliente(Long clienteId) {
        log.info("Consultando carteira do cliente: {}", clienteId);

        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> {
                    log.error("Cliente não encontrado: {}", clienteId);
                    return new RecursoNaoEncontradoException("CLIENTE_NAO_ENCONTRADO");
                });

        log.info("Buscando conta grafica do cliente: {}", cliente.getNome());
        ContaGrafica contaGrafica = contaGraficaRepository.findByCliente(cliente);
        log.info("Conta grafica encontrada: {}", contaGrafica.getNumeroConta());

        log.info("Buscando custodias do cliente: {}", cliente.getNome());
        List<CustodiaResponseDTO> custodiasCliente = custodiasFeignClient.obterCustodiasPorClienteId(cliente.getId());
        log.info("Total de custodias: {}", custodiasCliente.size());

        BigDecimal valorAtualCarteira = custodiasCliente.isEmpty() ? BigDecimal.ZERO
                : custodiasCliente.stream()
                .map(c -> c.valorAtual().multiply(BigDecimal.valueOf(c.quantidade())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        log.info("Valor atual da carteira: {}", valorAtualCarteira);

        ValoresResponseDTO valores = valoresFeignClient.obterValoresPorCliente(cliente.getId());

        BigDecimal valorTotalInvestido = valores.valorInvestido().subtract(valores.valorVendido());

        log.info("Valor total investido: {}", valorTotalInvestido);

        BigDecimal plTotal = custodiasCliente.isEmpty() ? BigDecimal.ZERO
                : custodiasCliente.stream()
                .map(c -> {
                    BigDecimal diferenca = c.valorAtual().subtract(c.precoMedio());
                    return diferenca.multiply(BigDecimal.valueOf(c.quantidade()));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        log.info("PL total: {}", plTotal);

        List<AtivoResponseDTO> ativos = new ArrayList<>();

        for (CustodiaResponseDTO custodia : custodiasCliente) {
            BigDecimal valorTotalAtivo = custodia.valorAtual().multiply(BigDecimal.valueOf(custodia.quantidade()));

            BigDecimal pl = custodia.valorAtual().subtract(custodia.precoMedio()).multiply(BigDecimal.valueOf(custodia.quantidade()));
            BigDecimal plPercentual = plTotal.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO : pl.divide(plTotal, 2, RoundingMode.HALF_UP);
            BigDecimal composicaoCarteira = valorAtualCarteira.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO : valorTotalAtivo.divide(valorAtualCarteira, 2, RoundingMode.HALF_UP);

            ativos.add(new AtivoResponseDTO(
                    custodia.ticker(),
                    custodia.quantidade(),
                    custodia.precoMedio(),
                    custodia.valorAtual(),
                    pl,
                    plPercentual,
                    composicaoCarteira
            ));
        }

        BigDecimal numerador = valorAtualCarteira.subtract(valorTotalInvestido);

        BigDecimal rentabilidadePercentual = valorTotalInvestido.compareTo(BigDecimal.ZERO) == 0
                ? BigDecimal.ZERO
                : numerador.divide(valorTotalInvestido, 4, RoundingMode.DOWN).multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.DOWN);
        log.info("Rentabilidade percentual: {}", rentabilidadePercentual);

        ResumoResponseDTO resumo = new ResumoResponseDTO(
                valorTotalInvestido,
                valorAtualCarteira,
                plTotal,
                rentabilidadePercentual
        );

        return carteiraMaper.mapearParaCarteiraResponseDTO(cliente, contaGrafica, resumo, ativos);
    }
}
