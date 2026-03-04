package com.itau.srv.gerenciamento.clientes.repository;

import com.itau.srv.gerenciamento.clientes.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    @Query("SELECT c FROM Cliente c WHERE c.id = :id AND c.ativo = TRUE")
    Optional<Cliente> findByIdAndAtivo(Long id);

    @Query("SELECT c FROM Cliente c WHERE c.cpf = :cpf AND c.ativo = TRUE")
    Optional<Cliente> findByCpfAndAtivo(String cpf);

    @Query("SELECT c FROM Cliente c WHERE c.ativo = TRUE")
    List<Cliente> findAllAtivos();
}
