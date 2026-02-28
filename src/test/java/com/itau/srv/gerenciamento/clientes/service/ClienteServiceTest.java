package com.itau.srv.gerenciamento.clientes.service;

import com.itau.common.library.exception.NegocioException;
import com.itau.common.library.exception.RecursoNaoEncontradoException;
import com.itau.srv.gerenciamento.clientes.dto.adesao.AdesaoCancelamentoResponseDTO;
import com.itau.srv.gerenciamento.clientes.dto.adesao.AdesaoRequestDTO;
import com.itau.srv.gerenciamento.clientes.dto.adesao.AdesaoResponseDTO;
import com.itau.srv.gerenciamento.clientes.dto.valormensal.AlterarValorMensalRequestDTO;
import com.itau.srv.gerenciamento.clientes.dto.valormensal.AlterarValorMensalResponseDTO;
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

    // Testes de Cancelamento de Adesão

    @Test
    void deveCancelarAdesaoComSucesso() {
        // Arrange
        when(clienteRepository.findByIdAndAtivo(1L)).thenReturn(java.util.Optional.of(clienteSalvo));
        when(clienteRepository.save(any(Cliente.class))).thenReturn(clienteSalvo);

        // Act
        AdesaoCancelamentoResponseDTO resultado = clienteService.cancelarAdesao(1L);

        // Assert
        assertNotNull(resultado);
        assertEquals(1L, resultado.clienteId());
        assertEquals("João Silva", resultado.nome());
        assertFalse(resultado.ativo());
        assertNotNull(resultado.dataSaida());
        assertEquals("Adesão encerrada. Sua posição em custodia foi mantida.", resultado.mensagem());
        verify(clienteRepository, times(1)).findByIdAndAtivo(1L);
        verify(clienteRepository, times(1)).save(clienteSalvo);
    }

    @Test
    void deveDefinirClienteComoInativoAoCancelar() {
        // Arrange
        when(clienteRepository.findByIdAndAtivo(1L)).thenReturn(java.util.Optional.of(clienteSalvo));
        when(clienteRepository.save(any(Cliente.class))).thenAnswer(invocation -> {
            Cliente c = invocation.getArgument(0);
            assertFalse(c.getAtivo());
            return c;
        });

        // Act
        AdesaoCancelamentoResponseDTO resultado = clienteService.cancelarAdesao(1L);

        // Assert
        assertFalse(resultado.ativo());
        verify(clienteRepository, times(1)).save(clienteSalvo);
    }

    @Test
    void deveLancarExcecaoAoCancelarClienteInexistente() {
        // Arrange
        when(clienteRepository.findByIdAndAtivo(99999L)).thenReturn(java.util.Optional.empty());

        // Act & Assert
        RecursoNaoEncontradoException exception = assertThrows(
                RecursoNaoEncontradoException.class,
                () -> clienteService.cancelarAdesao(99999L)
        );

        assertEquals("CLIENTE_NAO_ENCONTRADO", exception.getMessage());
        verify(clienteRepository, times(1)).findByIdAndAtivo(99999L);
        verify(clienteRepository, never()).save(any(Cliente.class));
    }

    @Test
    void deveLancarExcecaoAoCancelarClienteJaInativo() {
        // Arrange
        when(clienteRepository.findByIdAndAtivo(1L)).thenReturn(java.util.Optional.empty());

        // Act & Assert
        RecursoNaoEncontradoException exception = assertThrows(
                RecursoNaoEncontradoException.class,
                () -> clienteService.cancelarAdesao(1L)
        );

        assertEquals("CLIENTE_NAO_ENCONTRADO", exception.getMessage());
    }

    @Test
    void deveRetornarDataCancelamentoCorreta() {
        // Arrange
        when(clienteRepository.findByIdAndAtivo(1L)).thenReturn(java.util.Optional.of(clienteSalvo));
        when(clienteRepository.save(any(Cliente.class))).thenReturn(clienteSalvo);

        // Act
        AdesaoCancelamentoResponseDTO resultado = clienteService.cancelarAdesao(1L);

        // Assert
        assertNotNull(resultado.dataSaida());
    }

    @Test
    void deveRetornarMensagemPadraoDeCancelamento() {
        // Arrange
        when(clienteRepository.findByIdAndAtivo(1L)).thenReturn(java.util.Optional.of(clienteSalvo));
        when(clienteRepository.save(any(Cliente.class))).thenReturn(clienteSalvo);

        // Act
        AdesaoCancelamentoResponseDTO resultado = clienteService.cancelarAdesao(1L);

        // Assert
        assertNotNull(resultado.mensagem());
        assertEquals("Adesão encerrada. Sua posição em custodia foi mantida.", resultado.mensagem());
    }

    @Test
    void deveSalvarClienteInativoNoBancoDeDados() {
        // Arrange
        when(clienteRepository.findByIdAndAtivo(1L)).thenReturn(java.util.Optional.of(clienteSalvo));
        when(clienteRepository.save(any(Cliente.class))).thenReturn(clienteSalvo);

        // Act
        clienteService.cancelarAdesao(1L);

        // Assert
        verify(clienteRepository, times(1)).save(clienteSalvo);
    }

    @Test
    void deveChamarFindByIdAndAtivoComIdCorreto() {
        // Arrange
        Long clienteId = 5L;
        Cliente clienteTeste = new Cliente();
        clienteTeste.setId(clienteId);
        clienteTeste.setNome("Teste");
        clienteTeste.setAtivo(true);

        when(clienteRepository.findByIdAndAtivo(clienteId)).thenReturn(java.util.Optional.of(clienteTeste));
        when(clienteRepository.save(any(Cliente.class))).thenReturn(clienteTeste);

        // Act
        clienteService.cancelarAdesao(clienteId);

        // Assert
        verify(clienteRepository, times(1)).findByIdAndAtivo(clienteId);
    }

    // Testes de Alteração de Valor Mensal

    @Test
    void deveAlterarValorMensalComSucesso() {
        // Arrange
        AlterarValorMensalRequestDTO requestDTO = new AlterarValorMensalRequestDTO(new BigDecimal("200.00"));
        when(clienteRepository.findByIdAndAtivo(1L)).thenReturn(java.util.Optional.of(clienteSalvo));
        when(clienteRepository.save(any(Cliente.class))).thenReturn(clienteSalvo);

        // Act
        AlterarValorMensalResponseDTO resultado = clienteService.alterarValorMensal(1L, requestDTO);

        // Assert
        assertNotNull(resultado);
        assertEquals(1L, resultado.clienteId());
        assertEquals(new BigDecimal("150.00"), resultado.valorMensalAnterior());
        assertEquals(new BigDecimal("200.00"), resultado.valorMensalNovo());
        assertNotNull(resultado.dataAlteracao());
        assertEquals("Valor mensal atualizado. O novo valor será considerado a partir da próxima data de compra.", resultado.mensagem());
        verify(clienteRepository, times(1)).findByIdAndAtivo(1L);
        verify(clienteRepository, times(1)).save(clienteSalvo);
    }

    @Test
    void deveAtualizarValorMensalDoCliente() {
        // Arrange
        AlterarValorMensalRequestDTO requestDTO = new AlterarValorMensalRequestDTO(new BigDecimal("300.00"));
        when(clienteRepository.findByIdAndAtivo(1L)).thenReturn(java.util.Optional.of(clienteSalvo));
        when(clienteRepository.save(any(Cliente.class))).thenAnswer(invocation -> {
            Cliente c = invocation.getArgument(0);
            assertEquals(new BigDecimal("300.00"), c.getValorMensal());
            return c;
        });

        // Act
        clienteService.alterarValorMensal(1L, requestDTO);

        // Assert
        verify(clienteRepository, times(1)).save(clienteSalvo);
    }

    @Test
    void deveLancarExcecaoAoAlterarValorMensalDeClienteInexistente() {
        // Arrange
        AlterarValorMensalRequestDTO requestDTO = new AlterarValorMensalRequestDTO(new BigDecimal("200.00"));
        when(clienteRepository.findByIdAndAtivo(99999L)).thenReturn(java.util.Optional.empty());

        // Act & Assert
        RecursoNaoEncontradoException exception = assertThrows(
                RecursoNaoEncontradoException.class,
                () -> clienteService.alterarValorMensal(99999L, requestDTO)
        );

        assertEquals("CLIENTE_NAO_ENCONTRADO", exception.getMessage());
        verify(clienteRepository, times(1)).findByIdAndAtivo(99999L);
        verify(clienteRepository, never()).save(any(Cliente.class));
    }

    @Test
    void deveLancarExcecaoQuandoValorMensalMenorOuIgualA100() {
        // Arrange
        AlterarValorMensalRequestDTO requestDTO = new AlterarValorMensalRequestDTO(new BigDecimal("100.00"));

        // Act & Assert
        NegocioException exception = assertThrows(
                NegocioException.class,
                () -> clienteService.alterarValorMensal(1L, requestDTO)
        );

        assertEquals("VALOR_MENSAL_INVALIDO", exception.getMessage());
        verify(clienteRepository, never()).findByIdAndAtivo(any());
        verify(clienteRepository, never()).save(any(Cliente.class));
    }

    @Test
    void deveLancarExcecaoQuandoValorMensalIgual50() {
        // Arrange
        AlterarValorMensalRequestDTO requestDTO = new AlterarValorMensalRequestDTO(new BigDecimal("50.00"));

        // Act & Assert
        NegocioException exception = assertThrows(
                NegocioException.class,
                () -> clienteService.alterarValorMensal(1L, requestDTO)
        );

        assertEquals("VALOR_MENSAL_INVALIDO", exception.getMessage());
    }

    @Test
    void deveRetornarValorAntigoCorreto() {
        // Arrange
        AlterarValorMensalRequestDTO requestDTO = new AlterarValorMensalRequestDTO(new BigDecimal("250.00"));
        when(clienteRepository.findByIdAndAtivo(1L)).thenReturn(java.util.Optional.of(clienteSalvo));
        when(clienteRepository.save(any(Cliente.class))).thenReturn(clienteSalvo);

        // Act
        AlterarValorMensalResponseDTO resultado = clienteService.alterarValorMensal(1L, requestDTO);

        // Assert
        assertEquals(new BigDecimal("150.00"), resultado.valorMensalAnterior());
    }

    @Test
    void deveRetornarValorNovoCorreto() {
        // Arrange
        AlterarValorMensalRequestDTO requestDTO = new AlterarValorMensalRequestDTO(new BigDecimal("350.00"));
        when(clienteRepository.findByIdAndAtivo(1L)).thenReturn(java.util.Optional.of(clienteSalvo));
        when(clienteRepository.save(any(Cliente.class))).thenReturn(clienteSalvo);

        // Act
        AlterarValorMensalResponseDTO resultado = clienteService.alterarValorMensal(1L, requestDTO);

        // Assert
        assertEquals(new BigDecimal("350.00"), resultado.valorMensalNovo());
    }

    @Test
    void deveRetornarMensagemPadraoDeAlteracao() {
        // Arrange
        AlterarValorMensalRequestDTO requestDTO = new AlterarValorMensalRequestDTO(new BigDecimal("180.00"));
        when(clienteRepository.findByIdAndAtivo(1L)).thenReturn(java.util.Optional.of(clienteSalvo));
        when(clienteRepository.save(any(Cliente.class))).thenReturn(clienteSalvo);

        // Act
        AlterarValorMensalResponseDTO resultado = clienteService.alterarValorMensal(1L, requestDTO);

        // Assert
        assertNotNull(resultado.mensagem());
        assertEquals("Valor mensal atualizado. O novo valor será considerado a partir da próxima data de compra.", resultado.mensagem());
    }

    @Test
    void deveRetornarDataAtualizacaoCorreta() {
        // Arrange
        AlterarValorMensalRequestDTO requestDTO = new AlterarValorMensalRequestDTO(new BigDecimal("220.00"));
        when(clienteRepository.findByIdAndAtivo(1L)).thenReturn(java.util.Optional.of(clienteSalvo));
        when(clienteRepository.save(any(Cliente.class))).thenReturn(clienteSalvo);

        // Act
        AlterarValorMensalResponseDTO resultado = clienteService.alterarValorMensal(1L, requestDTO);

        // Assert
        assertNotNull(resultado.dataAlteracao());
    }

    @Test
    void deveSalvarClienteComNovoValorMensal() {
        // Arrange
        AlterarValorMensalRequestDTO requestDTO = new AlterarValorMensalRequestDTO(new BigDecimal("400.00"));
        when(clienteRepository.findByIdAndAtivo(1L)).thenReturn(java.util.Optional.of(clienteSalvo));
        when(clienteRepository.save(any(Cliente.class))).thenReturn(clienteSalvo);

        // Act
        clienteService.alterarValorMensal(1L, requestDTO);

        // Assert
        verify(clienteRepository, times(1)).save(clienteSalvo);
    }

    @Test
    void deveChamarFindByIdAndAtivoComIdCorretoParaAlteracao() {
        // Arrange
        Long clienteId = 7L;
        Cliente clienteTeste = new Cliente();
        clienteTeste.setId(clienteId);
        clienteTeste.setNome("Teste");
        clienteTeste.setValorMensal(new BigDecimal("150.00"));
        clienteTeste.setAtivo(true);

        AlterarValorMensalRequestDTO requestDTO = new AlterarValorMensalRequestDTO(new BigDecimal("200.00"));
        when(clienteRepository.findByIdAndAtivo(clienteId)).thenReturn(java.util.Optional.of(clienteTeste));
        when(clienteRepository.save(any(Cliente.class))).thenReturn(clienteTeste);

        // Act
        clienteService.alterarValorMensal(clienteId, requestDTO);

        // Assert
        verify(clienteRepository, times(1)).findByIdAndAtivo(clienteId);
    }
}
