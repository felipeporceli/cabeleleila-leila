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
public class PagamentoController {

    private final PagamentoService pagamentoService;
    private final PagamentoMapper pagamentoMapper;

    @PostMapping
    public ResponseEntity<PagamentoResponseDTO> salvar(@RequestBody @Valid PagamentoRequestDTO dto) {
        Pagamento pagamentoSalvo = pagamentoService.salvar(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(pagamentoMapper.toResponseDTO(pagamentoSalvo));
    }

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

    @GetMapping("/relatorio")
    public ResponseEntity<RelatorioFaturamentoDTO> gerarRelatorio(
            @RequestParam Integer cabeleireiroId,
            @RequestParam @DateTimeFormat(pattern = "dd/MM/yyyy HH:mm") LocalDateTime dataInicio,
            @RequestParam @DateTimeFormat(pattern = "dd/MM/yyyy HH:mm") LocalDateTime dataFim) {
        RelatorioFaturamentoDTO relatorio = pagamentoService
                .gerarRelatorioFaturamento(cabeleireiroId, dataInicio, dataFim);
        return ResponseEntity.ok(relatorio);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PagamentoResponseDTO> atualizar(
            @PathVariable Integer id,
            @RequestBody @Valid PagamentoUpdateRequestDTO dto) {
        Pagamento pagamentoAtualizado = pagamentoService.atualizar(id, dto);
        return ResponseEntity.ok(pagamentoMapper.toResponseDTO(pagamentoAtualizado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Integer id) {
        pagamentoService.deletar(id);
        return ResponseEntity.noContent().build();
    }

}