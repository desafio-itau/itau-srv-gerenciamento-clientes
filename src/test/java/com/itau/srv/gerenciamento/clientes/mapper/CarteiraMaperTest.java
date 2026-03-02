package com.itau.srv.gerenciamento.clientes.mapper;

import com.itau.srv.gerenciamento.clientes.dto.carteira.AtivoResponseDTO;
import com.itau.srv.gerenciamento.clientes.dto.carteira.CarteiraResponseDTO;
import com.itau.srv.gerenciamento.clientes.dto.carteira.ResumoResponseDTO;
import com.itau.srv.gerenciamento.clientes.model.Cliente;
import com.itau.srv.gerenciamento.clientes.model.ContaGrafica;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CarteiraMaperTest {

    private CarteiraMaper carteiraMaper;
    private Cliente cliente;
    private ContaGrafica contaGrafica;
    private ResumoResponseDTO resumo;
    private List<AtivoResponseDTO> ativos;

    @BeforeEach
    void setUp() {
        carteiraMaper = new CarteiraMaper();

        cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNome("João Silva");
        cliente.setCpf("12345678901");
        cliente.setEmail("joao@email.com");

        contaGrafica = new ContaGrafica();
        contaGrafica.setId(1L);
        contaGrafica.setNumeroConta("ITAUFL00001");
        contaGrafica.setCliente(cliente);

        resumo = new ResumoResponseDTO(
                new BigDecimal("5000.00"),
                new BigDecimal("5500.00"),
                new BigDecimal("500.00"),
                new BigDecimal("10.00")
        );

        ativos = Arrays.asList(
                new AtivoResponseDTO(
                        "PETR4",
                        100,
                        new BigDecimal("30.00"),
                        new BigDecimal("35.00"),
                        new BigDecimal("500.00"),
                        new BigDecimal("0.50"),
                        new BigDecimal("0.60")
                ),
                new AtivoResponseDTO(
                        "VALE3",
                        50,
                        new BigDecimal("60.00"),
                        new BigDecimal("65.00"),
                        new BigDecimal("250.00"),
                        new BigDecimal("0.50"),
                        new BigDecimal("0.40")
                )
        );
    }

    @Test
    void deveMapeCarteiraComSucesso() {
        // Act
        CarteiraResponseDTO resultado = carteiraMaper.mapearParaCarteiraResponseDTO(
                cliente,
                contaGrafica,
                resumo,
                ativos
        );

        // Assert
        assertNotNull(resultado);
        assertEquals(1L, resultado.clienteId());
        assertEquals("João Silva", resultado.nome());
        assertEquals("ITAUFL00001", resultado.contaGrafica());
        assertNotNull(resultado.dataConsulta());
        assertTrue(resultado.dataConsulta().isBefore(LocalDateTime.now().plusSeconds(1)));
        assertNotNull(resultado.resumo());
        assertNotNull(resultado.ativos());
    }

    @Test
    void deveMapeClienteCorretamente() {
        // Act
        CarteiraResponseDTO resultado = carteiraMaper.mapearParaCarteiraResponseDTO(
                cliente,
                contaGrafica,
                resumo,
                ativos
        );

        // Assert
        assertEquals(cliente.getId(), resultado.clienteId());
        assertEquals(cliente.getNome(), resultado.nome());
    }

    @Test
    void deveMapeContaGraficaCorretamente() {
        // Act
        CarteiraResponseDTO resultado = carteiraMaper.mapearParaCarteiraResponseDTO(
                cliente,
                contaGrafica,
                resumo,
                ativos
        );

        // Assert
        assertEquals(contaGrafica.getNumeroConta(), resultado.contaGrafica());
    }

    @Test
    void deveMapeResumoCorretamente() {
        // Act
        CarteiraResponseDTO resultado = carteiraMaper.mapearParaCarteiraResponseDTO(
                cliente,
                contaGrafica,
                resumo,
                ativos
        );

        // Assert
        assertEquals(resumo, resultado.resumo());
        assertEquals(new BigDecimal("5000.00"), resultado.resumo().valorTotalInvestido());
        assertEquals(new BigDecimal("5500.00"), resultado.resumo().valorAtualCarteira());
        assertEquals(new BigDecimal("500.00"), resultado.resumo().plTotal());
        assertEquals(new BigDecimal("10.00"), resultado.resumo().rentabilidadePercentual());
    }

    @Test
    void deveMapeAtivosCorretamente() {
        // Act
        CarteiraResponseDTO resultado = carteiraMaper.mapearParaCarteiraResponseDTO(
                cliente,
                contaGrafica,
                resumo,
                ativos
        );

        // Assert
        assertEquals(ativos, resultado.ativos());
        assertEquals(2, resultado.ativos().size());

        AtivoResponseDTO primeiroAtivo = resultado.ativos().get(0);
        assertEquals("PETR4", primeiroAtivo.ticker());
        assertEquals(100, primeiroAtivo.quantidade());
        assertEquals(new BigDecimal("30.00"), primeiroAtivo.precoMedio());
        assertEquals(new BigDecimal("35.00"), primeiroAtivo.cotacaoAtual());
    }

    @Test
    void deveDefinirDataConsultaComoDataAtual() {
        // Arrange
        LocalDateTime antes = LocalDateTime.now().minusSeconds(1);

        // Act
        CarteiraResponseDTO resultado = carteiraMaper.mapearParaCarteiraResponseDTO(
                cliente,
                contaGrafica,
                resumo,
                ativos
        );

        LocalDateTime depois = LocalDateTime.now().plusSeconds(1);

        // Assert
        assertTrue(resultado.dataConsulta().isAfter(antes));
        assertTrue(resultado.dataConsulta().isBefore(depois));
    }

    @Test
    void deveMapeCarteiraComListaVaziaDeAtivos() {
        // Act
        CarteiraResponseDTO resultado = carteiraMaper.mapearParaCarteiraResponseDTO(
                cliente,
                contaGrafica,
                resumo,
                Collections.emptyList()
        );

        // Assert
        assertNotNull(resultado);
        assertNotNull(resultado.ativos());
        assertTrue(resultado.ativos().isEmpty());
    }

    @Test
    void deveMapeCarteiraComResumoZerado() {
        // Arrange
        ResumoResponseDTO resumoZerado = new ResumoResponseDTO(
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO
        );

        // Act
        CarteiraResponseDTO resultado = carteiraMaper.mapearParaCarteiraResponseDTO(
                cliente,
                contaGrafica,
                resumoZerado,
                Collections.emptyList()
        );

        // Assert
        assertNotNull(resultado);
        assertEquals(BigDecimal.ZERO, resultado.resumo().valorTotalInvestido());
        assertEquals(BigDecimal.ZERO, resultado.resumo().valorAtualCarteira());
        assertEquals(BigDecimal.ZERO, resultado.resumo().plTotal());
        assertEquals(BigDecimal.ZERO, resultado.resumo().rentabilidadePercentual());
    }

    @Test
    void deveMapeCarteiraComUmUnicoAtivo() {
        // Arrange
        List<AtivoResponseDTO> umAtivo = Collections.singletonList(
                new AtivoResponseDTO(
                        "PETR4",
                        100,
                        new BigDecimal("30.00"),
                        new BigDecimal("35.00"),
                        new BigDecimal("500.00"),
                        new BigDecimal("1.00"),
                        new BigDecimal("1.00")
                )
        );

        // Act
        CarteiraResponseDTO resultado = carteiraMaper.mapearParaCarteiraResponseDTO(
                cliente,
                contaGrafica,
                resumo,
                umAtivo
        );

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.ativos().size());
        assertEquals("PETR4", resultado.ativos().get(0).ticker());
    }

    @Test
    void deveManterOrdemDosAtivos() {
        // Act
        CarteiraResponseDTO resultado = carteiraMaper.mapearParaCarteiraResponseDTO(
                cliente,
                contaGrafica,
                resumo,
                ativos
        );

        // Assert
        assertEquals("PETR4", resultado.ativos().get(0).ticker());
        assertEquals("VALE3", resultado.ativos().get(1).ticker());
    }
}

