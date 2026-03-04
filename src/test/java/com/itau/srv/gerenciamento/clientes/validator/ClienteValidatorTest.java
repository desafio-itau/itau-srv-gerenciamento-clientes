package com.itau.srv.gerenciamento.clientes.validator;

import com.itau.common.library.exception.ConflitoException;
import com.itau.srv.gerenciamento.clientes.model.Cliente;
import com.itau.srv.gerenciamento.clientes.repository.ClienteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClienteValidatorTest {

    @Mock
    private ClienteRepository clienteRepository;

    @InjectMocks
    private ClienteValidator clienteValidator;

    private Cliente cliente;
    private Cliente clienteExistente;

    @BeforeEach
    void setUp() {
        cliente = new Cliente();
        cliente.setId(null);
        cliente.setNome("João Silva");
        cliente.setCpf("12345678901");
        cliente.setEmail("joao@email.com");
        cliente.setValorMensal(new BigDecimal("150.00"));

        clienteExistente = new Cliente();
        clienteExistente.setId(1L);
        clienteExistente.setNome("Maria Santos");
        clienteExistente.setCpf("12345678901");
        clienteExistente.setEmail("maria@email.com");
        clienteExistente.setValorMensal(new BigDecimal("200.00"));
        clienteExistente.setAtivo(true);
    }

    @Test
    void deveValidarComSucessoQuandoCpfNaoExiste() {
        // Arrange
        when(clienteRepository.findByCpfAndAtivo(anyString())).thenReturn(Optional.empty());

        // Act & Assert
        assertDoesNotThrow(() -> clienteValidator.validarCpf(cliente));
        verify(clienteRepository, times(1)).findByCpfAndAtivo(cliente.getCpf());
    }

    @Test
    void deveLancarExcecaoQuandoCpfJaExisteParaNovoCliente() {
        // Arrange
        when(clienteRepository.findByCpfAndAtivo(anyString())).thenReturn(Optional.of(clienteExistente));

        // Act & Assert
        ConflitoException exception = assertThrows(
                ConflitoException.class,
                () -> clienteValidator.validarCpf(cliente)
        );

        assertEquals("CLIENTE_CPF_DUPLICADO", exception.getMessage());
        verify(clienteRepository, times(1)).findByCpfAndAtivo(cliente.getCpf());
    }

    @Test
    void deveValidarComSucessoQuandoMesmoClienteAtualizaSeusDados() {
        // Arrange
        cliente.setId(1L);
        when(clienteRepository.findByCpfAndAtivo(anyString())).thenReturn(Optional.of(clienteExistente));

        // Act & Assert
        assertDoesNotThrow(() -> clienteValidator.validarCpf(cliente));
        verify(clienteRepository, times(1)).findByCpfAndAtivo(cliente.getCpf());
    }

    @Test
    void deveLancarExcecaoQuandoClienteDiferenteTentaUsarCpfExistente() {
        // Arrange
        cliente.setId(2L);
        when(clienteRepository.findByCpfAndAtivo(anyString())).thenReturn(Optional.of(clienteExistente));

        // Act & Assert
        ConflitoException exception = assertThrows(
                ConflitoException.class,
                () -> clienteValidator.validarCpf(cliente)
        );

        assertEquals("CLIENTE_CPF_DUPLICADO", exception.getMessage());
        verify(clienteRepository, times(1)).findByCpfAndAtivo(cliente.getCpf());
    }

    @Test
    void deveValidarComSucessoQuandoCpfDiferenteDoExistente() {
        // Arrange
        cliente.setCpf("98765432100");
        when(clienteRepository.findByCpfAndAtivo(anyString())).thenReturn(Optional.empty());

        // Act & Assert
        assertDoesNotThrow(() -> clienteValidator.validarCpf(cliente));
        verify(clienteRepository, times(1)).findByCpfAndAtivo(cliente.getCpf());
    }

    @Test
    void deveValidarComSucessoQuandoClienteNulo() {
        // Arrange
        cliente.setId(null);
        when(clienteRepository.findByCpfAndAtivo(anyString())).thenReturn(Optional.empty());

        // Act & Assert
        assertDoesNotThrow(() -> clienteValidator.validarCpf(cliente));
        verify(clienteRepository, times(1)).findByCpfAndAtivo(cliente.getCpf());
    }
}

