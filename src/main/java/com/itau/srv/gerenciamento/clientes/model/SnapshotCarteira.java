package com.itau.srv.gerenciamento.clientes.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "snapshots_carteiras")
@Setter
@Getter
public class SnapshotCarteira {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "cliente_id", nullable = false)
    private Long clienteId;

    @Column(name = "data_snapshot", nullable = false)
    private LocalDate dataSnapshot;

    @Column(name = "valor_carteira", nullable = false, precision = 18, scale = 2)
    private BigDecimal valorCarteira;

    @Column(name = "valor_investido", nullable = false, precision = 18, scale = 2)
    private BigDecimal valorInvestido;

    @Column(name = "rentabilidade", nullable = false, precision = 4, scale = 2)
    private BigDecimal rentabilidade;
}
