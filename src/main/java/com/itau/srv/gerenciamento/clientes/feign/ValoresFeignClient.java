package com.itau.srv.gerenciamento.clientes.feign;

import com.itau.srv.gerenciamento.clientes.dto.valor.ValoresPorDataResponseDTO;
import com.itau.srv.gerenciamento.clientes.dto.valor.ValoresResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

@FeignClient(name = "${external-endpoints.itau-srv-valores.name}", url = "${external-endpoints.itau-srv-valores.url}")
public interface ValoresFeignClient {

    @GetMapping("/{clienteId}")
    ValoresResponseDTO obterValoresPorCliente(@PathVariable Long clienteId);

    @GetMapping
    ValoresPorDataResponseDTO obterValoresPorClienteEData(
            @RequestParam(name = "clienteId") Long clienteId,
            @RequestParam(name = "data") LocalDate data
    );
}
