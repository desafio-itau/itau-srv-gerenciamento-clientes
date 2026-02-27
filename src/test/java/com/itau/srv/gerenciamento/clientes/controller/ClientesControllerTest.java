package com.itau.srv.gerenciamento.clientes.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itau.srv.gerenciamento.clientes.dto.adesao.AdesaoCancelamentoResponseDTO;
import com.itau.srv.gerenciamento.clientes.dto.adesao.AdesaoRequestDTO;
import com.itau.srv.gerenciamento.clientes.dto.adesao.AdesaoResponseDTO;
import com.itau.srv.gerenciamento.clientes.dto.contagrafica.ContaGraficaResponseDTO;
import com.itau.srv.gerenciamento.clientes.model.enums.TipoConta;
import com.itau.srv.gerenciamento.clientes.service.ClienteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ClientesControllerTest {

    @Mock
    private ClienteService clienteService;

    @InjectMocks
    private ClientesController clientesController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private AdesaoRequestDTO adesaoRequestDTO;
    private AdesaoResponseDTO adesaoResponseDTO;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(clientesController).build();
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules(); // Registra módulos para suportar LocalDateTime

        adesaoRequestDTO = new AdesaoRequestDTO(
                "João Silva",
                "52998224725", // CPF válido
                "joao@email.com",
                new BigDecimal("150.00")
        );

        ContaGraficaResponseDTO contaGraficaResponseDTO = new ContaGraficaResponseDTO(
                1L,
                "ITAUFL00001",
                TipoConta.FILHOTE,
                LocalDateTime.now()
        );

        adesaoResponseDTO = new AdesaoResponseDTO(
                1L,
                "João Silva",
                "52998224725", // CPF válido
                "joao@email.com",
                new BigDecimal("150.00"),
                true,
                LocalDateTime.now(),
                contaGraficaResponseDTO
        );
    }

    @Test
    void deveAderirAoProdutoComSucesso() throws Exception {
        // Arrange
        when(clienteService.aderirAoProduto(any(AdesaoRequestDTO.class))).thenReturn(adesaoResponseDTO);

        // Act & Assert
        mockMvc.perform(post("/api/clientes/adesao")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(adesaoRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.clienteId").value(1))
                .andExpect(jsonPath("$.nome").value("João Silva"))
                .andExpect(jsonPath("$.cpf").value("52998224725"))
                .andExpect(jsonPath("$.email").value("joao@email.com"))
                .andExpect(jsonPath("$.valorMensal").value(150.00))
                .andExpect(jsonPath("$.ativo").value(true))
                .andExpect(jsonPath("$.contaGrafica").exists())
                .andExpect(jsonPath("$.contaGrafica.numeroConta").value("ITAUFL00001"));

        verify(clienteService, times(1)).aderirAoProduto(any(AdesaoRequestDTO.class));
    }

    @Test
    void deveRetornarStatus201QuandoAdesaoComSucesso() throws Exception {
        // Arrange
        when(clienteService.aderirAoProduto(any(AdesaoRequestDTO.class))).thenReturn(adesaoResponseDTO);

        // Act & Assert
        mockMvc.perform(post("/api/clientes/adesao")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(adesaoRequestDTO)))
                .andExpect(status().isCreated());
    }

    @Test
    void deveRetornarLocationHeaderComIdDoCliente() throws Exception {
        // Arrange
        when(clienteService.aderirAoProduto(any(AdesaoRequestDTO.class))).thenReturn(adesaoResponseDTO);

        // Act & Assert
        mockMvc.perform(post("/api/clientes/adesao")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(adesaoRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(header().string("Location", "http://localhost/api/clientes/adesao/1"));
    }

    @Test
    void deveRetornarDadosDoClienteNaResposta() throws Exception {
        // Arrange
        when(clienteService.aderirAoProduto(any(AdesaoRequestDTO.class))).thenReturn(adesaoResponseDTO);

        // Act & Assert
        mockMvc.perform(post("/api/clientes/adesao")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(adesaoRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.clienteId").exists())
                .andExpect(jsonPath("$.nome").exists())
                .andExpect(jsonPath("$.cpf").exists())
                .andExpect(jsonPath("$.email").exists())
                .andExpect(jsonPath("$.valorMensal").exists())
                .andExpect(jsonPath("$.ativo").exists())
                .andExpect(jsonPath("$.dataAdesao").exists());
    }

    @Test
    void deveRetornarDadosDaContaGraficaNaResposta() throws Exception {
        // Arrange
        when(clienteService.aderirAoProduto(any(AdesaoRequestDTO.class))).thenReturn(adesaoResponseDTO);

        // Act & Assert
        mockMvc.perform(post("/api/clientes/adesao")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(adesaoRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.contaGrafica").exists())
                .andExpect(jsonPath("$.contaGrafica.id").exists())
                .andExpect(jsonPath("$.contaGrafica.numeroConta").exists())
                .andExpect(jsonPath("$.contaGrafica.dataCriacao").exists());
    }

    @Test
    void deveChamarServiceParaAderir() throws Exception {
        // Arrange
        when(clienteService.aderirAoProduto(any(AdesaoRequestDTO.class))).thenReturn(adesaoResponseDTO);

        // Act
        mockMvc.perform(post("/api/clientes/adesao")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(adesaoRequestDTO)));

        // Assert
        verify(clienteService, times(1)).aderirAoProduto(any(AdesaoRequestDTO.class));
    }

    @Test
    void deveAceitarDadosValidosNoRequestBody() throws Exception {
        // Arrange
        when(clienteService.aderirAoProduto(any(AdesaoRequestDTO.class))).thenReturn(adesaoResponseDTO);

        String jsonRequest = """
                {
                    "nome": "João Silva",
                    "cpf": "52998224725",
                    "email": "joao@email.com",
                    "valorMensal": 150.00
                }
                """;

        // Act & Assert
        mockMvc.perform(post("/api/clientes/adesao")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isCreated());
    }

    // Testes de Cancelamento de Adesão

    @Test
    void deveCancelarAdesaoComSucesso() throws Exception {
        // Arrange
        Long clienteId = 1L;
        AdesaoCancelamentoResponseDTO cancelamentoResponse = new AdesaoCancelamentoResponseDTO(
                clienteId,
                "João Silva",
                false,
                LocalDateTime.now(),
                "Adesão encerrada. Sua posição em custodia foi mantida."
        );

        when(clienteService.cancelarAdesao(clienteId)).thenReturn(cancelamentoResponse);

        // Act & Assert
        mockMvc.perform(post("/api/clientes/" + clienteId + "/saida")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.clienteId").value(clienteId))
                .andExpect(jsonPath("$.nome").value("João Silva"))
                .andExpect(jsonPath("$.ativo").value(false))
                .andExpect(jsonPath("$.dataSaida").exists())
                .andExpect(jsonPath("$.mensagem").value("Adesão encerrada. Sua posição em custodia foi mantida."));

        verify(clienteService, times(1)).cancelarAdesao(clienteId);
    }

    @Test
    void deveRetornarStatus200QuandoCancelamentoComSucesso() throws Exception {
        // Arrange
        Long clienteId = 1L;
        AdesaoCancelamentoResponseDTO cancelamentoResponse = new AdesaoCancelamentoResponseDTO(
                clienteId,
                "João Silva",
                false,
                LocalDateTime.now(),
                "Adesão encerrada. Sua posição em custodia foi mantida."
        );

        when(clienteService.cancelarAdesao(clienteId)).thenReturn(cancelamentoResponse);

        // Act & Assert
        mockMvc.perform(post("/api/clientes/" + clienteId + "/saida")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void deveRetornarDadosDoCancelamentoNaResposta() throws Exception {
        // Arrange
        Long clienteId = 1L;
        AdesaoCancelamentoResponseDTO cancelamentoResponse = new AdesaoCancelamentoResponseDTO(
                clienteId,
                "João Silva",
                false,
                LocalDateTime.now(),
                "Adesão encerrada. Sua posição em custodia foi mantida."
        );

        when(clienteService.cancelarAdesao(clienteId)).thenReturn(cancelamentoResponse);

        // Act & Assert
        mockMvc.perform(post("/api/clientes/" + clienteId + "/saida")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.clienteId").exists())
                .andExpect(jsonPath("$.nome").exists())
                .andExpect(jsonPath("$.ativo").exists())
                .andExpect(jsonPath("$.dataSaida").exists())
                .andExpect(jsonPath("$.mensagem").exists());
    }

    @Test
    void deveRetornarAtivoFalsoAposCancelamento() throws Exception {
        // Arrange
        Long clienteId = 1L;
        AdesaoCancelamentoResponseDTO cancelamentoResponse = new AdesaoCancelamentoResponseDTO(
                clienteId,
                "João Silva",
                false,
                LocalDateTime.now(),
                "Adesão encerrada. Sua posição em custodia foi mantida."
        );

        when(clienteService.cancelarAdesao(clienteId)).thenReturn(cancelamentoResponse);

        // Act & Assert
        mockMvc.perform(post("/api/clientes/" + clienteId + "/saida")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ativo").value(false));
    }

    @Test
    void deveChamarServiceParaCancelar() throws Exception {
        // Arrange
        Long clienteId = 1L;
        AdesaoCancelamentoResponseDTO cancelamentoResponse = new AdesaoCancelamentoResponseDTO(
                clienteId,
                "João Silva",
                false,
                LocalDateTime.now(),
                "Adesão encerrada. Sua posição em custodia foi mantida."
        );

        when(clienteService.cancelarAdesao(clienteId)).thenReturn(cancelamentoResponse);

        // Act
        mockMvc.perform(post("/api/clientes/" + clienteId + "/saida")
                .contentType(MediaType.APPLICATION_JSON));

        // Assert
        verify(clienteService, times(1)).cancelarAdesao(clienteId);
    }

    @Test
    void deveUsarClienteIdDoPathVariable() throws Exception {
        // Arrange
        Long clienteId = 999L;
        AdesaoCancelamentoResponseDTO cancelamentoResponse = new AdesaoCancelamentoResponseDTO(
                clienteId,
                "Teste",
                false,
                LocalDateTime.now(),
                "Adesão encerrada. Sua posição em custodia foi mantida."
        );

        when(clienteService.cancelarAdesao(clienteId)).thenReturn(cancelamentoResponse);

        // Act
        mockMvc.perform(post("/api/clientes/" + clienteId + "/saida")
                .contentType(MediaType.APPLICATION_JSON));

        // Assert
        verify(clienteService, times(1)).cancelarAdesao(999L);
    }

    @Test
    void deveRetornarMensagemDeCancelamento() throws Exception {
        // Arrange
        Long clienteId = 1L;
        AdesaoCancelamentoResponseDTO cancelamentoResponse = new AdesaoCancelamentoResponseDTO(
                clienteId,
                "João Silva",
                false,
                LocalDateTime.now(),
                "Adesão encerrada. Sua posição em custodia foi mantida."
        );

        when(clienteService.cancelarAdesao(clienteId)).thenReturn(cancelamentoResponse);

        // Act & Assert
        mockMvc.perform(post("/api/clientes/" + clienteId + "/saida")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensagem").value("Adesão encerrada. Sua posição em custodia foi mantida."));
    }
}
