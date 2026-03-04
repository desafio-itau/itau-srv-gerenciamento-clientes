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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Clientes", description = "API para gerenciamento de clientes e suas adesões ao produto de investimento")
public class ClientesController implements ControllerGenerico {

    private final ClienteService clienteService;
    private final CarteiraService carteiraService;
    private final RentabilidadeService rentabilidadeService;

    @Operation(
            summary = "Aderir ao produto de investimento",
            description = "Registra um novo cliente e sua adesão ao produto de investimento automático"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Cliente cadastrado com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AdesaoResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos no request", content = @Content),
            @ApiResponse(responseCode = "409", description = "Cliente já cadastrado", content = @Content)
    })
    @PostMapping("/adesao")
    public ResponseEntity<AdesaoResponseDTO> aderirAoProduto(
            @Parameter(description = "Dados do cliente para adesão", required = true)
            @RequestBody @Valid AdesaoRequestDTO dto) {
        log.info("Aderindo cliente ao produto: {}", dto);

        AdesaoResponseDTO adesaoResponseDTO = clienteService.aderirAoProduto(dto);

        log.info("Cliente aderido ao produto: {}", adesaoResponseDTO.nome());

        return ResponseEntity
                .created(gerarHeaderLocation(adesaoResponseDTO.clienteId())).
                body(adesaoResponseDTO);
    }

    @Operation(
            summary = "Cancelar adesão do cliente",
            description = "Cancela a adesão de um cliente ao produto de investimento"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Adesão cancelada com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AdesaoCancelamentoResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado", content = @Content)
    })
    @PostMapping("/{clienteId}/saida")
    public ResponseEntity<AdesaoCancelamentoResponseDTO> cancelarAdesao(
            @Parameter(description = "ID do cliente", required = true)
            @PathVariable Long clienteId) {
        log.info("Cancelando adesao do cliente: {}", clienteId);


        return ResponseEntity
                .ok()
                .body(clienteService.cancelarAdesao(clienteId));
    }

    @Operation(
            summary = "Atualizar valor mensal de investimento",
            description = "Altera o valor mensal que o cliente deseja investir"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Valor mensal atualizado com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AlterarValorMensalResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Valor inválido", content = @Content),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado", content = @Content)
    })
    @PutMapping("/{clienteId}/valor-mensal")
    public ResponseEntity<AlterarValorMensalResponseDTO> atualizarValorMensal(
            @Parameter(description = "ID do cliente", required = true)
            @PathVariable Long clienteId,
            @Parameter(description = "Novo valor mensal de investimento", required = true)
            @RequestBody AlterarValorMensalRequestDTO novoValorMensal)
    {
        log.info("Atualizando valor mensal do cliente: {}", clienteId);

        return ResponseEntity
                .ok()
                .body(clienteService.alterarValorMensal(clienteId, novoValorMensal));
    }

    @Operation(
            summary = "Listar clientes ativos",
            description = "Retorna a lista de todos os clientes ativos no sistema"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de clientes retornada com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AdesaoResponseDTO.class)))
    })
    @GetMapping
    public ResponseEntity<List<AdesaoResponseDTO>> buscarClientesAtivos() {
        log.info("Buscando clientes ativos");

        return ResponseEntity
                .ok()
                .body(clienteService.buscarClientesAtivos());
    }

    @Operation(
            summary = "Consultar carteira do cliente",
            description = "Retorna os detalhes da carteira de investimentos do cliente, incluindo ativos, valores e rentabilidade"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Carteira retornada com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CarteiraResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado", content = @Content)
    })
    @GetMapping("/{clienteId}/carteira")
    public ResponseEntity<CarteiraResponseDTO> buscarCarteira(
            @Parameter(description = "ID do cliente", required = true)
            @PathVariable Long clienteId) {
        log.info("Buscando carteira do cliente: {}", clienteId);

        return ResponseEntity.ok(carteiraService.consultarCarteiraCliente(clienteId));
    }

    @Operation(
            summary = "Gerar snapshot das carteiras",
            description = "Gera um snapshot do estado das carteiras de todos os clientes em uma data específica"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Snapshot gerado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Data inválida", content = @Content)
    })
    @PostMapping("/carteiras-snapshots")
    public ResponseEntity<Void> gerarSnapshotCarteira(
            @Parameter(description = "Data para o snapshot", required = true)
            @RequestParam LocalDate data) {
        log.info("Gerando snapshot de carteiras de clientes para data: {}", data);

        carteiraService.gerarSnapshots(data);

        log.info("Snapshot de carteiras de clientes gerado com sucesso");

        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Consultar rentabilidade detalhada",
            description = "Retorna informações detalhadas sobre a rentabilidade do cliente, incluindo histórico de aportes e evolução da carteira"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rentabilidade retornada com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = RentabilidadeResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado", content = @Content)
    })
    @GetMapping("/{clienteId}/rentabilidade")
    public ResponseEntity<RentabilidadeResponseDTO> consultarRentabilidade(
            @Parameter(description = "ID do cliente", required = true)
            @PathVariable Long clienteId) {
        log.info("Consultando rentabilidade detalhada do cliente: {}", clienteId);

        return ResponseEntity.ok(rentabilidadeService.consultarRentabilidade(clienteId));
    }
}
