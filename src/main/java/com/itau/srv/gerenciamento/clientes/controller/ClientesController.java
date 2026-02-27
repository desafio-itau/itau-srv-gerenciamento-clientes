package com.itau.srv.gerenciamento.clientes.controller;

import com.itau.common.library.generic.ControllerGenerico;
import com.itau.srv.gerenciamento.clientes.dto.adesao.AdesaoRequestDTO;
import com.itau.srv.gerenciamento.clientes.dto.adesao.AdesaoResponseDTO;
import com.itau.srv.gerenciamento.clientes.service.ClienteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/clientes")
@RequiredArgsConstructor
@Slf4j
public class ClientesController implements ControllerGenerico {

    private final ClienteService clienteService;

    @PostMapping("/adesao")
    public ResponseEntity<AdesaoResponseDTO> aderirAoProduto(@RequestBody @Valid AdesaoRequestDTO dto) {
        log.info("Aderindo cliente ao produto: {}", dto);

        AdesaoResponseDTO adesaoResponseDTO = clienteService.aderirAoProduto(dto);

        log.info("Cliente aderido ao produto: {}", adesaoResponseDTO.nome());

        return ResponseEntity
                .created(gerarHeaderLocation(adesaoResponseDTO.clienteId())).
                body(adesaoResponseDTO);
    }
}
