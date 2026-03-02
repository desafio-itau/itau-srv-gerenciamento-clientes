package com.itau.srv.gerenciamento.clientes.feign;

import com.itau.srv.gerenciamento.clientes.dto.custodia.CustodiaResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "${external-endpoints.itau-srv-custodias.name}", url = "${external-endpoints.itau-srv-custodias.url}")
public interface CustodiasFeignClient {

    @GetMapping("/{clienteId}")
    List<CustodiaResponseDTO> obterCustodiasPorClienteId(@PathVariable Long clienteId);
}
