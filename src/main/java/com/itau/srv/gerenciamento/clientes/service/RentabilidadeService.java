package com.itau.srv.gerenciamento.clientes.service;

import com.itau.common.library.exception.RecursoNaoEncontradoException;
import com.itau.srv.gerenciamento.clientes.dto.carteira.ResumoResponseDTO;
import com.itau.srv.gerenciamento.clientes.dto.rentabilidade.EvolucaoCarteiraResponseDTO;
import com.itau.srv.gerenciamento.clientes.dto.rentabilidade.HistoricoAportesResponseDTO;
import com.itau.srv.gerenciamento.clientes.dto.rentabilidade.RentabilidadeResponseDTO;
import com.itau.srv.gerenciamento.clientes.feign.ValoresFeignClient;
import com.itau.srv.gerenciamento.clientes.mapper.SnapshotMapper;
import com.itau.srv.gerenciamento.clientes.model.Cliente;
import com.itau.srv.gerenciamento.clientes.model.SnapshotCarteira;
import com.itau.srv.gerenciamento.clientes.repository.ClienteRepository;
import com.itau.srv.gerenciamento.clientes.repository.SnapshotCarteiraRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RentabilidadeService {

    private final SnapshotCarteiraRepository snapshotCarteiraRepository;
    private final ClienteRepository clienteRepository;
    private final SnapshotMapper snapshotMapper;
    private final ValoresFeignClient valoresFeignClient;

    @Transactional(readOnly = true)
    public RentabilidadeResponseDTO consultarRentabilidade(Long clienteId) {

        Cliente cliente = clienteRepository.findByIdAndAtivo(clienteId)
                .orElseThrow(() -> {
                    log.error("Cliente não encontrado com ID: {}", clienteId);
                    return new RecursoNaoEncontradoException("CLIENTE_NAO_ENCONTRADO");
                });

        List<SnapshotCarteira> snapshots = snapshotCarteiraRepository.findAllByClienteId(cliente.getId());

        BigDecimal valorTotalInvestido = snapshots.stream()
                .map(SnapshotCarteira::getValorInvestido)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal valorAtual = snapshots.get(snapshots.size() - 1).getValorCarteira();

        BigDecimal plTotal = valorTotalInvestido.subtract(valorAtual);

        List<EvolucaoCarteiraResponseDTO> evolucoes = snapshots.stream()
                .map(snapshotMapper::mapearParaEvolucaoCarteiraResponseDTO)
                .toList();

        BigDecimal numerador = valorAtual.subtract(valorTotalInvestido);

        BigDecimal rentabilidadePercentual = valorTotalInvestido.compareTo(BigDecimal.ZERO) == 0
                ? BigDecimal.ZERO
                : numerador.divide(valorTotalInvestido, 4, RoundingMode.DOWN).multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.DOWN);

        log.info("Iniciando consulta de histórico de aportes do cliente {}", clienteId);

        List<HistoricoAportesResponseDTO> historicoAportes = valoresFeignClient.consultarHistoricoAportes(clienteId);

        return new RentabilidadeResponseDTO(
                cliente.getId(),
                cliente.getNome(),
                LocalDateTime.now(),
                new ResumoResponseDTO(
                        valorTotalInvestido,
                        valorAtual,
                        plTotal,
                        rentabilidadePercentual
                ),
                historicoAportes,
                evolucoes
        );
    }
}
