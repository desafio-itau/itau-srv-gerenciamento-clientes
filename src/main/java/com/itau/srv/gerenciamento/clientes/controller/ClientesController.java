package com.itau.srv.gerenciamento.clientes.controller;

import com.itau.common.library.generic.ControllerGenerico;
import com.itau.srv.gerenciamento.clientes.dto.adesao.AdesaoCancelamentoResponseDTO;
import com.itau.srv.gerenciamento.clientes.dto.adesao.AdesaoRequestDTO;
import com.itau.srv.gerenciamento.clientes.dto.adesao.AdesaoResponseDTO;
import com.itau.srv.gerenciamento.clientes.dto.carteira.CarteiraResponseDTO;
import com.itau.srv.gerenciamento.clientes.dto.rentabilidade.RentabilidadeResponseDTO;
import com.itau.srv.gerenciamento.clientes.dto.valormensal.AlterarValorMensalRequestDTO;
import com.itau.srv.gerenciamento.clientes.dto.valormensal.AlterarValorMensalResponseDTO;
import com.itau.srv.gerenciamento.clientes.service.CarteiraService;
import com.itau.srv.gerenciamento.clientes.service.ClienteService;
import com.itau.srv.gerenciamento.clientes.service.RentabilidadeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;


@RestController
@RequestMapping("api/clientes")
@RequiredArgsConstructor
@Slf4j
public class ClientesController implements ControllerGenerico {

    private final ClienteService clienteService;
    private final CarteiraService carteiraService;
    private final RentabilidadeService rentabilidadeService;

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

    @GetMapping("/{clienteId}/carteira")
    public ResponseEntity<CarteiraResponseDTO> buscarCarteira(@PathVariable Long clienteId) {
        log.info("Buscando carteira do cliente: {}", clienteId);

        return ResponseEntity.ok(carteiraService.consultarCarteiraCliente(clienteId));
    }

    @PostMapping("/carteiras-snapshots")
    public ResponseEntity<Void> gerarSnapshotCarteira(@RequestParam LocalDate data) {
        log.info("Gerando snapshot de carteiras de clientes para data: {}", data);

        carteiraService.gerarSnapshots(data);

        log.info("Snapshot de carteiras de clientes gerado com sucesso");

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{clienteId}/rentabilidade")
    public ResponseEntity<RentabilidadeResponseDTO> consultarRentabilidade(@PathVariable Long clienteId) {
        log.info("Consultando rentabilidade detalhada do cliente: {}", clienteId);

        return ResponseEntity.ok(rentabilidadeService.consultarRentabilidade(clienteId));
    }
}
