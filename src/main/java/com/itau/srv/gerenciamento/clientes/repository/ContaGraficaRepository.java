package com.itau.srv.gerenciamento.clientes.repository;

import com.itau.srv.gerenciamento.clientes.model.Cliente;
import com.itau.srv.gerenciamento.clientes.model.ContaGrafica;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContaGraficaRepository extends JpaRepository<ContaGrafica, Long> {
    ContaGrafica findByCliente(Cliente cliente);
}
