package com.itau.srv.gerenciamento.clientes.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "custodias")
@Setter
@Getter
public class Custodia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "conta_grafica_id")
    private ContaGrafica contaGrafica;

    @Column(length = 10, nullable = false)
    private String ticker;

    @Column(nullable = false)
    private Integer quantidade;

    @Column(scale = 4, precision = 18, nullable = false)
    private BigDecimal precoMedio;

    @Column(nullable = false)
    private LocalDateTime dataUltimaAtualizacao;

    @PrePersist
    private void prePersist() {
        this.dataUltimaAtualizacao = LocalDateTime.now();
    }

    @PreUpdate
    private void preUpdate() {
        this.dataUltimaAtualizacao = LocalDateTime.now();
    }
}
