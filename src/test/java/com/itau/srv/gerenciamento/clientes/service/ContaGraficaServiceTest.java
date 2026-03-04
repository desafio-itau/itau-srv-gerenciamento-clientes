package com.itau.srv.gerenciamento.clientes.service;

import com.itau.srv.gerenciamento.clientes.dto.contagrafica.ContaGraficaResponseDTO;
import com.itau.srv.gerenciamento.clientes.mapper.ContaGraficaMapper;
import com.itau.srv.gerenciamento.clientes.model.Cliente;
import com.itau.srv.gerenciamento.clientes.model.ContaGrafica;
import com.itau.srv.gerenciamento.clientes.model.enums.TipoConta;
import com.itau.srv.gerenciamento.clientes.repository.ContaGraficaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ContaGraficaServiceTest {

    @Mock
    private ContaGraficaRepository contaGraficaRepository;

    @Mock
    private ContaGraficaMapper contaGraficaMapper;

    @InjectMocks
    private ContaGraficaService contaGraficaService;

    private Cliente cliente;
    private ContaGrafica contaGraficaSalva;

    @BeforeEach
    void setUp() {
        cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNome("João Silva");
        cliente.setCpf("12345678901");
        cliente.setEmail("joao@email.com");
        cliente.setValorMensal(new BigDecimal("150.00"));
        cliente.setAtivo(true);

        contaGraficaSalva = new ContaGrafica();
        contaGraficaSalva.setId(1L);
        contaGraficaSalva.setCliente(cliente);
        contaGraficaSalva.setNumeroConta("ITAUFL00001");
        contaGraficaSalva.setTipo(TipoConta.FILHOTE);
    }

    @Test
    void deveCriarContaGraficaComSucesso() {
        // Arrange
        when(contaGraficaRepository.save(any(ContaGrafica.class))).thenReturn(contaGraficaSalva);

        // Act
        ContaGrafica resultado = contaGraficaService.criarContaGrafica(cliente);

        // Assert
        assertNotNull(resultado);
        assertEquals(contaGraficaSalva.getId(), resultado.getId());
        assertEquals(contaGraficaSalva.getNumeroConta(), resultado.getNumeroConta());
        assertEquals(TipoConta.FILHOTE, resultado.getTipo());
        verify(contaGraficaRepository, times(1)).save(any(ContaGrafica.class));
    }

    @Test
    void deveGerarNumeroContaComFormatoCorreto() {
        // Arrange
        when(contaGraficaRepository.save(any(ContaGrafica.class))).thenReturn(contaGraficaSalva);

        // Act
        ContaGrafica resultado = contaGraficaService.criarContaGrafica(cliente);

        // Assert
        assertNotNull(resultado.getNumeroConta());
        assertTrue(resultado.getNumeroConta().startsWith("ITAUFL"));
        assertTrue(resultado.getNumeroConta().matches("ITAUFL\\d{5}"));
    }

    @Test
    void deveGerarNumeroContaComBaseNoIdDoCliente() {
        // Arrange
        when(contaGraficaRepository.save(any(ContaGrafica.class))).thenReturn(contaGraficaSalva);
        ArgumentCaptor<ContaGrafica> captor = ArgumentCaptor.forClass(ContaGrafica.class);

        // Act
        contaGraficaService.criarContaGrafica(cliente);

        // Assert
        verify(contaGraficaRepository).save(captor.capture());
        ContaGrafica contaCapturada = captor.getValue();
        assertEquals("ITAUFL00001", contaCapturada.getNumeroConta());
    }

    @Test
    void deveDefinirTipoComoFilhote() {
        // Arrange
        when(contaGraficaRepository.save(any(ContaGrafica.class))).thenReturn(contaGraficaSalva);
        ArgumentCaptor<ContaGrafica> captor = ArgumentCaptor.forClass(ContaGrafica.class);

        // Act
        contaGraficaService.criarContaGrafica(cliente);

        // Assert
        verify(contaGraficaRepository).save(captor.capture());
        assertEquals(TipoConta.FILHOTE, captor.getValue().getTipo());
    }

    @Test
    void deveAssociarClienteAContaGrafica() {
        // Arrange
        when(contaGraficaRepository.save(any(ContaGrafica.class))).thenReturn(contaGraficaSalva);
        ArgumentCaptor<ContaGrafica> captor = ArgumentCaptor.forClass(ContaGrafica.class);

        // Act
        contaGraficaService.criarContaGrafica(cliente);

        // Assert
        verify(contaGraficaRepository).save(captor.capture());
        ContaGrafica contaCapturada = captor.getValue();
        assertNotNull(contaCapturada.getCliente());
        assertEquals(cliente.getId(), contaCapturada.getCliente().getId());
        assertEquals(cliente.getNome(), contaCapturada.getCliente().getNome());
    }

    @Test
    void deveGerarNumerosContaDiferentesParaClientesDiferentes() {
        // Arrange
        Cliente cliente2 = new Cliente();
        cliente2.setId(10L);
        cliente2.setNome("Maria Santos");
        cliente2.setCpf("98765432100");

        ContaGrafica contaGrafica2 = new ContaGrafica();
        contaGrafica2.setId(10L);
        contaGrafica2.setCliente(cliente2);
        contaGrafica2.setNumeroConta("ITAUFL00010");
        contaGrafica2.setTipo(TipoConta.FILHOTE);

        when(contaGraficaRepository.save(any(ContaGrafica.class)))
                .thenReturn(contaGraficaSalva)
                .thenReturn(contaGrafica2);

        // Act
        ContaGrafica conta1 = contaGraficaService.criarContaGrafica(cliente);
        ContaGrafica conta2 = contaGraficaService.criarContaGrafica(cliente2);

        // Assert
        assertNotEquals(conta1.getNumeroConta(), conta2.getNumeroConta());
        assertEquals("ITAUFL00001", conta1.getNumeroConta());
        assertEquals("ITAUFL00010", conta2.getNumeroConta());
    }

    @Test
    void deveGerarNumeroContaComPaddingDeZeros() {
        // Arrange
        Cliente cliente100 = new Cliente();
        cliente100.setId(100L);

        ContaGrafica contaGrafica100 = new ContaGrafica();
        contaGrafica100.setId(100L);
        contaGrafica100.setNumeroConta("ITAUFL00100");

        when(contaGraficaRepository.save(any(ContaGrafica.class))).thenReturn(contaGrafica100);
        ArgumentCaptor<ContaGrafica> captor = ArgumentCaptor.forClass(ContaGrafica.class);

        // Act
        contaGraficaService.criarContaGrafica(cliente100);

        // Assert
        verify(contaGraficaRepository).save(captor.capture());
        assertEquals("ITAUFL00100", captor.getValue().getNumeroConta());
    }

    @Test
    void deveChamarRepositorioParaSalvar() {
        // Arrange
        when(contaGraficaRepository.save(any(ContaGrafica.class))).thenReturn(contaGraficaSalva);

        // Act
        contaGraficaService.criarContaGrafica(cliente);

        // Assert
        verify(contaGraficaRepository, times(1)).save(any(ContaGrafica.class));
    }

    // =========== TESTES PARA BUSCAR CONTA GRÁFICA ===========

    @Test
    void deveBuscarContaGraficaComSucesso() {
        // Arrange
        Long contaId = 1L;
        ContaGraficaResponseDTO responseDTO = new ContaGraficaResponseDTO(
                1L,
                "ITAUFL00001",
                TipoConta.FILHOTE,
                null
        );

        when(contaGraficaRepository.findById(contaId)).thenReturn(java.util.Optional.of(contaGraficaSalva));
        when(contaGraficaMapper.mapearParaContaGraficaResponseDTO(contaGraficaSalva)).thenReturn(responseDTO);

        // Act
        ContaGraficaResponseDTO resultado = contaGraficaService.buscarConta(contaId);

        // Assert
        assertNotNull(resultado);
        assertEquals(1L, resultado.id());
        assertEquals("ITAUFL00001", resultado.numeroConta());
        assertEquals(TipoConta.FILHOTE, resultado.tipoConta());
        verify(contaGraficaRepository).findById(contaId);
        verify(contaGraficaMapper).mapearParaContaGraficaResponseDTO(contaGraficaSalva);
    }

    @Test
    void deveLancarExcecaoQuandoContaNaoEncontrada() {
        // Arrange
        Long contaId = 999L;
        when(contaGraficaRepository.findById(contaId)).thenReturn(java.util.Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            contaGraficaService.buscarConta(contaId);
        });

        assertEquals("Conta grafica não encontrada", exception.getMessage());
        verify(contaGraficaRepository).findById(contaId);
        verify(contaGraficaMapper, never()).mapearParaContaGraficaResponseDTO(any());
    }

    @Test
    void deveChamarRepositoryComIdCorreto() {
        // Arrange
        Long contaId = 5L;
        ContaGrafica conta = new ContaGrafica();
        conta.setId(contaId);
        conta.setNumeroConta("ITAUFL00005");

        ContaGraficaResponseDTO responseDTO = new ContaGraficaResponseDTO(
                contaId,
                "ITAUFL00005",
                TipoConta.FILHOTE,
                null
        );

        when(contaGraficaRepository.findById(contaId)).thenReturn(java.util.Optional.of(conta));
        when(contaGraficaMapper.mapearParaContaGraficaResponseDTO(conta)).thenReturn(responseDTO);

        // Act
        contaGraficaService.buscarConta(contaId);

        // Assert
        verify(contaGraficaRepository, times(1)).findById(5L);
    }

    @Test
    void deveChamarMapperParaTransformarEmDTO() {
        // Arrange
        Long contaId = 1L;
        ContaGraficaResponseDTO responseDTO = new ContaGraficaResponseDTO(
                1L,
                "ITAUFL00001",
                TipoConta.FILHOTE,
                null
        );

        when(contaGraficaRepository.findById(contaId)).thenReturn(java.util.Optional.of(contaGraficaSalva));
        when(contaGraficaMapper.mapearParaContaGraficaResponseDTO(contaGraficaSalva)).thenReturn(responseDTO);

        // Act
        contaGraficaService.buscarConta(contaId);

        // Assert
        verify(contaGraficaMapper, times(1)).mapearParaContaGraficaResponseDTO(contaGraficaSalva);
    }

    @Test
    void deveRetornarDadosCorretosDaConta() {
        // Arrange
        Long contaId = 10L;
        ContaGrafica conta = new ContaGrafica();
        conta.setId(10L);
        conta.setNumeroConta("ITAUFL00010");
        conta.setTipo(TipoConta.MASTER);

        ContaGraficaResponseDTO responseDTO = new ContaGraficaResponseDTO(
                10L,
                "ITAUFL00010",
                TipoConta.MASTER,
                null
        );

        when(contaGraficaRepository.findById(contaId)).thenReturn(java.util.Optional.of(conta));
        when(contaGraficaMapper.mapearParaContaGraficaResponseDTO(conta)).thenReturn(responseDTO);

        // Act
        ContaGraficaResponseDTO resultado = contaGraficaService.buscarConta(contaId);

        // Assert
        assertNotNull(resultado);
        assertEquals(10L, resultado.id());
        assertEquals("ITAUFL00010", resultado.numeroConta());
        assertEquals(TipoConta.MASTER, resultado.tipoConta());
    }

    @Test
    void deveBuscarContaTipoFilhote() {
        // Arrange
        Long contaId = 1L;
        ContaGraficaResponseDTO responseDTO = new ContaGraficaResponseDTO(
                1L,
                "ITAUFL00001",
                TipoConta.FILHOTE,
                null
        );

        when(contaGraficaRepository.findById(contaId)).thenReturn(java.util.Optional.of(contaGraficaSalva));
        when(contaGraficaMapper.mapearParaContaGraficaResponseDTO(contaGraficaSalva)).thenReturn(responseDTO);

        // Act
        ContaGraficaResponseDTO resultado = contaGraficaService.buscarConta(contaId);

        // Assert
        assertEquals(TipoConta.FILHOTE, resultado.tipoConta());
    }

    @Test
    void deveBuscarContaTipoMaster() {
        // Arrange
        Long contaId = 1L;
        ContaGrafica contaMaster = new ContaGrafica();
        contaMaster.setId(1L);
        contaMaster.setNumeroConta("ITAUFL00001");
        contaMaster.setTipo(TipoConta.MASTER);

        ContaGraficaResponseDTO responseDTO = new ContaGraficaResponseDTO(
                1L,
                "ITAUFL00001",
                TipoConta.MASTER,
                null
        );

        when(contaGraficaRepository.findById(contaId)).thenReturn(java.util.Optional.of(contaMaster));
        when(contaGraficaMapper.mapearParaContaGraficaResponseDTO(contaMaster)).thenReturn(responseDTO);

        // Act
        ContaGraficaResponseDTO resultado = contaGraficaService.buscarConta(contaId);

        // Assert
        assertEquals(TipoConta.MASTER, resultado.tipoConta());
    }

    @Test
    void deveRetornarNumeroDaContaFormatadoCorretamente() {
        // Arrange
        Long contaId = 1L;
        ContaGraficaResponseDTO responseDTO = new ContaGraficaResponseDTO(
                1L,
                "ITAUFL00001",
                TipoConta.FILHOTE,
                null
        );

        when(contaGraficaRepository.findById(contaId)).thenReturn(java.util.Optional.of(contaGraficaSalva));
        when(contaGraficaMapper.mapearParaContaGraficaResponseDTO(contaGraficaSalva)).thenReturn(responseDTO);

        // Act
        ContaGraficaResponseDTO resultado = contaGraficaService.buscarConta(contaId);

        // Assert
        assertTrue(resultado.numeroConta().matches("ITAUFL\\d{5}"));
    }

    @Test
    void deveLancarExcecaoComMensagemCorreta() {
        // Arrange
        Long contaId = 999L;
        when(contaGraficaRepository.findById(contaId)).thenReturn(java.util.Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            contaGraficaService.buscarConta(contaId);
        });

        assertEquals("Conta grafica não encontrada", exception.getMessage());
    }

    @Test
    void deveNaoChamarMapperQuandoContaNaoEncontrada() {
        // Arrange
        Long contaId = 999L;
        when(contaGraficaRepository.findById(contaId)).thenReturn(java.util.Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            contaGraficaService.buscarConta(contaId);
        });

        verify(contaGraficaMapper, never()).mapearParaContaGraficaResponseDTO(any(ContaGrafica.class));
    }
}
