package com.itau.srv.gerenciamento.clientes.repository;

import com.itau.srv.gerenciamento.clientes.model.SnapshotCarteira;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SnapshotCarteiraRepository extends JpaRepository<SnapshotCarteira, Long> {
    @Query("SELECT s FROM SnapshotCarteira s WHERE s.clienteId = :clienteId ORDER BY s.dataSnapshot DESC")
    List<SnapshotCarteira> findAllByClienteId(Long clienteId);
}
