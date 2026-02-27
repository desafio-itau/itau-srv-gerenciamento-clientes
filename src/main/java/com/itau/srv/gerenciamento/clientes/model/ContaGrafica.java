package com.itau.srv.gerenciamento.clientes.model;

import com.itau.srv.gerenciamento.clientes.model.enums.TipoConta;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "contas_graficas")
@Setter
@Getter
public class ContaGrafica {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    @Column(length = 20, unique = true)
    private String numeroConta;

    @Enumerated(EnumType.STRING)
    private TipoConta tipo = TipoConta.FILHOTE;

    @Column(nullable = false)
    private LocalDateTime dataCriacao;

    @PrePersist
    private void prePersist() {
        this.dataCriacao = LocalDateTime.now();
    }
}
