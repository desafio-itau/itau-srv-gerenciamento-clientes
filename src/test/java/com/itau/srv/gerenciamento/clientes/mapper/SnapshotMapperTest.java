package com.itau.srv.gerenciamento.clientes.mapper;

import com.itau.srv.gerenciamento.clientes.dto.rentabilidade.EvolucaoCarteiraResponseDTO;
import com.itau.srv.gerenciamento.clientes.model.SnapshotCarteira;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class SnapshotMapperTest {

    private SnapshotMapper snapshotMapper;
    private SnapshotCarteira snapshot;

    @BeforeEach
    void setUp() {
        snapshotMapper = new SnapshotMapper();

        snapshot = new SnapshotCarteira();
        snapshot.setClienteId(1L);
        snapshot.setDataSnapshot(LocalDate.of(2026, 3, 1));
        snapshot.setValorCarteira(new BigDecimal("10000.00"));
        snapshot.setValorInvestido(new BigDecimal("8000.00"));
        snapshot.setRentabilidade(new BigDecimal("25.00"));
    }

    @Test
    void deveMapearSnapshotParaEvolucaoCarteiraResponseDTO() {
        // Act
        EvolucaoCarteiraResponseDTO resultado = snapshotMapper.mapearParaEvolucaoCarteiraResponseDTO(snapshot);

        // Assert
        assertNotNull(resultado);
        assertEquals(LocalDate.of(2026, 3, 1), resultado.data());
        assertEquals(new BigDecimal("10000.00"), resultado.valorCarteira());
        assertEquals(new BigDecimal("8000.00"), resultado.valorInvestido());
        assertEquals(new BigDecimal("25.00"), resultado.rentabilidade());
    }

    @Test
    void deveMapearSnapshotComRentabilidadeZero() {
        // Arrange
        snapshot.setRentabilidade(BigDecimal.ZERO);

        // Act
        EvolucaoCarteiraResponseDTO resultado = snapshotMapper.mapearParaEvolucaoCarteiraResponseDTO(snapshot);

        // Assert
        assertNotNull(resultado);
        assertEquals(BigDecimal.ZERO, resultado.rentabilidade());
    }

    @Test
    void deveMapearSnapshotComRentabilidadeNegativa() {
        // Arrange
        snapshot.setRentabilidade(new BigDecimal("-15.50"));

        // Act
        EvolucaoCarteiraResponseDTO resultado = snapshotMapper.mapearParaEvolucaoCarteiraResponseDTO(snapshot);

        // Assert
        assertNotNull(resultado);
        assertEquals(new BigDecimal("-15.50"), resultado.rentabilidade());
    }

    @Test
    void deveMapearTodosOsCamposCorretamente() {
        // Act
        EvolucaoCarteiraResponseDTO resultado = snapshotMapper.mapearParaEvolucaoCarteiraResponseDTO(snapshot);

        // Assert
        assertNotNull(resultado);
        assertAll("Verificar todos os campos",
                () -> assertNotNull(resultado.data(), "Data não deve ser nula"),
                () -> assertNotNull(resultado.valorCarteira(), "Valor da carteira não deve ser nulo"),
                () -> assertNotNull(resultado.valorInvestido(), "Valor investido não deve ser nulo"),
                () -> assertNotNull(resultado.rentabilidade(), "Rentabilidade não deve ser nula")
        );
    }

    @Test
    void deveMapearSnapshotComValoresGrandes() {
        // Arrange
        snapshot.setValorCarteira(new BigDecimal("999999999.99"));
        snapshot.setValorInvestido(new BigDecimal("500000000.00"));
        snapshot.setRentabilidade(new BigDecimal("99.99"));

        // Act
        EvolucaoCarteiraResponseDTO resultado = snapshotMapper.mapearParaEvolucaoCarteiraResponseDTO(snapshot);

        // Assert
        assertNotNull(resultado);
        assertEquals(new BigDecimal("999999999.99"), resultado.valorCarteira());
        assertEquals(new BigDecimal("500000000.00"), resultado.valorInvestido());
        assertEquals(new BigDecimal("99.99"), resultado.rentabilidade());
    }

    @Test
    void deveMapearSnapshotComDataAtual() {
        // Arrange
        LocalDate dataAtual = LocalDate.now();
        snapshot.setDataSnapshot(dataAtual);

        // Act
        EvolucaoCarteiraResponseDTO resultado = snapshotMapper.mapearParaEvolucaoCarteiraResponseDTO(snapshot);

        // Assert
        assertNotNull(resultado);
        assertEquals(dataAtual, resultado.data());
    }
}

