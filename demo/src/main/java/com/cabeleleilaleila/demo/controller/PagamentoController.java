package com.cabeleleilaleila.demo.controller;

import com.cabeleleilaleila.demo.dto.PagamentoRequestDTO;
import com.cabeleleilaleila.demo.dto.PagamentoResponseDTO;
import com.cabeleleilaleila.demo.dto.PagamentoUpdateRequestDTO;
import com.cabeleleilaleila.demo.dto.RelatorioFaturamentoDTO;
import com.cabeleleilaleila.demo.mapper.PagamentoMapper;
import com.cabeleleilaleila.demo.model.Pagamento;
import com.cabeleleilaleila.demo.model.enums.FormaPagamentoEnum;
import com.cabeleleilaleila.demo.model.enums.StatusPagamentoEnum;
import com.cabeleleilaleila.demo.service.PagamentoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/pagamentos")
@RequiredArgsConstructor
@Tag(name = "Pagamento", description = "Endpoints para gerenciamento de pagamentos e relatórios")
public class PagamentoController {

    private final PagamentoService pagamentoService;
    private final PagamentoMapper pagamentoMapper;

    @Operation(summary = "Cadastrar pagamento", description = "Cadastra um novo pagamento para um agendamento")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Pagamento cadastrado com sucesso"),
            @ApiResponse(responseCode = "409", description = "Já existe pagamento para este agendamento"),
            @ApiResponse(responseCode = "422", description = "Erro de validação nos campos")
    })
    @PostMapping
    public ResponseEntity<PagamentoResponseDTO> salvar(@RequestBody @Valid PagamentoRequestDTO dto) {
        Pagamento pagamentoSalvo = pagamentoService.salvar(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(pagamentoMapper.toResponseDTO(pagamentoSalvo));
    }

    @Operation(summary = "Pesquisar pagamentos", description = "Pesquisa pagamentos com filtros opcionais")
    @ApiResponse(responseCode = "200", description = "Pesquisa realizada com sucesso")
    @GetMapping
    public ResponseEntity<Page<PagamentoResponseDTO>> pesquisar(
            @RequestParam(value = "agendamento-id", required = false) Integer agendamentoId,
            @RequestParam(value = "status-pagamento", required = false) StatusPagamentoEnum statusPagamento,
            @RequestParam(value = "forma-pagamento", required = false) FormaPagamentoEnum formaPagamento,
            @RequestParam(value = "pagina", defaultValue = "0") Integer pagina,
            @RequestParam(value = "tamanho-pagina", defaultValue = "10") Integer tamanhoPagina) {

        Page<Pagamento> pagamentos = pagamentoService.pesquisar(
                agendamentoId, statusPagamento, formaPagamento, pagina, tamanhoPagina);
        return ResponseEntity.ok(pagamentos.map(pagamentoMapper::toResponseDTO));
    }

    @Operation(summary = "Relatório de faturamento", description = "Gera relatório de faturamento do cabeleireiro por período")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Relatório gerado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Cabeleireiro não encontrado")
    })
    @GetMapping("/relatorio")
    public ResponseEntity<RelatorioFaturamentoDTO> gerarRelatorio(
            @RequestParam Integer cabeleireiroId,
            @RequestParam @DateTimeFormat(pattern = "dd/MM/yyyy HH:mm") LocalDateTime dataInicio,
            @RequestParam @DateTimeFormat(pattern = "dd/MM/yyyy HH:mm") LocalDateTime dataFim) {
        RelatorioFaturamentoDTO relatorio = pagamentoService
                .gerarRelatorioFaturamento(cabeleireiroId, dataInicio, dataFim);
        return ResponseEntity.ok(relatorio);
    }

    @Operation(summary = "Atualizar pagamento", description = "Atualiza os dados de um pagamento existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pagamento atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Pagamento não encontrado"),
            @ApiResponse(responseCode = "422", description = "Erro de validação nos campos")
    })
    @PutMapping("/{id}")
    public ResponseEntity<PagamentoResponseDTO> atualizar(
            @PathVariable Integer id,
            @RequestBody @Valid PagamentoUpdateRequestDTO dto) {
        Pagamento pagamentoAtualizado = pagamentoService.atualizar(id, dto);
        return ResponseEntity.ok(pagamentoMapper.toResponseDTO(pagamentoAtualizado));
    }

    @Operation(summary = "Deletar pagamento", description = "Remove um pagamento do sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Pagamento deletado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Pagamento não encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Integer id) {
        pagamentoService.deletar(id);
        return ResponseEntity.noContent().build();
    }

}