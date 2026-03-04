package com.itau.srv.gerenciamento.clientes.controller;

import com.itau.srv.gerenciamento.clientes.dto.contagrafica.ContaGraficaResponseDTO;
import com.itau.srv.gerenciamento.clientes.service.ContaGraficaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/contas-graficas")
@RequiredArgsConstructor
@Slf4j
public class ContaGraficaController {

    private final ContaGraficaService contaGraficaService;

    @GetMapping("/{id}")
    public ResponseEntity<ContaGraficaResponseDTO> buscarConta(@PathVariable Long id) {
        log.info("Buscando conta grafica: {}", id);

        return ResponseEntity.ok(contaGraficaService.buscarConta(id));
    }
}
