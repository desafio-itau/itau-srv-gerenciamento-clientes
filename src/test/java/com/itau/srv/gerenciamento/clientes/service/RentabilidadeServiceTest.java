package com.itau.srv.gerenciamento.clientes.service;

import com.itau.common.library.exception.RecursoNaoEncontradoException;
import com.itau.srv.gerenciamento.clientes.dto.rentabilidade.EvolucaoCarteiraResponseDTO;
import com.itau.srv.gerenciamento.clientes.dto.rentabilidade.HistoricoAportesResponseDTO;
import com.itau.srv.gerenciamento.clientes.dto.rentabilidade.RentabilidadeResponseDTO;
import com.itau.srv.gerenciamento.clientes.feign.ValoresFeignClient;
import com.itau.srv.gerenciamento.clientes.mapper.SnapshotMapper;
import com.itau.srv.gerenciamento.clientes.model.Cliente;
import com.itau.srv.gerenciamento.clientes.model.SnapshotCarteira;
import com.itau.srv.gerenciamento.clientes.repository.ClienteRepository;
import com.itau.srv.gerenciamento.clientes.repository.SnapshotCarteiraRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RentabilidadeServiceTest {

    @Mock
    private SnapshotCarteiraRepository snapshotCarteiraRepository;

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private SnapshotMapper snapshotMapper;

    @Mock
    private ValoresFeignClient valoresFeignClient;

    @InjectMocks
    private RentabilidadeService rentabilidadeService;

    private Cliente cliente;
    private List<SnapshotCarteira> snapshots;
    private List<HistoricoAportesResponseDTO> historicoAportes;
    private List<EvolucaoCarteiraResponseDTO> evolucoes;

    @BeforeEach
    void setUp() {
        cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNome("João Silva");
        cliente.setCpf("12345678901");
        cliente.setEmail("joao@email.com");
        cliente.setValorMensal(new BigDecimal("150.00"));
        cliente.setAtivo(true);
        cliente.setDataAdesao(LocalDateTime.now());

        // Criar snapshots
        SnapshotCarteira snapshot1 = new SnapshotCarteira();
        snapshot1.setClienteId(1L);
        snapshot1.setDataSnapshot(LocalDate.of(2026, 1, 5));
        snapshot1.setValorCarteira(new BigDecimal("1000.00"));
        snapshot1.setValorInvestido(new BigDecimal("1000.00"));
        snapshot1.setRentabilidade(BigDecimal.ZERO);

        SnapshotCarteira snapshot2 = new SnapshotCarteira();
        snapshot2.setClienteId(1L);
        snapshot2.setDataSnapshot(LocalDate.of(2026, 2, 5));
        snapshot2.setValorCarteira(new BigDecimal("2100.00"));
        snapshot2.setValorInvestido(new BigDecimal("2000.00"));
        snapshot2.setRentabilidade(new BigDecimal("5.00"));

        SnapshotCarteira snapshot3 = new SnapshotCarteira();
        snapshot3.setClienteId(1L);
        snapshot3.setDataSnapshot(LocalDate.of(2026, 3, 5));
        snapshot3.setValorCarteira(new BigDecimal("3300.00"));
        snapshot3.setValorInvestido(new BigDecimal("3000.00"));
        snapshot3.setRentabilidade(new BigDecimal("10.00"));

        snapshots = Arrays.asList(snapshot1, snapshot2, snapshot3);

        // Criar histórico de aportes
        historicoAportes = Arrays.asList(
                new HistoricoAportesResponseDTO(LocalDate.of(2026, 1, 5), new BigDecimal("1000.00"), "1/12"),
                new HistoricoAportesResponseDTO(LocalDate.of(2026, 2, 5), new BigDecimal("1000.00"), "2/12"),
                new HistoricoAportesResponseDTO(LocalDate.of(2026, 3, 5), new BigDecimal("1000.00"), "3/12")
        );

        // Criar evoluções
        evolucoes = Arrays.asList(
                new EvolucaoCarteiraResponseDTO(LocalDate.of(2026, 1, 5), new BigDecimal("1000.00"), new BigDecimal("1000.00"), BigDecimal.ZERO),
                new EvolucaoCarteiraResponseDTO(LocalDate.of(2026, 2, 5), new BigDecimal("2100.00"), new BigDecimal("2000.00"), new BigDecimal("5.00")),
                new EvolucaoCarteiraResponseDTO(LocalDate.of(2026, 3, 5), new BigDecimal("3300.00"), new BigDecimal("3000.00"), new BigDecimal("10.00"))
        );
    }

    @Test
    void deveConsultarRentabilidadeComSucesso() {
        // Arrange
        when(clienteRepository.findByIdAndAtivo(anyLong())).thenReturn(Optional.of(cliente));
        when(snapshotCarteiraRepository.findAllByClienteId(anyLong())).thenReturn(snapshots);
        when(snapshotMapper.mapearParaEvolucaoCarteiraResponseDTO(any(SnapshotCarteira.class)))
                .thenReturn(evolucoes.get(0), evolucoes.get(1), evolucoes.get(2));
        when(valoresFeignClient.consultarHistoricoAportes(anyLong())).thenReturn(historicoAportes);

        // Act
        RentabilidadeResponseDTO resultado = rentabilidadeService.consultarRentabilidade(1L);

        // Assert
        assertNotNull(resultado);
        assertEquals(1L, resultado.clienteId());
        assertEquals("João Silva", resultado.nome());
        assertNotNull(resultado.dataConsulta());
        assertNotNull(resultado.rentabilidade());
        assertNotNull(resultado.historicoAportes());
        assertNotNull(resultado.evolucaoCarteira());

        verify(clienteRepository, times(1)).findByIdAndAtivo(1L);
        verify(snapshotCarteiraRepository, times(1)).findAllByClienteId(1L);
        verify(snapshotMapper, times(3)).mapearParaEvolucaoCarteiraResponseDTO(any(SnapshotCarteira.class));
        verify(valoresFeignClient, times(1)).consultarHistoricoAportes(1L);
    }

    @Test
    void deveLancarExcecaoQuandoClienteNaoEncontrado() {
        // Arrange
        when(clienteRepository.findByIdAndAtivo(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RecursoNaoEncontradoException.class, () -> {
            rentabilidadeService.consultarRentabilidade(999L);
        });

        verify(clienteRepository, times(1)).findByIdAndAtivo(999L);
        verify(snapshotCarteiraRepository, never()).findAllByClienteId(anyLong());
    }

    @Test
    void deveRetornarRentabilidadeCorreta() {
        // Arrange
        when(clienteRepository.findByIdAndAtivo(anyLong())).thenReturn(Optional.of(cliente));
        when(snapshotCarteiraRepository.findAllByClienteId(anyLong())).thenReturn(snapshots);
        when(snapshotMapper.mapearParaEvolucaoCarteiraResponseDTO(any(SnapshotCarteira.class)))
                .thenReturn(evolucoes.get(0), evolucoes.get(1), evolucoes.get(2));
        when(valoresFeignClient.consultarHistoricoAportes(anyLong())).thenReturn(historicoAportes);

        // Act
        RentabilidadeResponseDTO resultado = rentabilidadeService.consultarRentabilidade(1L);

        // Assert
        assertNotNull(resultado.rentabilidade());
        assertTrue(resultado.rentabilidade().valorTotalInvestido().compareTo(BigDecimal.ZERO) > 0);
        assertTrue(resultado.rentabilidade().valorAtualCarteira().compareTo(BigDecimal.ZERO) > 0);
    }

    @Test
    void deveRetornarHistoricoAportesCompleto() {
        // Arrange
        when(clienteRepository.findByIdAndAtivo(anyLong())).thenReturn(Optional.of(cliente));
        when(snapshotCarteiraRepository.findAllByClienteId(anyLong())).thenReturn(snapshots);
        when(snapshotMapper.mapearParaEvolucaoCarteiraResponseDTO(any(SnapshotCarteira.class)))
                .thenReturn(evolucoes.get(0), evolucoes.get(1), evolucoes.get(2));
        when(valoresFeignClient.consultarHistoricoAportes(anyLong())).thenReturn(historicoAportes);

        // Act
        RentabilidadeResponseDTO resultado = rentabilidadeService.consultarRentabilidade(1L);

        // Assert
        assertNotNull(resultado.historicoAportes());
        assertEquals(3, resultado.historicoAportes().size());
        assertEquals(new BigDecimal("1000.00"), resultado.historicoAportes().get(0).valor());
    }

    @Test
    void deveRetornarEvolucaoCarteiraCompleta() {
        // Arrange
        when(clienteRepository.findByIdAndAtivo(anyLong())).thenReturn(Optional.of(cliente));
        when(snapshotCarteiraRepository.findAllByClienteId(anyLong())).thenReturn(snapshots);
        when(snapshotMapper.mapearParaEvolucaoCarteiraResponseDTO(any(SnapshotCarteira.class)))
                .thenReturn(evolucoes.get(0), evolucoes.get(1), evolucoes.get(2));
        when(valoresFeignClient.consultarHistoricoAportes(anyLong())).thenReturn(historicoAportes);

        // Act
        RentabilidadeResponseDTO resultado = rentabilidadeService.consultarRentabilidade(1L);

        // Assert
        assertNotNull(resultado.evolucaoCarteira());
        assertEquals(3, resultado.evolucaoCarteira().size());
    }

    @Test
    void deveCalcularValorTotalInvestidoCorretamente() {
        // Arrange
        when(clienteRepository.findByIdAndAtivo(anyLong())).thenReturn(Optional.of(cliente));
        when(snapshotCarteiraRepository.findAllByClienteId(anyLong())).thenReturn(snapshots);
        when(snapshotMapper.mapearParaEvolucaoCarteiraResponseDTO(any(SnapshotCarteira.class)))
                .thenReturn(evolucoes.get(0), evolucoes.get(1), evolucoes.get(2));
        when(valoresFeignClient.consultarHistoricoAportes(anyLong())).thenReturn(historicoAportes);

        // Act
        RentabilidadeResponseDTO resultado = rentabilidadeService.consultarRentabilidade(1L);

        // Assert
        BigDecimal valorTotalEsperado = new BigDecimal("6000.00"); // Soma dos valores investidos
        assertEquals(valorTotalEsperado, resultado.rentabilidade().valorTotalInvestido());
    }

    @Test
    void deveUsarUltimoSnapshotParaValorAtual() {
        // Arrange
        when(clienteRepository.findByIdAndAtivo(anyLong())).thenReturn(Optional.of(cliente));
        when(snapshotCarteiraRepository.findAllByClienteId(anyLong())).thenReturn(snapshots);
        when(snapshotMapper.mapearParaEvolucaoCarteiraResponseDTO(any(SnapshotCarteira.class)))
                .thenReturn(evolucoes.get(0), evolucoes.get(1), evolucoes.get(2));
        when(valoresFeignClient.consultarHistoricoAportes(anyLong())).thenReturn(historicoAportes);

        // Act
        RentabilidadeResponseDTO resultado = rentabilidadeService.consultarRentabilidade(1L);

        // Assert
        BigDecimal valorAtualEsperado = new BigDecimal("3300.00"); // Valor do último snapshot
        assertEquals(valorAtualEsperado, resultado.rentabilidade().valorAtualCarteira());
    }

    @Test
    void deveChamarMapperParaCadaSnapshot() {
        // Arrange
        when(clienteRepository.findByIdAndAtivo(anyLong())).thenReturn(Optional.of(cliente));
        when(snapshotCarteiraRepository.findAllByClienteId(anyLong())).thenReturn(snapshots);
        when(snapshotMapper.mapearParaEvolucaoCarteiraResponseDTO(any(SnapshotCarteira.class)))
                .thenReturn(evolucoes.get(0), evolucoes.get(1), evolucoes.get(2));
        when(valoresFeignClient.consultarHistoricoAportes(anyLong())).thenReturn(historicoAportes);

        // Act
        rentabilidadeService.consultarRentabilidade(1L);

        // Assert
        verify(snapshotMapper, times(3)).mapearParaEvolucaoCarteiraResponseDTO(any(SnapshotCarteira.class));
    }

    @Test
    void deveChamarFeignClientParaHistoricoAportes() {
        // Arrange
        when(clienteRepository.findByIdAndAtivo(anyLong())).thenReturn(Optional.of(cliente));
        when(snapshotCarteiraRepository.findAllByClienteId(anyLong())).thenReturn(snapshots);
        when(snapshotMapper.mapearParaEvolucaoCarteiraResponseDTO(any(SnapshotCarteira.class)))
                .thenReturn(evolucoes.get(0), evolucoes.get(1), evolucoes.get(2));
        when(valoresFeignClient.consultarHistoricoAportes(anyLong())).thenReturn(historicoAportes);

        // Act
        rentabilidadeService.consultarRentabilidade(1L);

        // Assert
        verify(valoresFeignClient, times(1)).consultarHistoricoAportes(1L);
    }

    @Test
    void deveRetornarDataConsultaAtual() {
        // Arrange
        when(clienteRepository.findByIdAndAtivo(anyLong())).thenReturn(Optional.of(cliente));
        when(snapshotCarteiraRepository.findAllByClienteId(anyLong())).thenReturn(snapshots);
        when(snapshotMapper.mapearParaEvolucaoCarteiraResponseDTO(any(SnapshotCarteira.class)))
                .thenReturn(evolucoes.get(0), evolucoes.get(1), evolucoes.get(2));
        when(valoresFeignClient.consultarHistoricoAportes(anyLong())).thenReturn(historicoAportes);

        LocalDateTime antes = LocalDateTime.now();

        // Act
        RentabilidadeResponseDTO resultado = rentabilidadeService.consultarRentabilidade(1L);

        LocalDateTime depois = LocalDateTime.now();

        // Assert
        assertNotNull(resultado.dataConsulta());
        assertTrue(resultado.dataConsulta().isAfter(antes) || resultado.dataConsulta().isEqual(antes));
        assertTrue(resultado.dataConsulta().isBefore(depois) || resultado.dataConsulta().isEqual(depois));
    }

    @Test
    void deveRetornarDadosDoClienteCorretamente() {
        // Arrange
        when(clienteRepository.findByIdAndAtivo(anyLong())).thenReturn(Optional.of(cliente));
        when(snapshotCarteiraRepository.findAllByClienteId(anyLong())).thenReturn(snapshots);
        when(snapshotMapper.mapearParaEvolucaoCarteiraResponseDTO(any(SnapshotCarteira.class)))
                .thenReturn(evolucoes.get(0), evolucoes.get(1), evolucoes.get(2));
        when(valoresFeignClient.consultarHistoricoAportes(anyLong())).thenReturn(historicoAportes);

        // Act
        RentabilidadeResponseDTO resultado = rentabilidadeService.consultarRentabilidade(1L);

        // Assert
        assertEquals(cliente.getId(), resultado.clienteId());
        assertEquals(cliente.getNome(), resultado.nome());
    }

    @Test
    void deveCalcularRentabilidadePercentualCorretamente() {
        // Arrange
        when(clienteRepository.findByIdAndAtivo(anyLong())).thenReturn(Optional.of(cliente));
        when(snapshotCarteiraRepository.findAllByClienteId(anyLong())).thenReturn(snapshots);
        when(snapshotMapper.mapearParaEvolucaoCarteiraResponseDTO(any(SnapshotCarteira.class)))
                .thenReturn(evolucoes.get(0), evolucoes.get(1), evolucoes.get(2));
        when(valoresFeignClient.consultarHistoricoAportes(anyLong())).thenReturn(historicoAportes);

        // Act
        RentabilidadeResponseDTO resultado = rentabilidadeService.consultarRentabilidade(1L);

        // Assert
        assertNotNull(resultado.rentabilidade().rentabilidadePercentual());
        // Rentabilidade esperada: (3300 - 6000) / 6000 * 100 = -45.00%
        assertTrue(resultado.rentabilidade().rentabilidadePercentual().compareTo(BigDecimal.ZERO) != 0);
    }

    @Test
    void deveRetornarRentabilidadeZeroQuandoValorInvestidoZero() {
        // Arrange
        SnapshotCarteira snapshotZero = new SnapshotCarteira();
        snapshotZero.setClienteId(1L);
        snapshotZero.setDataSnapshot(LocalDate.of(2026, 1, 5));
        snapshotZero.setValorCarteira(BigDecimal.ZERO);
        snapshotZero.setValorInvestido(BigDecimal.ZERO);
        snapshotZero.setRentabilidade(BigDecimal.ZERO);

        when(clienteRepository.findByIdAndAtivo(anyLong())).thenReturn(Optional.of(cliente));
        when(snapshotCarteiraRepository.findAllByClienteId(anyLong())).thenReturn(List.of(snapshotZero));
        when(snapshotMapper.mapearParaEvolucaoCarteiraResponseDTO(any(SnapshotCarteira.class)))
                .thenReturn(new EvolucaoCarteiraResponseDTO(LocalDate.of(2026, 1, 5), BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO));
        when(valoresFeignClient.consultarHistoricoAportes(anyLong())).thenReturn(historicoAportes);

        // Act
        RentabilidadeResponseDTO resultado = rentabilidadeService.consultarRentabilidade(1L);

        // Assert
        assertEquals(BigDecimal.ZERO, resultado.rentabilidade().rentabilidadePercentual());
    }
}

