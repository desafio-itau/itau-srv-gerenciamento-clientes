package com.itau.srv.gerenciamento.clientes.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itau.srv.gerenciamento.clientes.dto.adesao.AdesaoCancelamentoResponseDTO;
import com.itau.srv.gerenciamento.clientes.dto.adesao.AdesaoRequestDTO;
import com.itau.srv.gerenciamento.clientes.dto.adesao.AdesaoResponseDTO;
import com.itau.srv.gerenciamento.clientes.dto.carteira.AtivoResponseDTO;
import com.itau.srv.gerenciamento.clientes.dto.carteira.CarteiraResponseDTO;
import com.itau.srv.gerenciamento.clientes.dto.carteira.ResumoResponseDTO;
import com.itau.srv.gerenciamento.clientes.dto.contagrafica.ContaGraficaResponseDTO;
import com.itau.srv.gerenciamento.clientes.dto.valormensal.AlterarValorMensalRequestDTO;
import com.itau.srv.gerenciamento.clientes.dto.valormensal.AlterarValorMensalResponseDTO;
import com.itau.srv.gerenciamento.clientes.dto.rentabilidade.RentabilidadeResponseDTO;
import com.itau.srv.gerenciamento.clientes.dto.rentabilidade.EvolucaoCarteiraResponseDTO;
import com.itau.srv.gerenciamento.clientes.dto.rentabilidade.HistoricoAportesResponseDTO;
import com.itau.srv.gerenciamento.clientes.model.enums.TipoConta;
import com.itau.srv.gerenciamento.clientes.service.CarteiraService;
import com.itau.srv.gerenciamento.clientes.service.ClienteService;
import com.itau.srv.gerenciamento.clientes.service.RentabilidadeService;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ClientesControllerTest {

    @Mock
    private ClienteService clienteService;

    @Mock
    private CarteiraService carteiraService;

    @Mock
    private RentabilidadeService rentabilidadeService;

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

    // Testes de Alteração de Valor Mensal

    @Test
    void deveAlterarValorMensalComSucesso() throws Exception {
        // Arrange
        Long clienteId = 1L;
        AlterarValorMensalRequestDTO requestDTO = new AlterarValorMensalRequestDTO(new BigDecimal("200.00"));
        AlterarValorMensalResponseDTO responseDTO = new AlterarValorMensalResponseDTO(
                clienteId,
                new BigDecimal("150.00"),
                new BigDecimal("200.00"),
                LocalDateTime.now(),
                "Valor mensal atualizado. O novo valor será considerado a partir da próxima data de compra."
        );

        when(clienteService.alterarValorMensal(eq(clienteId), any(AlterarValorMensalRequestDTO.class)))
                .thenReturn(responseDTO);

        // Act & Assert
        mockMvc.perform(put("/api/clientes/" + clienteId + "/valor-mensal")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.clienteId").value(clienteId))
                .andExpect(jsonPath("$.valorMensalAnterior").value(150.00))
                .andExpect(jsonPath("$.valorMensalNovo").value(200.00))
                .andExpect(jsonPath("$.dataAlteracao").exists())
                .andExpect(jsonPath("$.mensagem").value("Valor mensal atualizado. O novo valor será considerado a partir da próxima data de compra."));

        verify(clienteService, times(1)).alterarValorMensal(eq(clienteId), any(AlterarValorMensalRequestDTO.class));
    }

    @Test
    void deveRetornarStatus200QuandoAlteracaoComSucesso() throws Exception {
        // Arrange
        Long clienteId = 1L;
        AlterarValorMensalRequestDTO requestDTO = new AlterarValorMensalRequestDTO(new BigDecimal("250.00"));
        AlterarValorMensalResponseDTO responseDTO = new AlterarValorMensalResponseDTO(
                clienteId,
                new BigDecimal("150.00"),
                new BigDecimal("250.00"),
                LocalDateTime.now(),
                "Valor mensal atualizado. O novo valor será considerado a partir da próxima data de compra."
        );

        when(clienteService.alterarValorMensal(eq(clienteId), any(AlterarValorMensalRequestDTO.class)))
                .thenReturn(responseDTO);

        // Act & Assert
        mockMvc.perform(put("/api/clientes/" + clienteId + "/valor-mensal")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk());
    }

    @Test
    void deveRetornarDadosDaAlteracaoNaResposta() throws Exception {
        // Arrange
        Long clienteId = 1L;
        AlterarValorMensalRequestDTO requestDTO = new AlterarValorMensalRequestDTO(new BigDecimal("300.00"));
        AlterarValorMensalResponseDTO responseDTO = new AlterarValorMensalResponseDTO(
                clienteId,
                new BigDecimal("150.00"),
                new BigDecimal("300.00"),
                LocalDateTime.now(),
                "Valor mensal atualizado. O novo valor será considerado a partir da próxima data de compra."
        );

        when(clienteService.alterarValorMensal(eq(clienteId), any(AlterarValorMensalRequestDTO.class)))
                .thenReturn(responseDTO);

        // Act & Assert
        mockMvc.perform(put("/api/clientes/" + clienteId + "/valor-mensal")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.clienteId").exists())
                .andExpect(jsonPath("$.valorMensalAnterior").exists())
                .andExpect(jsonPath("$.valorMensalNovo").exists())
                .andExpect(jsonPath("$.dataAlteracao").exists())
                .andExpect(jsonPath("$.mensagem").exists());
    }

    @Test
    void deveRetornarValorAntigoENovoCorretos() throws Exception {
        // Arrange
        Long clienteId = 1L;
        AlterarValorMensalRequestDTO requestDTO = new AlterarValorMensalRequestDTO(new BigDecimal("350.00"));
        AlterarValorMensalResponseDTO responseDTO = new AlterarValorMensalResponseDTO(
                clienteId,
                new BigDecimal("150.00"),
                new BigDecimal("350.00"),
                LocalDateTime.now(),
                "Valor mensal atualizado. O novo valor será considerado a partir da próxima data de compra."
        );

        when(clienteService.alterarValorMensal(eq(clienteId), any(AlterarValorMensalRequestDTO.class)))
                .thenReturn(responseDTO);

        // Act & Assert
        mockMvc.perform(put("/api/clientes/" + clienteId + "/valor-mensal")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valorMensalAnterior").value(150.00))
                .andExpect(jsonPath("$.valorMensalNovo").value(350.00));
    }

    @Test
    void deveChamarServiceParaAlterar() throws Exception {
        // Arrange
        Long clienteId = 1L;
        AlterarValorMensalRequestDTO requestDTO = new AlterarValorMensalRequestDTO(new BigDecimal("180.00"));
        AlterarValorMensalResponseDTO responseDTO = new AlterarValorMensalResponseDTO(
                clienteId,
                new BigDecimal("150.00"),
                new BigDecimal("180.00"),
                LocalDateTime.now(),
                "Valor mensal atualizado. O novo valor será considerado a partir da próxima data de compra."
        );

        when(clienteService.alterarValorMensal(eq(clienteId), any(AlterarValorMensalRequestDTO.class)))
                .thenReturn(responseDTO);

        // Act
        mockMvc.perform(put("/api/clientes/" + clienteId + "/valor-mensal")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)));

        // Assert
        verify(clienteService, times(1)).alterarValorMensal(eq(clienteId), any(AlterarValorMensalRequestDTO.class));
    }

    @Test
    void deveUsarClienteIdDoPathVariableParaAlteracao() throws Exception {
        // Arrange
        Long clienteId = 999L;
        AlterarValorMensalRequestDTO requestDTO = new AlterarValorMensalRequestDTO(new BigDecimal("500.00"));
        AlterarValorMensalResponseDTO responseDTO = new AlterarValorMensalResponseDTO(
                clienteId,
                new BigDecimal("150.00"),
                new BigDecimal("500.00"),
                LocalDateTime.now(),
                "Valor mensal atualizado. O novo valor será considerado a partir da próxima data de compra."
        );

        when(clienteService.alterarValorMensal(eq(clienteId), any(AlterarValorMensalRequestDTO.class)))
                .thenReturn(responseDTO);

        // Act
        mockMvc.perform(put("/api/clientes/" + clienteId + "/valor-mensal")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)));

        // Assert
        verify(clienteService, times(1)).alterarValorMensal(eq(999L), any(AlterarValorMensalRequestDTO.class));
    }

    @Test
    void deveRetornarMensagemDeAlteracao() throws Exception {
        // Arrange
        Long clienteId = 1L;
        AlterarValorMensalRequestDTO requestDTO = new AlterarValorMensalRequestDTO(new BigDecimal("220.00"));
        AlterarValorMensalResponseDTO responseDTO = new AlterarValorMensalResponseDTO(
                clienteId,
                new BigDecimal("150.00"),
                new BigDecimal("220.00"),
                LocalDateTime.now(),
                "Valor mensal atualizado. O novo valor será considerado a partir da próxima data de compra."
        );

        when(clienteService.alterarValorMensal(eq(clienteId), any(AlterarValorMensalRequestDTO.class)))
                .thenReturn(responseDTO);

        // Act & Assert
        mockMvc.perform(put("/api/clientes/" + clienteId + "/valor-mensal")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensagem").value("Valor mensal atualizado. O novo valor será considerado a partir da próxima data de compra."));
    }

    @Test
    void deveAceitarRequestBodyComNovoValorMensal() throws Exception {
        // Arrange
        Long clienteId = 1L;
        String jsonRequest = """
                {
                    "novoValorMensal": 400.00
                }
                """;
        AlterarValorMensalResponseDTO responseDTO = new AlterarValorMensalResponseDTO(
                clienteId,
                new BigDecimal("150.00"),
                new BigDecimal("400.00"),
                LocalDateTime.now(),
                "Valor mensal atualizado. O novo valor será considerado a partir da próxima data de compra."
        );

        when(clienteService.alterarValorMensal(eq(clienteId), any(AlterarValorMensalRequestDTO.class)))
                .thenReturn(responseDTO);

        // Act & Assert
        mockMvc.perform(put("/api/clientes/" + clienteId + "/valor-mensal")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valorMensalNovo").value(400.00));
    }

    // ==================== TESTES DE CONSULTA DE CARTEIRA ====================

    @Test
    void deveBuscarCarteiraComSucesso() throws Exception {
        // Arrange
        Long clienteId = 1L;
        ResumoResponseDTO resumo = new ResumoResponseDTO(
                new BigDecimal("5000.00"),
                new BigDecimal("5500.00"),
                new BigDecimal("500.00"),
                new BigDecimal("10.00")
        );

        AtivoResponseDTO ativo1 = new AtivoResponseDTO(
                "PETR4",
                100,
                new BigDecimal("30.00"),
                new BigDecimal("35.00"),
                new BigDecimal("500.00"),
                new BigDecimal("0.67"),
                new BigDecimal("0.52")
        );

        AtivoResponseDTO ativo2 = new AtivoResponseDTO(
                "VALE3",
                50,
                new BigDecimal("60.00"),
                new BigDecimal("65.00"),
                new BigDecimal("250.00"),
                new BigDecimal("0.33"),
                new BigDecimal("0.48")
        );

        CarteiraResponseDTO carteiraResponse = new CarteiraResponseDTO(
                clienteId,
                "João Silva",
                "ITAUFL00001",
                LocalDateTime.now(),
                resumo,
                Arrays.asList(ativo1, ativo2)
        );

        when(carteiraService.consultarCarteiraCliente(clienteId)).thenReturn(carteiraResponse);

        // Act & Assert
        mockMvc.perform(get("/api/clientes/" + clienteId + "/carteira")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.clienteId").value(1))
                .andExpect(jsonPath("$.nome").value("João Silva"))
                .andExpect(jsonPath("$.contaGrafica").value("ITAUFL00001"))
                .andExpect(jsonPath("$.dataConsulta").exists())
                .andExpect(jsonPath("$.resumo").exists())
                .andExpect(jsonPath("$.resumo.valorTotalInvestido").value(5000.00))
                .andExpect(jsonPath("$.resumo.valorAtualCarteira").value(5500.00))
                .andExpect(jsonPath("$.resumo.plTotal").value(500.00))
                .andExpect(jsonPath("$.resumo.rentabilidadePercentual").value(10.00))
                .andExpect(jsonPath("$.ativos").isArray())
                .andExpect(jsonPath("$.ativos.length()").value(2))
                .andExpect(jsonPath("$.ativos[0].ticker").value("PETR4"))
                .andExpect(jsonPath("$.ativos[1].ticker").value("VALE3"));

        verify(carteiraService, times(1)).consultarCarteiraCliente(clienteId);
    }

    @Test
    void deveChamarServiceParaBuscarCarteira() throws Exception {
        // Arrange
        Long clienteId = 5L;
        CarteiraResponseDTO carteiraResponse = new CarteiraResponseDTO(
                clienteId,
                "Maria Santos",
                "ITAUFL00005",
                LocalDateTime.now(),
                new ResumoResponseDTO(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO),
                Collections.emptyList()
        );

        when(carteiraService.consultarCarteiraCliente(clienteId)).thenReturn(carteiraResponse);

        // Act & Assert
        mockMvc.perform(get("/api/clientes/" + clienteId + "/carteira"))
                .andExpect(status().isOk());

        verify(carteiraService, times(1)).consultarCarteiraCliente(clienteId);
    }

    @Test
    void deveRetornarDadosDoResumoNaCarteira() throws Exception {
        // Arrange
        Long clienteId = 1L;
        ResumoResponseDTO resumo = new ResumoResponseDTO(
                new BigDecimal("10000.00"),
                new BigDecimal("12000.00"),
                new BigDecimal("2000.00"),
                new BigDecimal("20.00")
        );

        CarteiraResponseDTO carteiraResponse = new CarteiraResponseDTO(
                clienteId,
                "João Silva",
                "ITAUFL00001",
                LocalDateTime.now(),
                resumo,
                Collections.emptyList()
        );

        when(carteiraService.consultarCarteiraCliente(clienteId)).thenReturn(carteiraResponse);

        // Act & Assert
        mockMvc.perform(get("/api/clientes/" + clienteId + "/carteira"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resumo.valorTotalInvestido").value(10000.00))
                .andExpect(jsonPath("$.resumo.valorAtualCarteira").value(12000.00))
                .andExpect(jsonPath("$.resumo.plTotal").value(2000.00))
                .andExpect(jsonPath("$.resumo.rentabilidadePercentual").value(20.00));
    }

    @Test
    void deveRetornarListaDeAtivosNaCarteira() throws Exception {
        // Arrange
        Long clienteId = 1L;
        AtivoResponseDTO ativo1 = new AtivoResponseDTO(
                "PETR4",
                100,
                new BigDecimal("30.00"),
                new BigDecimal("35.00"),
                new BigDecimal("500.00"),
                new BigDecimal("1.00"),
                new BigDecimal("1.00")
        );

        CarteiraResponseDTO carteiraResponse = new CarteiraResponseDTO(
                clienteId,
                "João Silva",
                "ITAUFL00001",
                LocalDateTime.now(),
                new ResumoResponseDTO(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO),
                Collections.singletonList(ativo1)
        );

        when(carteiraService.consultarCarteiraCliente(clienteId)).thenReturn(carteiraResponse);

        // Act & Assert
        mockMvc.perform(get("/api/clientes/" + clienteId + "/carteira"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ativos").isArray())
                .andExpect(jsonPath("$.ativos.length()").value(1))
                .andExpect(jsonPath("$.ativos[0].ticker").value("PETR4"))
                .andExpect(jsonPath("$.ativos[0].quantidade").value(100))
                .andExpect(jsonPath("$.ativos[0].precoMedio").value(30.00))
                .andExpect(jsonPath("$.ativos[0].cotacaoAtual").value(35.00))
                .andExpect(jsonPath("$.ativos[0].pl").value(500.00))
                .andExpect(jsonPath("$.ativos[0].plPercentual").value(1.00))
                .andExpect(jsonPath("$.ativos[0].composicaoCarteira").value(1.00));
    }

    @Test
    void deveRetornarCarteiraVaziaQuandoNaoHaAtivos() throws Exception {
        // Arrange
        Long clienteId = 1L;
        CarteiraResponseDTO carteiraResponse = new CarteiraResponseDTO(
                clienteId,
                "João Silva",
                "ITAUFL00001",
                LocalDateTime.now(),
                new ResumoResponseDTO(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO),
                Collections.emptyList()
        );

        when(carteiraService.consultarCarteiraCliente(clienteId)).thenReturn(carteiraResponse);

        // Act & Assert
        mockMvc.perform(get("/api/clientes/" + clienteId + "/carteira"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ativos").isArray())
                .andExpect(jsonPath("$.ativos.length()").value(0))
                .andExpect(jsonPath("$.resumo.valorTotalInvestido").value(0))
                .andExpect(jsonPath("$.resumo.valorAtualCarteira").value(0));
    }

    @Test
    void deveRetornarDadosCompletosDosAtivos() throws Exception {
        // Arrange
        Long clienteId = 1L;
        AtivoResponseDTO ativo = new AtivoResponseDTO(
                "ITUB4",
                200,
                new BigDecimal("25.50"),
                new BigDecimal("27.00"),
                new BigDecimal("300.00"),
                new BigDecimal("0.75"),
                new BigDecimal("0.85")
        );

        CarteiraResponseDTO carteiraResponse = new CarteiraResponseDTO(
                clienteId,
                "João Silva",
                "ITAUFL00001",
                LocalDateTime.now(),
                new ResumoResponseDTO(
                        new BigDecimal("5100.00"),
                        new BigDecimal("5400.00"),
                        new BigDecimal("300.00"),
                        new BigDecimal("5.88")
                ),
                Collections.singletonList(ativo)
        );

        when(carteiraService.consultarCarteiraCliente(clienteId)).thenReturn(carteiraResponse);

        // Act & Assert
        mockMvc.perform(get("/api/clientes/" + clienteId + "/carteira"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ativos[0].ticker").value("ITUB4"))
                .andExpect(jsonPath("$.ativos[0].quantidade").value(200))
                .andExpect(jsonPath("$.ativos[0].precoMedio").value(25.50))
                .andExpect(jsonPath("$.ativos[0].cotacaoAtual").value(27.00))
                .andExpect(jsonPath("$.ativos[0].pl").value(300.00))
                .andExpect(jsonPath("$.ativos[0].plPercentual").value(0.75))
                .andExpect(jsonPath("$.ativos[0].composicaoCarteira").value(0.85));
    }

    @Test
    void deveUsarPathVariableCorretamente() throws Exception {
        // Arrange
        Long clienteId = 99L;
        CarteiraResponseDTO carteiraResponse = new CarteiraResponseDTO(
                clienteId,
                "Teste",
                "ITAUFL00099",
                LocalDateTime.now(),
                new ResumoResponseDTO(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO),
                Collections.emptyList()
        );

        when(carteiraService.consultarCarteiraCliente(clienteId)).thenReturn(carteiraResponse);

        // Act & Assert
        mockMvc.perform(get("/api/clientes/" + clienteId + "/carteira"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.clienteId").value(99));

        verify(carteiraService).consultarCarteiraCliente(99L);
    }

    // =========== TESTES PARA GERAR SNAPSHOT DE CARTEIRAS ===========

    @Test
    void deveGerarSnapshotDeCarteirasComSucesso() throws Exception {
        // Arrange
        String data = "2026-02-05";
        doNothing().when(carteiraService).gerarSnapshots(any());

        // Act & Assert
        mockMvc.perform(post("/api/clientes/carteiras-snapshots")
                        .param("data", data))
                .andExpect(status().isNoContent());

        verify(carteiraService).gerarSnapshots(any());
    }

    @Test
    void deveChamarServiceParaGerarSnapshotComDataCorreta() throws Exception {
        // Arrange
        String data = "2026-02-15";
        doNothing().when(carteiraService).gerarSnapshots(any());

        // Act
        mockMvc.perform(post("/api/clientes/carteiras-snapshots")
                        .param("data", data))
                .andExpect(status().isNoContent());

        // Assert
        verify(carteiraService, times(1)).gerarSnapshots(any());
    }

    @Test
    void deveRetornar204AoGerarSnapshot() throws Exception {
        // Arrange
        String data = "2026-01-05";
        doNothing().when(carteiraService).gerarSnapshots(any());

        // Act & Assert
        mockMvc.perform(post("/api/clientes/carteiras-snapshots")
                        .param("data", data))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));
    }

    // =========== TESTES PARA BUSCAR CLIENTES ATIVOS ===========

    @Test
    void deveBuscarClientesAtivosComSucesso() throws Exception {
        // Arrange
        ContaGraficaResponseDTO contaGrafica1 = new ContaGraficaResponseDTO(
                1L,
                "ITAUFL00001",
                TipoConta.FILHOTE,
                LocalDateTime.now()
        );

        ContaGraficaResponseDTO contaGrafica2 = new ContaGraficaResponseDTO(
                2L,
                "ITAUFL00002",
                TipoConta.FILHOTE,
                LocalDateTime.now()
        );

        AdesaoResponseDTO cliente1 = new AdesaoResponseDTO(
                1L,
                "João Silva",
                "52998224725",
                "joao@email.com",
                new BigDecimal("150.00"),
                true,
                LocalDateTime.now(),
                contaGrafica1
        );

        AdesaoResponseDTO cliente2 = new AdesaoResponseDTO(
                2L,
                "Maria Santos",
                "12345678901",
                "maria@email.com",
                new BigDecimal("200.00"),
                true,
                LocalDateTime.now(),
                contaGrafica2
        );

        when(clienteService.buscarClientesAtivos()).thenReturn(Arrays.asList(cliente1, cliente2));

        // Act & Assert
        mockMvc.perform(get("/api/clientes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].clienteId").value(1))
                .andExpect(jsonPath("$[0].nome").value("João Silva"))
                .andExpect(jsonPath("$[0].cpf").value("52998224725"))
                .andExpect(jsonPath("$[1].clienteId").value(2))
                .andExpect(jsonPath("$[1].nome").value("Maria Santos"))
                .andExpect(jsonPath("$[1].cpf").value("12345678901"));

        verify(clienteService).buscarClientesAtivos();
    }

    @Test
    void deveRetornarListaVaziaQuandoNaoHouverClientesAtivos() throws Exception {
        // Arrange
        when(clienteService.buscarClientesAtivos()).thenReturn(Collections.emptyList());

        // Act & Assert
        mockMvc.perform(get("/api/clientes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));

        verify(clienteService).buscarClientesAtivos();
    }

    @Test
    void deveChamarServiceParaBuscarClientesAtivos() throws Exception {
        // Arrange
        when(clienteService.buscarClientesAtivos()).thenReturn(Collections.emptyList());

        // Act
        mockMvc.perform(get("/api/clientes"))
                .andExpect(status().isOk());

        // Assert
        verify(clienteService, times(1)).buscarClientesAtivos();
    }

    @Test
    void deveRetornarContentTypeJSONAoBuscarClientesAtivos() throws Exception {
        // Arrange
        when(clienteService.buscarClientesAtivos()).thenReturn(Collections.emptyList());

        // Act & Assert
        mockMvc.perform(get("/api/clientes"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(clienteService).buscarClientesAtivos();
    }

    // ============= Testes para Gerar Snapshot de Carteira =============

    @Test
    void deveGerarSnapshotCarteiraComSucesso() throws Exception {
        // Arrange
        LocalDate data = LocalDate.of(2026, 3, 5);
        doNothing().when(carteiraService).gerarSnapshots(any(LocalDate.class));

        // Act & Assert
        mockMvc.perform(post("/api/clientes/carteiras-snapshots")
                        .param("data", "2026-03-05"))
                .andExpect(status().isNoContent());

        verify(carteiraService, times(1)).gerarSnapshots(data);
    }

    @Test
    void deveChamarServiceParaGerarSnapshot() throws Exception {
        // Arrange
        LocalDate data = LocalDate.of(2026, 3, 1);
        doNothing().when(carteiraService).gerarSnapshots(any(LocalDate.class));

        // Act
        mockMvc.perform(post("/api/clientes/carteiras-snapshots")
                .param("data", "2026-03-01"));

        // Assert
        verify(carteiraService, times(1)).gerarSnapshots(data);
    }

    @Test
    void deveAceitarParametroDataNaRequisicao() throws Exception {
        // Arrange
        doNothing().when(carteiraService).gerarSnapshots(any(LocalDate.class));

        // Act & Assert
        mockMvc.perform(post("/api/clientes/carteiras-snapshots")
                        .param("data", "2026-02-15"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deveRetornarStatus204QuandoSnapshotGeradoComSucesso() throws Exception {
        // Arrange
        doNothing().when(carteiraService).gerarSnapshots(any(LocalDate.class));

        // Act & Assert
        mockMvc.perform(post("/api/clientes/carteiras-snapshots")
                        .param("data", "2026-03-01"))
                .andExpect(status().isNoContent());
    }

    // ============= Testes para Consultar Rentabilidade =============

    @Test
    void deveConsultarRentabilidadeComSucesso() throws Exception {
        // Arrange
        RentabilidadeResponseDTO rentabilidadeResponse = criarRentabilidadeResponseMock();
        when(rentabilidadeService.consultarRentabilidade(anyLong())).thenReturn(rentabilidadeResponse);

        // Act & Assert
        mockMvc.perform(get("/api/clientes/1/rentabilidade"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.clienteId").value(1))
                .andExpect(jsonPath("$.nome").value("João Silva"))
                .andExpect(jsonPath("$.rentabilidade").exists())
                .andExpect(jsonPath("$.historicoAportes").isArray())
                .andExpect(jsonPath("$.evolucaoCarteira").isArray());

        verify(rentabilidadeService, times(1)).consultarRentabilidade(1L);
    }

    @Test
    void deveChamarServiceParaConsultarRentabilidade() throws Exception {
        // Arrange
        RentabilidadeResponseDTO rentabilidadeResponse = criarRentabilidadeResponseMock();
        when(rentabilidadeService.consultarRentabilidade(anyLong())).thenReturn(rentabilidadeResponse);

        // Act
        mockMvc.perform(get("/api/clientes/1/rentabilidade"));

        // Assert
        verify(rentabilidadeService, times(1)).consultarRentabilidade(1L);
    }

    @Test
    void deveRetornarDadosDeRentabilidadeNaResposta() throws Exception {
        // Arrange
        RentabilidadeResponseDTO rentabilidadeResponse = criarRentabilidadeResponseMock();
        when(rentabilidadeService.consultarRentabilidade(anyLong())).thenReturn(rentabilidadeResponse);

        // Act & Assert
        mockMvc.perform(get("/api/clientes/1/rentabilidade"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rentabilidade.valorTotalInvestido").value(3000.00))
                .andExpect(jsonPath("$.rentabilidade.valorAtualCarteira").value(3300.00))
                .andExpect(jsonPath("$.rentabilidade.plTotal").value(300.00))
                .andExpect(jsonPath("$.rentabilidade.rentabilidadePercentual").value(10.00));
    }

    @Test
    void deveRetornarHistoricoAportesNaResposta() throws Exception {
        // Arrange
        RentabilidadeResponseDTO rentabilidadeResponse = criarRentabilidadeResponseMock();
        when(rentabilidadeService.consultarRentabilidade(anyLong())).thenReturn(rentabilidadeResponse);

        // Act & Assert
        mockMvc.perform(get("/api/clientes/1/rentabilidade"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.historicoAportes").isArray())
                .andExpect(jsonPath("$.historicoAportes.length()").value(3))
                .andExpect(jsonPath("$.historicoAportes[0].data").isArray())
                .andExpect(jsonPath("$.historicoAportes[0].valor").value(1000.00))
                .andExpect(jsonPath("$.historicoAportes[0].parcela").value("1/12"));
    }

    @Test
    void deveRetornarEvolucaoCarteiraNaResposta() throws Exception {
        // Arrange
        RentabilidadeResponseDTO rentabilidadeResponse = criarRentabilidadeResponseMock();
        when(rentabilidadeService.consultarRentabilidade(anyLong())).thenReturn(rentabilidadeResponse);

        // Act & Assert
        mockMvc.perform(get("/api/clientes/1/rentabilidade"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.evolucaoCarteira").isArray())
                .andExpect(jsonPath("$.evolucaoCarteira.length()").value(3))
                .andExpect(jsonPath("$.evolucaoCarteira[0].data").isArray())
                .andExpect(jsonPath("$.evolucaoCarteira[0].valorCarteira").value(1000.00))
                .andExpect(jsonPath("$.evolucaoCarteira[0].valorInvestido").value(1000.00))
                .andExpect(jsonPath("$.evolucaoCarteira[0].rentabilidade").value(0.00));
    }

    @Test
    void deveRetornarContentTypeJSONAoConsultarRentabilidade() throws Exception {
        // Arrange
        RentabilidadeResponseDTO rentabilidadeResponse = criarRentabilidadeResponseMock();
        when(rentabilidadeService.consultarRentabilidade(anyLong())).thenReturn(rentabilidadeResponse);

        // Act & Assert
        mockMvc.perform(get("/api/clientes/1/rentabilidade"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void deveUsarPathVariableClienteIdParaConsultarRentabilidade() throws Exception {
        // Arrange
        RentabilidadeResponseDTO rentabilidadeResponse = criarRentabilidadeResponseMock();
        when(rentabilidadeService.consultarRentabilidade(anyLong())).thenReturn(rentabilidadeResponse);

        // Act
        mockMvc.perform(get("/api/clientes/5/rentabilidade"))
                .andExpect(status().isOk());

        // Assert
        verify(rentabilidadeService, times(1)).consultarRentabilidade(5L);
    }

    // ============= Métodos auxiliares =============

    private RentabilidadeResponseDTO criarRentabilidadeResponseMock() {
        ResumoResponseDTO resumo = new ResumoResponseDTO(
                new BigDecimal("3000.00"),  // valorTotalInvestido
                new BigDecimal("3300.00"),  // valorAtualCarteira
                new BigDecimal("300.00"),   // plTotal
                new BigDecimal("10.00")     // rentabilidadePercentual
        );

        HistoricoAportesResponseDTO aporte1 = new HistoricoAportesResponseDTO(
                LocalDate.of(2026, 1, 5),
                new BigDecimal("1000.00"),
                "1/12"
        );

        HistoricoAportesResponseDTO aporte2 = new HistoricoAportesResponseDTO(
                LocalDate.of(2026, 2, 5),
                new BigDecimal("1000.00"),
                "2/12"
        );

        HistoricoAportesResponseDTO aporte3 = new HistoricoAportesResponseDTO(
                LocalDate.of(2026, 3, 5),
                new BigDecimal("1000.00"),
                "3/12"
        );

        EvolucaoCarteiraResponseDTO evolucao1 = new EvolucaoCarteiraResponseDTO(
                LocalDate.of(2026, 1, 5),
                new BigDecimal("1000.00"),
                new BigDecimal("1000.00"),
                BigDecimal.ZERO
        );

        EvolucaoCarteiraResponseDTO evolucao2 = new EvolucaoCarteiraResponseDTO(
                LocalDate.of(2026, 2, 5),
                new BigDecimal("2100.00"),
                new BigDecimal("2000.00"),
                new BigDecimal("5.00")
        );

        EvolucaoCarteiraResponseDTO evolucao3 = new EvolucaoCarteiraResponseDTO(
                LocalDate.of(2026, 3, 5),
                new BigDecimal("3300.00"),
                new BigDecimal("3000.00"),
                new BigDecimal("10.00")
        );

        return new RentabilidadeResponseDTO(
                1L,
                "João Silva",
                LocalDateTime.now(),
                resumo,
                Arrays.asList(aporte1, aporte2, aporte3),
                Arrays.asList(evolucao1, evolucao2, evolucao3)
        );
    }
}
