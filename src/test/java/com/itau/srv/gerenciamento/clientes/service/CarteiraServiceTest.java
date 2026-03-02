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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
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
}

