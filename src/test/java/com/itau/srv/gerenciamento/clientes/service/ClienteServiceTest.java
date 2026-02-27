package com.itau.srv.gerenciamento.clientes.service;

import com.itau.srv.gerenciamento.clientes.dto.adesao.AdesaoRequestDTO;
import com.itau.srv.gerenciamento.clientes.dto.adesao.AdesaoResponseDTO;
import com.itau.srv.gerenciamento.clientes.mapper.ClienteMapper;
import com.itau.srv.gerenciamento.clientes.model.Cliente;
import com.itau.srv.gerenciamento.clientes.model.ContaGrafica;
import com.itau.srv.gerenciamento.clientes.model.enums.TipoConta;
import com.itau.srv.gerenciamento.clientes.repository.ClienteRepository;
import com.itau.srv.gerenciamento.clientes.validator.ClienteValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClienteServiceTest {

    @Mock
    private ClienteValidator clienteValidator;

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private ClienteMapper clienteMapper;

    @Mock
    private ContaGraficaService contaGraficaService;

    @InjectMocks
    private ClienteService clienteService;

    private AdesaoRequestDTO adesaoRequestDTO;
    private Cliente cliente;
    private Cliente clienteSalvo;
    private ContaGrafica contaGrafica;
    private AdesaoResponseDTO adesaoResponseDTO;

    @BeforeEach
    void setUp() {
        adesaoRequestDTO = new AdesaoRequestDTO(
                "João Silva",
                "12345678901",
                "joao@email.com",
                new BigDecimal("150.00")
        );

        cliente = new Cliente();
        cliente.setNome("João Silva");
        cliente.setCpf("12345678901");
        cliente.setEmail("joao@email.com");
        cliente.setValorMensal(new BigDecimal("150.00"));

        clienteSalvo = new Cliente();
        clienteSalvo.setId(1L);
        clienteSalvo.setNome("João Silva");
        clienteSalvo.setCpf("12345678901");
        clienteSalvo.setEmail("joao@email.com");
        clienteSalvo.setValorMensal(new BigDecimal("150.00"));
        clienteSalvo.setAtivo(true);
        clienteSalvo.setDataAdesao(LocalDateTime.now());

        contaGrafica = new ContaGrafica();
        contaGrafica.setId(1L);
        contaGrafica.setCliente(clienteSalvo);
        contaGrafica.setNumeroConta("ITAUFL00001");
        contaGrafica.setTipo(TipoConta.FILHOTE);
        contaGrafica.setDataCriacao(LocalDateTime.now());

        adesaoResponseDTO = new AdesaoResponseDTO(
                1L,
                "João Silva",
                "12345678901",
                "joao@email.com",
                new BigDecimal("150.00"),
                true,
                LocalDateTime.now(),
                null
        );
    }

    @Test
    void deveAderirAoProdutoComSucesso() {
        // Arrange
        when(clienteMapper.mapearParaCliente(any(AdesaoRequestDTO.class))).thenReturn(cliente);
        doNothing().when(clienteValidator).validarCpf(any(Cliente.class));
        when(clienteRepository.save(any(Cliente.class))).thenReturn(clienteSalvo);
        when(contaGraficaService.criarContaGrafica(any(Cliente.class))).thenReturn(contaGrafica);
        when(clienteMapper.mapearParaAdesaoResponseDTO(any(Cliente.class), any(ContaGrafica.class)))
                .thenReturn(adesaoResponseDTO);

        // Act
        AdesaoResponseDTO resultado = clienteService.aderirAoProduto(adesaoRequestDTO);

        // Assert
        assertNotNull(resultado);
        assertEquals(adesaoResponseDTO.clienteId(), resultado.clienteId());
        assertEquals(adesaoResponseDTO.nome(), resultado.nome());
        assertEquals(adesaoResponseDTO.cpf(), resultado.cpf());
        assertEquals(adesaoResponseDTO.email(), resultado.email());

        verify(clienteMapper, times(1)).mapearParaCliente(adesaoRequestDTO);
        verify(clienteValidator, times(1)).validarCpf(cliente);
        verify(clienteRepository, times(1)).save(cliente);
        verify(contaGraficaService, times(1)).criarContaGrafica(clienteSalvo);
        verify(clienteMapper, times(1)).mapearParaAdesaoResponseDTO(clienteSalvo, contaGrafica);
    }

    @Test
    void deveMapearDTOCorretamente() {
        // Arrange
        when(clienteMapper.mapearParaCliente(any(AdesaoRequestDTO.class))).thenReturn(cliente);
        doNothing().when(clienteValidator).validarCpf(any(Cliente.class));
        when(clienteRepository.save(any(Cliente.class))).thenReturn(clienteSalvo);
        when(contaGraficaService.criarContaGrafica(any(Cliente.class))).thenReturn(contaGrafica);
        when(clienteMapper.mapearParaAdesaoResponseDTO(any(Cliente.class), any(ContaGrafica.class)))
                .thenReturn(adesaoResponseDTO);

        // Act
        clienteService.aderirAoProduto(adesaoRequestDTO);

        // Assert
        verify(clienteMapper, times(1)).mapearParaCliente(adesaoRequestDTO);
    }

    @Test
    void deveValidarClienteAntesDeAderir() {
        // Arrange
        when(clienteMapper.mapearParaCliente(any(AdesaoRequestDTO.class))).thenReturn(cliente);
        doNothing().when(clienteValidator).validarCpf(any(Cliente.class));
        when(clienteRepository.save(any(Cliente.class))).thenReturn(clienteSalvo);
        when(contaGraficaService.criarContaGrafica(any(Cliente.class))).thenReturn(contaGrafica);
        when(clienteMapper.mapearParaAdesaoResponseDTO(any(Cliente.class), any(ContaGrafica.class)))
                .thenReturn(adesaoResponseDTO);

        // Act
        clienteService.aderirAoProduto(adesaoRequestDTO);

        // Assert
        verify(clienteValidator, times(1)).validarCpf(cliente);
    }

    @Test
    void deveSalvarClienteNoRepositorio() {
        // Arrange
        when(clienteMapper.mapearParaCliente(any(AdesaoRequestDTO.class))).thenReturn(cliente);
        doNothing().when(clienteValidator).validarCpf(any(Cliente.class));
        when(clienteRepository.save(any(Cliente.class))).thenReturn(clienteSalvo);
        when(contaGraficaService.criarContaGrafica(any(Cliente.class))).thenReturn(contaGrafica);
        when(clienteMapper.mapearParaAdesaoResponseDTO(any(Cliente.class), any(ContaGrafica.class)))
                .thenReturn(adesaoResponseDTO);

        // Act
        clienteService.aderirAoProduto(adesaoRequestDTO);

        // Assert
        verify(clienteRepository, times(1)).save(cliente);
    }

    @Test
    void deveCriarContaGraficaParaCliente() {
        // Arrange
        when(clienteMapper.mapearParaCliente(any(AdesaoRequestDTO.class))).thenReturn(cliente);
        doNothing().when(clienteValidator).validarCpf(any(Cliente.class));
        when(clienteRepository.save(any(Cliente.class))).thenReturn(clienteSalvo);
        when(contaGraficaService.criarContaGrafica(any(Cliente.class))).thenReturn(contaGrafica);
        when(clienteMapper.mapearParaAdesaoResponseDTO(any(Cliente.class), any(ContaGrafica.class)))
                .thenReturn(adesaoResponseDTO);

        // Act
        clienteService.aderirAoProduto(adesaoRequestDTO);

        // Assert
        verify(contaGraficaService, times(1)).criarContaGrafica(clienteSalvo);
    }

    @Test
    void deveRetornarResponseDTOComDadosCorretos() {
        // Arrange
        when(clienteMapper.mapearParaCliente(any(AdesaoRequestDTO.class))).thenReturn(cliente);
        doNothing().when(clienteValidator).validarCpf(any(Cliente.class));
        when(clienteRepository.save(any(Cliente.class))).thenReturn(clienteSalvo);
        when(contaGraficaService.criarContaGrafica(any(Cliente.class))).thenReturn(contaGrafica);
        when(clienteMapper.mapearParaAdesaoResponseDTO(any(Cliente.class), any(ContaGrafica.class)))
                .thenReturn(adesaoResponseDTO);

        // Act
        AdesaoResponseDTO resultado = clienteService.aderirAoProduto(adesaoRequestDTO);

        // Assert
        verify(clienteMapper, times(1)).mapearParaAdesaoResponseDTO(clienteSalvo, contaGrafica);
        assertNotNull(resultado);
    }
}

