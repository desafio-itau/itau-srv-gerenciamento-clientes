package com.itau.srv.gerenciamento.clientes.mapper;

import com.itau.srv.gerenciamento.clientes.dto.contagrafica.ContaGraficaResponseDTO;
import com.itau.srv.gerenciamento.clientes.model.Cliente;
import com.itau.srv.gerenciamento.clientes.model.ContaGrafica;
import com.itau.srv.gerenciamento.clientes.model.enums.TipoConta;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ContaGraficaMapperTest {

    private ContaGraficaMapper contaGraficaMapper;
    private ContaGrafica contaGrafica;
    private LocalDateTime dataAtual;

    @BeforeEach
    void setUp() {
        contaGraficaMapper = new ContaGraficaMapper();
        dataAtual = LocalDateTime.now();

        Cliente cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNome("João Silva");
        cliente.setCpf("12345678901");

        contaGrafica = new ContaGrafica();
        contaGrafica.setId(1L);
        contaGrafica.setCliente(cliente);
        contaGrafica.setNumeroConta("ITAUFL00001");
        contaGrafica.setTipo(TipoConta.FILHOTE);
        contaGrafica.setDataCriacao(dataAtual);
    }

    @Test
    void devMapearContaGraficaParaContaGraficaResponseDTO() {
        // Act
        ContaGraficaResponseDTO resultado = contaGraficaMapper.mapearParaContaGraficaResponseDTO(contaGrafica);

        // Assert
        assertNotNull(resultado);
        assertEquals(contaGrafica.getId(), resultado.id());
        assertEquals(contaGrafica.getNumeroConta(), resultado.numeroConta());
        assertEquals(contaGrafica.getTipo(), resultado.tipoConta());
        assertEquals(contaGrafica.getDataCriacao(), resultado.dataCriacao());
    }

    @Test
    void devMapearContaGraficaComIdNulo() {
        // Arrange
        contaGrafica.setId(null);

        // Act
        ContaGraficaResponseDTO resultado = contaGraficaMapper.mapearParaContaGraficaResponseDTO(contaGrafica);

        // Assert
        assertNotNull(resultado);
        assertNull(resultado.id());
        assertEquals(contaGrafica.getNumeroConta(), resultado.numeroConta());
    }

    @Test
    void devMapearContaGraficaComNumeroContaNulo() {
        // Arrange
        contaGrafica.setNumeroConta(null);

        // Act
        ContaGraficaResponseDTO resultado = contaGraficaMapper.mapearParaContaGraficaResponseDTO(contaGrafica);

        // Assert
        assertNotNull(resultado);
        assertNull(resultado.numeroConta());
        assertEquals(contaGrafica.getId(), resultado.id());
    }

    @Test
    void devMapearContaGraficaComDiferentesNumerosConta() {
        // Arrange
        String[] numerosConta = {"ITAUFL00001", "ITAUFL00010", "ITAUFL00100", "ITAUFL99999"};

        for (String numero : numerosConta) {
            contaGrafica.setNumeroConta(numero);

            // Act
            ContaGraficaResponseDTO resultado = contaGraficaMapper.mapearParaContaGraficaResponseDTO(contaGrafica);

            // Assert
            assertNotNull(resultado);
            assertEquals(numero, resultado.numeroConta());
        }
    }
}

