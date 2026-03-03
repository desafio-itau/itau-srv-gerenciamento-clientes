package com.itau.srv.gerenciamento.clientes.service;

import com.itau.common.library.exception.RecursoNaoEncontradoException;
import com.itau.srv.gerenciamento.clientes.dto.carteira.AtivoResponseDTO;
import com.itau.srv.gerenciamento.clientes.dto.carteira.CarteiraResponseDTO;
import com.itau.srv.gerenciamento.clientes.dto.carteira.ResumoResponseDTO;
import com.itau.srv.gerenciamento.clientes.dto.custodia.CustodiaResponseDTO;
import com.itau.srv.gerenciamento.clientes.dto.valor.ValoresPorDataResponseDTO;
import com.itau.srv.gerenciamento.clientes.dto.valor.ValoresResponseDTO;
import com.itau.srv.gerenciamento.clientes.feign.CustodiasFeignClient;
import com.itau.srv.gerenciamento.clientes.feign.ValoresFeignClient;
import com.itau.srv.gerenciamento.clientes.mapper.CarteiraMaper;
import com.itau.srv.gerenciamento.clientes.model.Cliente;
import com.itau.srv.gerenciamento.clientes.model.ContaGrafica;
import com.itau.srv.gerenciamento.clientes.model.SnapshotCarteira;
import com.itau.srv.gerenciamento.clientes.repository.ClienteRepository;
import com.itau.srv.gerenciamento.clientes.repository.ContaGraficaRepository;
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
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CarteiraServiceTest {

    @Mock
    private ContaGraficaRepository contaGraficaRepository;

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private CustodiasFeignClient custodiasFeignClient;

    @Mock
    private CarteiraMaper carteiraMaper;

    @Mock
    private ValoresFeignClient valoresFeignClient;

    @Mock
    private SnapshotCarteiraRepository snapshotCarteiraRepository;

    @InjectMocks
    private CarteiraService carteiraService;

    private Cliente cliente;
    private ContaGrafica contaGrafica;
    private List<CustodiaResponseDTO> custodias;
    private ValoresResponseDTO valores;
    private CarteiraResponseDTO carteiraResponseDTO;

    @BeforeEach
    void setUp() {
        cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNome("João Silva");
        cliente.setCpf("12345678901");
        cliente.setEmail("joao@email.com");

        contaGrafica = new ContaGrafica();
        contaGrafica.setId(1L);
        contaGrafica.setNumeroConta("ITAUFL00001");
        contaGrafica.setCliente(cliente);

        custodias = Arrays.asList(
                new CustodiaResponseDTO("PETR4", 100, new BigDecimal("30.00"), new BigDecimal("35.00"), "COMPRA"),
                new CustodiaResponseDTO("VALE3", 50, new BigDecimal("60.00"), new BigDecimal("65.00"), "COMPRA")
        );

        valores = new ValoresResponseDTO(new BigDecimal("6000.00"), new BigDecimal("0.00"));

        ResumoResponseDTO resumo = new ResumoResponseDTO(
                new BigDecimal("6000.00"),
                new BigDecimal("6750.00"),
                new BigDecimal("750.00"),
                new BigDecimal("12.50")
        );

        List<AtivoResponseDTO> ativos = Arrays.asList(
                new AtivoResponseDTO("PETR4", 100, new BigDecimal("30.00"), new BigDecimal("35.00"),
                        new BigDecimal("500.00"), new BigDecimal("0.67"), new BigDecimal("0.52")),
                new AtivoResponseDTO("VALE3", 50, new BigDecimal("60.00"), new BigDecimal("65.00"),
                        new BigDecimal("250.00"), new BigDecimal("0.33"), new BigDecimal("0.48"))
        );

        carteiraResponseDTO = new CarteiraResponseDTO(
                1L,
                "João Silva",
                "ITAUFL00001",
                LocalDateTime.now(),
                resumo,
                ativos
        );
    }

    @Test
    void deveConsultarCarteiraComSucesso() {
        // Arrange
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(contaGraficaRepository.findByCliente(cliente)).thenReturn(contaGrafica);
        when(custodiasFeignClient.obterCustodiasPorClienteId(1L)).thenReturn(custodias);
        when(valoresFeignClient.obterValoresPorCliente(1L)).thenReturn(valores);
        when(carteiraMaper.mapearParaCarteiraResponseDTO(
                eq(cliente),
                eq(contaGrafica),
                any(ResumoResponseDTO.class),
                anyList()
        )).thenReturn(carteiraResponseDTO);

        // Act
        CarteiraResponseDTO resultado = carteiraService.consultarCarteiraCliente(1L);

        // Assert
        assertNotNull(resultado);
        assertEquals(1L, resultado.clienteId());
        assertEquals("João Silva", resultado.nome());
        assertEquals("ITAUFL00001", resultado.contaGrafica());
        assertNotNull(resultado.resumo());
        assertNotNull(resultado.ativos());
        assertEquals(2, resultado.ativos().size());

        verify(clienteRepository).findById(1L);
        verify(contaGraficaRepository).findByCliente(cliente);
        verify(custodiasFeignClient).obterCustodiasPorClienteId(1L);
        verify(valoresFeignClient).obterValoresPorCliente(1L);
        verify(carteiraMaper).mapearParaCarteiraResponseDTO(
                eq(cliente),
                eq(contaGrafica),
                any(ResumoResponseDTO.class),
                anyList()
        );
    }

    @Test
    void deveLancarExcecaoQuandoClienteNaoEncontrado() {
        // Arrange
        when(clienteRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        RecursoNaoEncontradoException exception = assertThrows(
                RecursoNaoEncontradoException.class,
                () -> carteiraService.consultarCarteiraCliente(1L)
        );

        assertEquals("CLIENTE_NAO_ENCONTRADO", exception.getMessage());
        verify(clienteRepository).findById(1L);
        verifyNoInteractions(contaGraficaRepository, custodiasFeignClient, valoresFeignClient, carteiraMaper);
    }

    @Test
    void deveCalcularCarteiraVaziaQuandoNaoHaCustodias() {
        // Arrange
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(contaGraficaRepository.findByCliente(cliente)).thenReturn(contaGrafica);
        when(custodiasFeignClient.obterCustodiasPorClienteId(1L)).thenReturn(Collections.emptyList());
        when(valoresFeignClient.obterValoresPorCliente(1L)).thenReturn(new ValoresResponseDTO(BigDecimal.ZERO, BigDecimal.ZERO));
        when(carteiraMaper.mapearParaCarteiraResponseDTO(
                eq(cliente),
                eq(contaGrafica),
                any(ResumoResponseDTO.class),
                anyList()
        )).thenReturn(carteiraResponseDTO);

        // Act
        CarteiraResponseDTO resultado = carteiraService.consultarCarteiraCliente(1L);

        // Assert
        assertNotNull(resultado);
        verify(clienteRepository).findById(1L);
        verify(contaGraficaRepository).findByCliente(cliente);
        verify(custodiasFeignClient).obterCustodiasPorClienteId(1L);
        verify(valoresFeignClient).obterValoresPorCliente(1L);
        verify(carteiraMaper).mapearParaCarteiraResponseDTO(
                eq(cliente),
                eq(contaGrafica),
                any(ResumoResponseDTO.class),
                anyList()
        );
    }

    @Test
    void deveCalcularValoresCorretamente() {
        // Arrange
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(contaGraficaRepository.findByCliente(cliente)).thenReturn(contaGrafica);
        when(custodiasFeignClient.obterCustodiasPorClienteId(1L)).thenReturn(custodias);
        when(valoresFeignClient.obterValoresPorCliente(1L)).thenReturn(valores);
        when(carteiraMaper.mapearParaCarteiraResponseDTO(
                eq(cliente),
                eq(contaGrafica),
                any(ResumoResponseDTO.class),
                anyList()
        )).thenAnswer(invocation -> {
            ResumoResponseDTO resumo = invocation.getArgument(2);
            List<AtivoResponseDTO> ativos = invocation.getArgument(3);

            // Verificar valores calculados
            assertEquals(new BigDecimal("6000.00"), resumo.valorTotalInvestido());
            assertEquals(new BigDecimal("6750.00"), resumo.valorAtualCarteira());
            assertEquals(new BigDecimal("750.00"), resumo.plTotal());
            assertEquals(2, ativos.size());

            return carteiraResponseDTO;
        });

        // Act
        CarteiraResponseDTO resultado = carteiraService.consultarCarteiraCliente(1L);

        // Assert
        assertNotNull(resultado);
        verify(carteiraMaper).mapearParaCarteiraResponseDTO(
                eq(cliente),
                eq(contaGrafica),
                any(ResumoResponseDTO.class),
                anyList()
        );
    }

    @Test
    void deveEvitarDivisaoPorZeroQuandoValorInvestidoZero() {
        // Arrange
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(contaGraficaRepository.findByCliente(cliente)).thenReturn(contaGrafica);
        when(custodiasFeignClient.obterCustodiasPorClienteId(1L)).thenReturn(Collections.emptyList());
        when(valoresFeignClient.obterValoresPorCliente(1L)).thenReturn(new ValoresResponseDTO(BigDecimal.ZERO, BigDecimal.ZERO));
        when(carteiraMaper.mapearParaCarteiraResponseDTO(
                eq(cliente),
                eq(contaGrafica),
                any(ResumoResponseDTO.class),
                anyList()
        )).thenAnswer(invocation -> {
            ResumoResponseDTO resumo = invocation.getArgument(2);

            // Verificar que rentabilidade é zero quando não há investimento
            assertEquals(BigDecimal.ZERO, resumo.rentabilidadePercentual());

            return carteiraResponseDTO;
        });

        // Act & Assert
        assertDoesNotThrow(() -> carteiraService.consultarCarteiraCliente(1L));
    }

    @Test
    void deveCalcularComposicaoDaCarteiraCorretamente() {
        // Arrange
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(contaGraficaRepository.findByCliente(cliente)).thenReturn(contaGrafica);
        when(custodiasFeignClient.obterCustodiasPorClienteId(1L)).thenReturn(custodias);
        when(valoresFeignClient.obterValoresPorCliente(1L)).thenReturn(valores);
        when(carteiraMaper.mapearParaCarteiraResponseDTO(
                eq(cliente),
                eq(contaGrafica),
                any(ResumoResponseDTO.class),
                anyList()
        )).thenAnswer(invocation -> {
            List<AtivoResponseDTO> ativos = invocation.getArgument(3);

            // Verificar que cada ativo tem composição calculada
            for (AtivoResponseDTO ativo : ativos) {
                assertNotNull(ativo.composicaoCarteira());
                assertTrue(ativo.composicaoCarteira().compareTo(BigDecimal.ZERO) >= 0);
                assertTrue(ativo.composicaoCarteira().compareTo(BigDecimal.ONE) <= 0);
            }

            return carteiraResponseDTO;
        });

        // Act
        CarteiraResponseDTO resultado = carteiraService.consultarCarteiraCliente(1L);

        // Assert
        assertNotNull(resultado);
    }

    @Test
    void deveCalcularPLCorretamenteParaCadaAtivo() {
        // Arrange
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(contaGraficaRepository.findByCliente(cliente)).thenReturn(contaGrafica);
        when(custodiasFeignClient.obterCustodiasPorClienteId(1L)).thenReturn(custodias);
        when(valoresFeignClient.obterValoresPorCliente(1L)).thenReturn(valores);
        when(carteiraMaper.mapearParaCarteiraResponseDTO(
                eq(cliente),
                eq(contaGrafica),
                any(ResumoResponseDTO.class),
                anyList()
        )).thenAnswer(invocation -> {
            List<AtivoResponseDTO> ativos = invocation.getArgument(3);

            // Verificar PL de PETR4: (35 - 30) * 100 = 500
            AtivoResponseDTO petr4 = ativos.stream()
                    .filter(a -> a.ticker().equals("PETR4"))
                    .findFirst()
                    .orElseThrow();
            assertEquals(new BigDecimal("500.00"), petr4.pl());

            // Verificar PL de VALE3: (65 - 60) * 50 = 250
            AtivoResponseDTO vale3 = ativos.stream()
                    .filter(a -> a.ticker().equals("VALE3"))
                    .findFirst()
                    .orElseThrow();
            assertEquals(new BigDecimal("250.00"), vale3.pl());

            return carteiraResponseDTO;
        });

        // Act
        CarteiraResponseDTO resultado = carteiraService.consultarCarteiraCliente(1L);

        // Assert
        assertNotNull(resultado);
    }

    @Test
    void deveSubtrairValorVendidoDoValorInvestido() {
        // Arrange
        ValoresResponseDTO valoresComVenda = new ValoresResponseDTO(
                new BigDecimal("10000.00"),
                new BigDecimal("4000.00")
        );

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(contaGraficaRepository.findByCliente(cliente)).thenReturn(contaGrafica);
        when(custodiasFeignClient.obterCustodiasPorClienteId(1L)).thenReturn(custodias);
        when(valoresFeignClient.obterValoresPorCliente(1L)).thenReturn(valoresComVenda);
        when(carteiraMaper.mapearParaCarteiraResponseDTO(
                eq(cliente),
                eq(contaGrafica),
                any(ResumoResponseDTO.class),
                anyList()
        )).thenAnswer(invocation -> {
            ResumoResponseDTO resumo = invocation.getArgument(2);

            // Valor investido deve ser: 10000 - 4000 = 6000
            assertEquals(new BigDecimal("6000.00"), resumo.valorTotalInvestido());

            return carteiraResponseDTO;
        });

        // Act
        CarteiraResponseDTO resultado = carteiraService.consultarCarteiraCliente(1L);

        // Assert
        assertNotNull(resultado);
        verify(valoresFeignClient).obterValoresPorCliente(1L);
    }

    // =========== TESTES PARA GERAR SNAPSHOTS ===========

    @Test
    void deveGerarSnapshotsComSucesso() {
        // Arrange
        LocalDate data = LocalDate.of(2026, 2, 5);

        Cliente cliente1 = new Cliente();
        cliente1.setId(1L);
        cliente1.setNome("Cliente 1");

        Cliente cliente2 = new Cliente();
        cliente2.setId(2L);
        cliente2.setNome("Cliente 2");

        List<Cliente> clientes = Arrays.asList(cliente1, cliente2);

        List<CustodiaResponseDTO> custodias1 = Arrays.asList(
                new CustodiaResponseDTO("PETR4", 100, new BigDecimal("30.00"), new BigDecimal("35.00"), "COMPRA")
        );

        List<CustodiaResponseDTO> custodias2 = Arrays.asList(
                new CustodiaResponseDTO("VALE3", 50, new BigDecimal("60.00"), new BigDecimal("65.00"), "COMPRA")
        );

        ValoresResponseDTO valores1 = new ValoresResponseDTO(new BigDecimal("3000.00"), BigDecimal.ZERO);
        ValoresResponseDTO valores2 = new ValoresResponseDTO(new BigDecimal("3000.00"), BigDecimal.ZERO);

        ValoresPorDataResponseDTO valoresPorData1 = new ValoresPorDataResponseDTO(data, valores1);
        ValoresPorDataResponseDTO valoresPorData2 = new ValoresPorDataResponseDTO(data, valores2);

        when(clienteRepository.findAllAtivos()).thenReturn(clientes);
        when(custodiasFeignClient.obterCustodiasPorClienteId(1L)).thenReturn(custodias1);
        when(custodiasFeignClient.obterCustodiasPorClienteId(2L)).thenReturn(custodias2);
        when(valoresFeignClient.obterValoresPorClienteEData(1L, data)).thenReturn(valoresPorData1);
        when(valoresFeignClient.obterValoresPorClienteEData(2L, data)).thenReturn(valoresPorData2);
        when(snapshotCarteiraRepository.saveAll(anyList())).thenReturn(Collections.emptyList());

        // Act
        carteiraService.gerarSnapshots(data);

        // Assert
        verify(clienteRepository).findAllAtivos();
        verify(custodiasFeignClient).obterCustodiasPorClienteId(1L);
        verify(custodiasFeignClient).obterCustodiasPorClienteId(2L);
        verify(valoresFeignClient).obterValoresPorClienteEData(1L, data);
        verify(valoresFeignClient).obterValoresPorClienteEData(2L, data);
        verify(snapshotCarteiraRepository).saveAll(anyList());
    }

    @Test
    void deveCalcularValorCarteiraCorretamente() {
        // Arrange
        LocalDate data = LocalDate.of(2026, 2, 5);
        Cliente cliente = new Cliente();
        cliente.setId(1L);

        List<CustodiaResponseDTO> custodias = Arrays.asList(
                new CustodiaResponseDTO("PETR4", 100, new BigDecimal("30.00"), new BigDecimal("35.00"), "COMPRA"),
                new CustodiaResponseDTO("VALE3", 50, new BigDecimal("60.00"), new BigDecimal("65.00"), "COMPRA")
        );

        // Valor esperado: (35 * 100) + (65 * 50) = 3500 + 3250 = 6750
        ValoresResponseDTO valores = new ValoresResponseDTO(new BigDecimal("6000.00"), BigDecimal.ZERO);
        ValoresPorDataResponseDTO valoresPorData = new ValoresPorDataResponseDTO(data, valores);

        when(clienteRepository.findAllAtivos()).thenReturn(Collections.singletonList(cliente));
        when(custodiasFeignClient.obterCustodiasPorClienteId(1L)).thenReturn(custodias);
        when(valoresFeignClient.obterValoresPorClienteEData(1L, data)).thenReturn(valoresPorData);
        when(snapshotCarteiraRepository.saveAll(anyList())).thenAnswer(invocation -> {
            List<SnapshotCarteira> snapshots = invocation.getArgument(0);
            assertEquals(1, snapshots.size());
            assertEquals(new BigDecimal("6750.00"), snapshots.get(0).getValorCarteira());
            return snapshots;
        });

        // Act
        carteiraService.gerarSnapshots(data);

        // Assert
        verify(snapshotCarteiraRepository).saveAll(anyList());
    }

    @Test
    void deveCalcularRentabilidadeCorretamente() {
        // Arrange
        LocalDate data = LocalDate.of(2026, 2, 5);
        Cliente cliente = new Cliente();
        cliente.setId(1L);

        List<CustodiaResponseDTO> custodias = Arrays.asList(
                new CustodiaResponseDTO("PETR4", 100, new BigDecimal("30.00"), new BigDecimal("35.00"), "COMPRA")
        );

        // Valor carteira: 35 * 100 = 3500
        // Valor investido: 3000 - 0 = 3000
        // Rentabilidade: ((3500 - 3000) / 3000) * 100 = 16.66%
        ValoresResponseDTO valores = new ValoresResponseDTO(new BigDecimal("3000.00"), BigDecimal.ZERO);
        ValoresPorDataResponseDTO valoresPorData = new ValoresPorDataResponseDTO(data, valores);

        when(clienteRepository.findAllAtivos()).thenReturn(Collections.singletonList(cliente));
        when(custodiasFeignClient.obterCustodiasPorClienteId(1L)).thenReturn(custodias);
        when(valoresFeignClient.obterValoresPorClienteEData(1L, data)).thenReturn(valoresPorData);
        when(snapshotCarteiraRepository.saveAll(anyList())).thenAnswer(invocation -> {
            List<SnapshotCarteira> snapshots = invocation.getArgument(0);
            assertEquals(1, snapshots.size());
            assertEquals(new BigDecimal("16.66"), snapshots.get(0).getRentabilidade());
            return snapshots;
        });

        // Act
        carteiraService.gerarSnapshots(data);

        // Assert
        verify(snapshotCarteiraRepository).saveAll(anyList());
    }

    @Test
    void deveRetornarRentabilidadeZeroQuandoValorInvestidoForZero() {
        // Arrange
        LocalDate data = LocalDate.of(2026, 2, 5);
        Cliente cliente = new Cliente();
        cliente.setId(1L);

        List<CustodiaResponseDTO> custodias = Arrays.asList(
                new CustodiaResponseDTO("PETR4", 100, new BigDecimal("30.00"), new BigDecimal("35.00"), "COMPRA")
        );

        ValoresResponseDTO valores = new ValoresResponseDTO(BigDecimal.ZERO, BigDecimal.ZERO);
        ValoresPorDataResponseDTO valoresPorData = new ValoresPorDataResponseDTO(data, valores);

        when(clienteRepository.findAllAtivos()).thenReturn(Collections.singletonList(cliente));
        when(custodiasFeignClient.obterCustodiasPorClienteId(1L)).thenReturn(custodias);
        when(valoresFeignClient.obterValoresPorClienteEData(1L, data)).thenReturn(valoresPorData);
        when(snapshotCarteiraRepository.saveAll(anyList())).thenAnswer(invocation -> {
            List<SnapshotCarteira> snapshots = invocation.getArgument(0);
            assertEquals(1, snapshots.size());
            assertEquals(BigDecimal.ZERO, snapshots.get(0).getRentabilidade());
            return snapshots;
        });

        // Act
        carteiraService.gerarSnapshots(data);

        // Assert
        verify(snapshotCarteiraRepository).saveAll(anyList());
    }

    @Test
    void deveGerarSnapshotParaTodosClientesAtivos() {
        // Arrange
        LocalDate data = LocalDate.of(2026, 2, 5);

        Cliente cliente1 = new Cliente();
        cliente1.setId(1L);

        Cliente cliente2 = new Cliente();
        cliente2.setId(2L);

        Cliente cliente3 = new Cliente();
        cliente3.setId(3L);

        List<Cliente> clientes = Arrays.asList(cliente1, cliente2, cliente3);

        ValoresResponseDTO valores = new ValoresResponseDTO(new BigDecimal("1000.00"), BigDecimal.ZERO);
        ValoresPorDataResponseDTO valoresPorData = new ValoresPorDataResponseDTO(data, valores);

        when(clienteRepository.findAllAtivos()).thenReturn(clientes);
        when(custodiasFeignClient.obterCustodiasPorClienteId(anyLong())).thenReturn(Collections.emptyList());
        when(valoresFeignClient.obterValoresPorClienteEData(anyLong(), eq(data))).thenReturn(valoresPorData);
        when(snapshotCarteiraRepository.saveAll(anyList())).thenAnswer(invocation -> {
            List<SnapshotCarteira> snapshots = invocation.getArgument(0);
            assertEquals(3, snapshots.size());
            return snapshots;
        });

        // Act
        carteiraService.gerarSnapshots(data);

        // Assert
        verify(clienteRepository).findAllAtivos();
        verify(snapshotCarteiraRepository).saveAll(anyList());
    }

    @Test
    void deveDefinirClienteIdEDataNoSnapshot() {
        // Arrange
        LocalDate data = LocalDate.of(2026, 2, 15);
        Cliente cliente = new Cliente();
        cliente.setId(99L);

        ValoresResponseDTO valores = new ValoresResponseDTO(new BigDecimal("1000.00"), BigDecimal.ZERO);
        ValoresPorDataResponseDTO valoresPorData = new ValoresPorDataResponseDTO(data, valores);

        when(clienteRepository.findAllAtivos()).thenReturn(Collections.singletonList(cliente));
        when(custodiasFeignClient.obterCustodiasPorClienteId(99L)).thenReturn(Collections.emptyList());
        when(valoresFeignClient.obterValoresPorClienteEData(99L, data)).thenReturn(valoresPorData);
        when(snapshotCarteiraRepository.saveAll(anyList())).thenAnswer(invocation -> {
            List<SnapshotCarteira> snapshots = invocation.getArgument(0);
            assertEquals(1, snapshots.size());
            assertEquals(99L, snapshots.get(0).getClienteId());
            assertEquals(data, snapshots.get(0).getDataSnapshot());
            return snapshots;
        });

        // Act
        carteiraService.gerarSnapshots(data);

        // Assert
        verify(snapshotCarteiraRepository).saveAll(anyList());
    }

    @Test
    void deveCalcularValorInvestidoSubtraindoValorVendido() {
        // Arrange
        LocalDate data = LocalDate.of(2026, 2, 5);
        Cliente cliente = new Cliente();
        cliente.setId(1L);

        List<CustodiaResponseDTO> custodias = Arrays.asList(
                new CustodiaResponseDTO("PETR4", 100, new BigDecimal("30.00"), new BigDecimal("35.00"), "COMPRA")
        );

        // Valor investido: 10000 - 4000 = 6000
        ValoresResponseDTO valores = new ValoresResponseDTO(new BigDecimal("10000.00"), new BigDecimal("4000.00"));
        ValoresPorDataResponseDTO valoresPorData = new ValoresPorDataResponseDTO(data, valores);

        when(clienteRepository.findAllAtivos()).thenReturn(Collections.singletonList(cliente));
        when(custodiasFeignClient.obterCustodiasPorClienteId(1L)).thenReturn(custodias);
        when(valoresFeignClient.obterValoresPorClienteEData(1L, data)).thenReturn(valoresPorData);
        when(snapshotCarteiraRepository.saveAll(anyList())).thenAnswer(invocation -> {
            List<SnapshotCarteira> snapshots = invocation.getArgument(0);
            assertEquals(1, snapshots.size());
            assertEquals(new BigDecimal("6000.00"), snapshots.get(0).getValorInvestido());
            return snapshots;
        });

        // Act
        carteiraService.gerarSnapshots(data);

        // Assert
        verify(snapshotCarteiraRepository).saveAll(anyList());
    }

    @Test
    void deveRetornarValorCarteiraZeroQuandoNaoHouverCustodias() {
        // Arrange
        LocalDate data = LocalDate.of(2026, 2, 5);
        Cliente cliente = new Cliente();
        cliente.setId(1L);

        ValoresResponseDTO valores = new ValoresResponseDTO(new BigDecimal("1000.00"), BigDecimal.ZERO);
        ValoresPorDataResponseDTO valoresPorData = new ValoresPorDataResponseDTO(data, valores);

        when(clienteRepository.findAllAtivos()).thenReturn(Collections.singletonList(cliente));
        when(custodiasFeignClient.obterCustodiasPorClienteId(1L)).thenReturn(Collections.emptyList());
        when(valoresFeignClient.obterValoresPorClienteEData(1L, data)).thenReturn(valoresPorData);
        when(snapshotCarteiraRepository.saveAll(anyList())).thenAnswer(invocation -> {
            List<SnapshotCarteira> snapshots = invocation.getArgument(0);
            assertEquals(1, snapshots.size());
            assertEquals(BigDecimal.ZERO, snapshots.get(0).getValorCarteira());
            return snapshots;
        });

        // Act
        carteiraService.gerarSnapshots(data);

        // Assert
        verify(snapshotCarteiraRepository).saveAll(anyList());
    }

    @Test
    void deveNaoGerarSnapshotsQuandoNaoHouverClientesAtivos() {
        // Arrange
        LocalDate data = LocalDate.of(2026, 2, 5);
        when(clienteRepository.findAllAtivos()).thenReturn(Collections.emptyList());
        when(snapshotCarteiraRepository.saveAll(anyList())).thenAnswer(invocation -> {
            List<SnapshotCarteira> snapshots = invocation.getArgument(0);
            assertTrue(snapshots.isEmpty());
            return snapshots;
        });

        // Act
        carteiraService.gerarSnapshots(data);

        // Assert
        verify(clienteRepository).findAllAtivos();
        verify(snapshotCarteiraRepository).saveAll(anyList());
        verify(custodiasFeignClient, never()).obterCustodiasPorClienteId(anyLong());
        verify(valoresFeignClient, never()).obterValoresPorClienteEData(anyLong(), any(LocalDate.class));
    }

    @Test
    void deveChamarFeignClientParaCadaCliente() {
        // Arrange
        LocalDate data = LocalDate.of(2026, 2, 5);

        Cliente cliente1 = new Cliente();
        cliente1.setId(1L);

        Cliente cliente2 = new Cliente();
        cliente2.setId(2L);

        List<Cliente> clientes = Arrays.asList(cliente1, cliente2);

        ValoresResponseDTO valores = new ValoresResponseDTO(new BigDecimal("1000.00"), BigDecimal.ZERO);
        ValoresPorDataResponseDTO valoresPorData = new ValoresPorDataResponseDTO(data, valores);

        when(clienteRepository.findAllAtivos()).thenReturn(clientes);
        when(custodiasFeignClient.obterCustodiasPorClienteId(anyLong())).thenReturn(Collections.emptyList());
        when(valoresFeignClient.obterValoresPorClienteEData(anyLong(), eq(data))).thenReturn(valoresPorData);
        when(snapshotCarteiraRepository.saveAll(anyList())).thenReturn(Collections.emptyList());

        // Act
        carteiraService.gerarSnapshots(data);

        // Assert
        verify(custodiasFeignClient, times(1)).obterCustodiasPorClienteId(1L);
        verify(custodiasFeignClient, times(1)).obterCustodiasPorClienteId(2L);
        verify(valoresFeignClient, times(1)).obterValoresPorClienteEData(1L, data);
        verify(valoresFeignClient, times(1)).obterValoresPorClienteEData(2L, data);
    }

    @Test
    void deveSalvarTodosSnapshotsDeUmaVez() {
        // Arrange
        LocalDate data = LocalDate.of(2026, 2, 5);

        Cliente cliente1 = new Cliente();
        cliente1.setId(1L);

        Cliente cliente2 = new Cliente();
        cliente2.setId(2L);

        List<Cliente> clientes = Arrays.asList(cliente1, cliente2);

        ValoresResponseDTO valores = new ValoresResponseDTO(new BigDecimal("1000.00"), BigDecimal.ZERO);
        ValoresPorDataResponseDTO valoresPorData = new ValoresPorDataResponseDTO(data, valores);

        when(clienteRepository.findAllAtivos()).thenReturn(clientes);
        when(custodiasFeignClient.obterCustodiasPorClienteId(anyLong())).thenReturn(Collections.emptyList());
        when(valoresFeignClient.obterValoresPorClienteEData(anyLong(), eq(data))).thenReturn(valoresPorData);
        when(snapshotCarteiraRepository.saveAll(anyList())).thenReturn(Collections.emptyList());

        // Act
        carteiraService.gerarSnapshots(data);

        // Assert
        verify(snapshotCarteiraRepository, times(1)).saveAll(anyList());
    }
}
