package com.itau.srv.gerenciamento.clientes.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itau.srv.gerenciamento.clientes.dto.contagrafica.ContaGraficaResponseDTO;
import com.itau.srv.gerenciamento.clientes.model.enums.TipoConta;
import com.itau.srv.gerenciamento.clientes.service.ContaGraficaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ContaGraficaControllerTest {

    @Mock
    private ContaGraficaService contaGraficaService;

    @InjectMocks
    private ContaGraficaController contaGraficaController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private ContaGraficaResponseDTO contaGraficaResponseDTO;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(contaGraficaController).build();
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();

        contaGraficaResponseDTO = new ContaGraficaResponseDTO(
                1L,
                "ITAUFL00001",
                TipoConta.FILHOTE,
                LocalDateTime.of(2026, 2, 5, 10, 30, 0)
        );
    }

    // =========== TESTES PARA BUSCAR CONTA GRÁFICA ===========

    @Test
    void deveBuscarContaGraficaComSucesso() throws Exception {
        // Arrange
        Long contaId = 1L;
        when(contaGraficaService.buscarConta(contaId)).thenReturn(contaGraficaResponseDTO);

        // Act & Assert
        mockMvc.perform(get("/api/contas-graficas/" + contaId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.numeroConta").value("ITAUFL00001"))
                .andExpect(jsonPath("$.tipoConta").value("FILHOTE"));

        verify(contaGraficaService).buscarConta(contaId);
    }

    @Test
    void deveRetornarDadosCorretosDaConta() throws Exception {
        // Arrange
        Long contaId = 5L;
        ContaGraficaResponseDTO conta = new ContaGraficaResponseDTO(
                5L,
                "ITAUFL00005",
                TipoConta.FILHOTE,
                LocalDateTime.of(2026, 2, 10, 14, 0, 0)
        );
        when(contaGraficaService.buscarConta(contaId)).thenReturn(conta);

        // Act & Assert
        mockMvc.perform(get("/api/contas-graficas/" + contaId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.numeroConta").value("ITAUFL00005"))
                .andExpect(jsonPath("$.tipoConta").value("FILHOTE"));

        verify(contaGraficaService).buscarConta(contaId);
    }

    @Test
    void deveChamarServiceComIdCorreto() throws Exception {
        // Arrange
        Long contaId = 10L;
        when(contaGraficaService.buscarConta(contaId)).thenReturn(contaGraficaResponseDTO);

        // Act
        mockMvc.perform(get("/api/contas-graficas/" + contaId))
                .andExpect(status().isOk());

        // Assert
        verify(contaGraficaService, times(1)).buscarConta(10L);
    }

    @Test
    void deveRetornarStatus200AoBuscarConta() throws Exception {
        // Arrange
        Long contaId = 1L;
        when(contaGraficaService.buscarConta(contaId)).thenReturn(contaGraficaResponseDTO);

        // Act & Assert
        mockMvc.perform(get("/api/contas-graficas/" + contaId))
                .andExpect(status().isOk());

        verify(contaGraficaService).buscarConta(contaId);
    }

    @Test
    void deveRetornarContentTypeJSON() throws Exception {
        // Arrange
        Long contaId = 1L;
        when(contaGraficaService.buscarConta(contaId)).thenReturn(contaGraficaResponseDTO);

        // Act & Assert
        mockMvc.perform(get("/api/contas-graficas/" + contaId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(contaGraficaService).buscarConta(contaId);
    }

    @Test
    void deveProcessarPathVariableCorretamente() throws Exception {
        // Arrange
        Long contaId = 999L;
        ContaGraficaResponseDTO contaResponse = new ContaGraficaResponseDTO(
                999L,
                "ITAUFL00999",
                TipoConta.FILHOTE,
                LocalDateTime.now()
        );
        when(contaGraficaService.buscarConta(contaId)).thenReturn(contaResponse);

        // Act & Assert
        mockMvc.perform(get("/api/contas-graficas/999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(999))
                .andExpect(jsonPath("$.numeroConta").value("ITAUFL00999"));

        verify(contaGraficaService).buscarConta(999L);
    }

    @Test
    void deveBuscarContaTipoFilhote() throws Exception {
        // Arrange
        Long contaId = 1L;
        when(contaGraficaService.buscarConta(contaId)).thenReturn(contaGraficaResponseDTO);

        // Act & Assert
        mockMvc.perform(get("/api/contas-graficas/" + contaId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tipoConta").value("FILHOTE"));

        verify(contaGraficaService).buscarConta(contaId);
    }

    @Test
    void deveBuscarContaTipoMaster() throws Exception {
        // Arrange
        Long contaId = 1L;
        ContaGraficaResponseDTO contaMaster = new ContaGraficaResponseDTO(
                1L,
                "ITAUFL00001",
                TipoConta.MASTER,
                LocalDateTime.now()
        );
        when(contaGraficaService.buscarConta(contaId)).thenReturn(contaMaster);

        // Act & Assert
        mockMvc.perform(get("/api/contas-graficas/" + contaId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tipoConta").value("MASTER"));

        verify(contaGraficaService).buscarConta(contaId);
    }

    @Test
    void deveRetornarNumeroDaContaFormatadoCorretamente() throws Exception {
        // Arrange
        Long contaId = 1L;
        when(contaGraficaService.buscarConta(contaId)).thenReturn(contaGraficaResponseDTO);

        // Act & Assert
        mockMvc.perform(get("/api/contas-graficas/" + contaId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numeroConta").value("ITAUFL00001"));

        verify(contaGraficaService).buscarConta(contaId);
    }

    @Test
    void deveChamarServiceApenasUmaVez() throws Exception {
        // Arrange
        Long contaId = 1L;
        when(contaGraficaService.buscarConta(contaId)).thenReturn(contaGraficaResponseDTO);

        // Act
        mockMvc.perform(get("/api/contas-graficas/" + contaId))
                .andExpect(status().isOk());

        // Assert
        verify(contaGraficaService, times(1)).buscarConta(anyLong());
    }
}

