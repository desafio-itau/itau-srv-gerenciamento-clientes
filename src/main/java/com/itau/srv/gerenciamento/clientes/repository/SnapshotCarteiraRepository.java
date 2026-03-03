package com.itau.srv.gerenciamento.clientes.repository;

import com.itau.srv.gerenciamento.clientes.model.SnapshotCarteira;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SnapshotCarteiraRepository extends JpaRepository<SnapshotCarteira, Long> {
}
