package com.cabeleleilaleila.demo.controller;

import com.cabeleleilaleila.demo.dto.RelatorioSemanalDTO;
import com.cabeleleilaleila.demo.service.RelatorioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/relatorios")
@RequiredArgsConstructor
@Tag(name = "Relatório", description = "Endpoints para relatórios gerenciais")
public class RelatorioController {

    private final RelatorioService relatorioService;

    @Operation(summary = "Relatório semanal", description = "Gera o relatório de desempenho semanal do salão")
    @ApiResponse(responseCode = "200", description = "Relatório gerado com sucesso")
    @GetMapping("/semanal")
    public ResponseEntity<RelatorioSemanalDTO> gerarRelatorioSemanal(
            @Parameter(description = "Data início (dd/MM/yyyy HH:mm)") @RequestParam (value = "data-inicio")@DateTimeFormat(pattern = "dd/MM/yyyy HH:mm") LocalDateTime dataInicio,
            @Parameter(description = "Data fim (dd/MM/yyyy HH:mm)") @RequestParam (value = "data-fim") @DateTimeFormat(pattern = "dd/MM/yyyy HH:mm") LocalDateTime dataFim) {
        return ResponseEntity.ok(relatorioService.gerarRelatorioSemanal(dataInicio, dataFim));
    }

}