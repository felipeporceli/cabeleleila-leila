package com.cabeleleilaleila.demo.controller;

import com.cabeleleilaleila.demo.dto.AgendamentoRequestDTO;
import com.cabeleleilaleila.demo.dto.AgendamentoResponseDTO;
import com.cabeleleilaleila.demo.dto.AgendamentoUpdateRequestDTO;
import com.cabeleleilaleila.demo.service.AgendamentoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/agendamentos")
@RequiredArgsConstructor
@Tag(name = "Agendamento", description = "Endpoints para gerenciamento de agendamentos")
public class AgendamentoController {

    private final AgendamentoService agendamentoService;

    @Operation(summary = "Cadastrar agendamento", description = "Cadastra um novo agendamento no sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Agendamento cadastrado com sucesso"),
            @ApiResponse(responseCode = "409", description = "Já existe agendamento para esta data e cliente"),
            @ApiResponse(responseCode = "422", description = "Erro de validação nos campos")
    })
    @PostMapping
    public ResponseEntity<AgendamentoResponseDTO> salvar(@RequestBody @Valid AgendamentoRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(agendamentoService.salvar(dto));
    }

    @Operation(summary = "Pesquisar agendamentos", description = "Pesquisa agendamentos com filtros opcionais")
    @ApiResponse(responseCode = "200", description = "Pesquisa realizada com sucesso")
    @GetMapping
    public ResponseEntity<Page<AgendamentoResponseDTO>> pesquisar(
            @RequestParam(value = "cliente-id", required = false) Integer clienteId,
            @RequestParam(value = "cabeleireiro-id", required = false) Integer cabeleireiroId,
            @RequestParam(value = "data", required = false) @DateTimeFormat(pattern = "dd/MM/yyyy HH:mm") LocalDateTime dataAgendamento,
            @RequestParam(value = "pagina", defaultValue = "0") Integer pagina,
            @RequestParam(value = "tamanho-pagina", defaultValue = "10") Integer tamanhoPagina) {
        return ResponseEntity.ok(agendamentoService.pesquisar(
                clienteId, cabeleireiroId, dataAgendamento, pagina, tamanhoPagina));
    }

    @Operation(summary = "Atualizar agendamento", description = "Atualiza os dados de um agendamento existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Agendamento atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Agendamento não encontrado"),
            @ApiResponse(responseCode = "422", description = "Erro de validação nos campos")
    })
    @PutMapping("/{id}")
    public ResponseEntity<AgendamentoResponseDTO> atualizar(
            @Parameter(description = "ID do agendamento") @PathVariable Integer id,
            @RequestBody @Valid AgendamentoUpdateRequestDTO dto) {
        return ResponseEntity.ok(agendamentoService.atualizar(id, dto));
    }

    @Operation(summary = "Deletar agendamento", description = "Remove um agendamento do sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Agendamento deletado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Agendamento não encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Integer id) {
        agendamentoService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Histórico de agendamentos", description = "Retorna o histórico de agendamentos do cliente em um período")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Histórico retornado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    })
    @GetMapping("/historico")
    public ResponseEntity<List<AgendamentoResponseDTO>> buscarHistorico(
            @Parameter(description = "ID do cliente") @RequestParam(value = "cliente-id") Integer clienteId,
            @Parameter(description = "Data início (dd/MM/yyyy HH:mm)") @RequestParam(value = "data-inicio") @DateTimeFormat(pattern = "dd/MM/yyyy HH:mm") LocalDateTime dataInicio,
            @Parameter(description = "Data fim (dd/MM/yyyy HH:mm)") @RequestParam(value = "data-fim") @DateTimeFormat(pattern = "dd/MM/yyyy HH:mm") LocalDateTime dataFim) {
        return ResponseEntity.ok(agendamentoService.buscarHistorico(clienteId, dataInicio, dataFim));
    }

    @Operation(summary = "Confirmar agendamento", description = "Confirma um agendamento existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Agendamento confirmado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Agendamento não encontrado"),
            @ApiResponse(responseCode = "422", description = "Agendamento não pode ser confirmado")
    })
    @PatchMapping("/{id}/confirmar")
    public ResponseEntity<AgendamentoResponseDTO> confirmar(
            @Parameter(description = "ID do agendamento") @PathVariable Integer id) {
        return ResponseEntity.ok(agendamentoService.confirmar(id));
    }

    @Operation(summary = "Agendamentos do dia", description = "Retorna todos os agendamentos do cabeleireiro em um dia específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Agendamentos retornados com sucesso"),
            @ApiResponse(responseCode = "404", description = "Cabeleireiro não encontrado")
    })
    @GetMapping("/dia")
    public ResponseEntity<List<AgendamentoResponseDTO>> buscarAgendamentosDoDia(
            @Parameter(description = "ID do cabeleireiro") @RequestParam(value = "cabeleireiro-id") Integer cabeleireiroId,
            @Parameter(description = "Data (dd/MM/yyyy)") @RequestParam(value = "data") @DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate data) {
        return ResponseEntity.ok(agendamentoService.buscarAgendamentosDoDia(cabeleireiroId, data));
    }

}