package com.itau.srv.gerenciamento.clientes.mapper;

import com.itau.srv.gerenciamento.clientes.dto.adesao.AdesaoRequestDTO;
import com.itau.srv.gerenciamento.clientes.dto.adesao.AdesaoResponseDTO;
import com.itau.srv.gerenciamento.clientes.model.Cliente;
import com.itau.srv.gerenciamento.clientes.model.ContaGrafica;
import com.itau.srv.gerenciamento.clientes.model.enums.TipoConta;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ClienteMapperTest {

    private ContaGraficaMapper contaGraficaMapper;
    private ClienteMapper clienteMapper;

    private AdesaoRequestDTO adesaoRequestDTO;
    private Cliente cliente;
    private ContaGrafica contaGrafica;

    @BeforeEach
    void setUp() {
        // Inicializar mappers
        contaGraficaMapper = new ContaGraficaMapper();
        clienteMapper = new ClienteMapper(contaGraficaMapper);

        // Arrange - Setup dos objetos de teste
        adesaoRequestDTO = new AdesaoRequestDTO(
                "João Silva",
                "12345678901",
                "joao@email.com",
                new BigDecimal("150.00")
        );

        cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNome("João Silva");
        cliente.setCpf("12345678901");
        cliente.setEmail("joao@email.com");
        cliente.setValorMensal(new BigDecimal("150.00"));
        cliente.setAtivo(true);
        cliente.setDataAdesao(LocalDateTime.now());

        contaGrafica = new ContaGrafica();
        contaGrafica.setId(1L);
        contaGrafica.setCliente(cliente);
        contaGrafica.setNumeroConta("ITAUFL00001");
        contaGrafica.setTipo(TipoConta.FILHOTE);
        contaGrafica.setDataCriacao(LocalDateTime.now());
    }

    @Test
    void devMapearAdesaoRequestDTOParaCliente() {
        // Act
        Cliente resultado = clienteMapper.mapearParaCliente(adesaoRequestDTO);

        // Assert
        assertNotNull(resultado);
        assertEquals(adesaoRequestDTO.nome(), resultado.getNome());
        assertEquals(adesaoRequestDTO.cpf(), resultado.getCpf());
        assertEquals(adesaoRequestDTO.email(), resultado.getEmail());
        assertEquals(adesaoRequestDTO.valorMensal(), resultado.getValorMensal());
    }

    @Test
    void devMapearAdesaoRequestDTOParaClienteComValoresNulos() {
        // Arrange
        AdesaoRequestDTO dtoComNulos = new AdesaoRequestDTO(null, null, null, null);

        // Act
        Cliente resultado = clienteMapper.mapearParaCliente(dtoComNulos);

        // Assert
        assertNotNull(resultado);
        assertNull(resultado.getNome());
        assertNull(resultado.getCpf());
        assertNull(resultado.getEmail());
        assertNull(resultado.getValorMensal());
    }

    @Test
    void devMapearClienteEContaGraficaParaAdesaoResponseDTO() {
        // Act
        AdesaoResponseDTO resultado = clienteMapper.mapearParaAdesaoResponseDTO(cliente, contaGrafica);

        // Assert
        assertNotNull(resultado);
        assertEquals(cliente.getId(), resultado.clienteId());
        assertEquals(cliente.getNome(), resultado.nome());
        assertEquals(cliente.getCpf(), resultado.cpf());
        assertEquals(cliente.getEmail(), resultado.email());
        assertEquals(cliente.getValorMensal(), resultado.valorMensal());
        assertEquals(cliente.getAtivo(), resultado.ativo());
        assertEquals(cliente.getDataAdesao(), resultado.dataAdesao());
        assertNotNull(resultado.contaGrafica());
        assertEquals(contaGrafica.getId(), resultado.contaGrafica().id());
        assertEquals(contaGrafica.getNumeroConta(), resultado.contaGrafica().numeroConta());
    }

    @Test
    void devMapearClienteComDadosMinimosParaAdesaoResponseDTO() {
        // Arrange
        Cliente clienteMinimo = new Cliente();
        clienteMinimo.setId(1L);
        clienteMinimo.setNome("Test");
        clienteMinimo.setCpf("12345678901");
        clienteMinimo.setEmail("test@test.com");
        clienteMinimo.setValorMensal(BigDecimal.ZERO);
        clienteMinimo.setAtivo(false);
        clienteMinimo.setDataAdesao(LocalDateTime.now());

        // Act
        AdesaoResponseDTO resultado = clienteMapper.mapearParaAdesaoResponseDTO(clienteMinimo, contaGrafica);

        // Assert
        assertNotNull(resultado);
        assertEquals(clienteMinimo.getId(), resultado.clienteId());
        assertEquals(clienteMinimo.getNome(), resultado.nome());
        assertFalse(resultado.ativo());
        assertEquals(BigDecimal.ZERO, resultado.valorMensal());
    }
}






