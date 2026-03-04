package com.itau.srv.gerenciamento.clientes.mapper;

import com.itau.srv.gerenciamento.clientes.dto.rentabilidade.EvolucaoCarteiraResponseDTO;
import com.itau.srv.gerenciamento.clientes.model.SnapshotCarteira;
import org.springframework.stereotype.Component;

@Component
public class SnapshotMapper {

    public EvolucaoCarteiraResponseDTO mapearParaEvolucaoCarteiraResponseDTO(SnapshotCarteira snapshot) {
        return new EvolucaoCarteiraResponseDTO(
                snapshot.getDataSnapshot(),
                snapshot.getValorCarteira(),
                snapshot.getValorInvestido(),
                snapshot.getRentabilidade()
        );
    }
}
