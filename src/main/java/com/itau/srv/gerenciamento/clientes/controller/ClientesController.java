package com.itau.srv.gerenciamento.clientes.controller;

import com.itau.common.library.generic.ControllerGenerico;
import com.itau.srv.gerenciamento.clientes.dto.adesao.AdesaoCancelamentoResponseDTO;
import com.itau.srv.gerenciamento.clientes.dto.adesao.AdesaoRequestDTO;
import com.itau.srv.gerenciamento.clientes.dto.adesao.AdesaoResponseDTO;
import com.itau.srv.gerenciamento.clientes.dto.valormensal.AlterarValorMensalRequestDTO;
import com.itau.srv.gerenciamento.clientes.dto.valormensal.AlterarValorMensalResponseDTO;
import com.itau.srv.gerenciamento.clientes.service.ClienteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


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

    @PostMapping("/{clienteId}/saida")
    public ResponseEntity<AdesaoCancelamentoResponseDTO> cancelarAdesao(@PathVariable Long clienteId) {
        log.info("Cancelando adesao do cliente: {}", clienteId);


        return ResponseEntity
                .ok()
                .body(clienteService.cancelarAdesao(clienteId));
    }

    @PutMapping("/{clienteId}/valor-mensal")
    public ResponseEntity<AlterarValorMensalResponseDTO> atualizarValorMensal(
            @PathVariable Long clienteId,
            @RequestBody AlterarValorMensalRequestDTO novoValorMensal)
    {
        log.info("Atualizando valor mensal do cliente: {}", clienteId);

        return ResponseEntity
                .ok()
                .body(clienteService.alterarValorMensal(clienteId, novoValorMensal));
    }

    @GetMapping
    public ResponseEntity<List<AdesaoResponseDTO>> buscarClientesAtivos() {
        log.info("Buscando clientes ativos");

        return ResponseEntity
                .ok()
                .body(clienteService.buscarClientesAtivos());
    }
}
