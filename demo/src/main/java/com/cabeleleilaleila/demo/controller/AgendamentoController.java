package com.cabeleleilaleila.demo.controller;

import com.cabeleleilaleila.demo.dto.AgendamentoRequestDTO;
import com.cabeleleilaleila.demo.dto.AgendamentoResponseDTO;
import com.cabeleleilaleila.demo.dto.AgendamentoUpdateRequestDTO;
import com.cabeleleilaleila.demo.service.AgendamentoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/agendamentos")
@RequiredArgsConstructor
public class AgendamentoController {

    private final AgendamentoService agendamentoService;

    @PostMapping
    public ResponseEntity<AgendamentoResponseDTO> salvar(@RequestBody @Valid AgendamentoRequestDTO dto) {
        AgendamentoResponseDTO response = agendamentoService.salvar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<Page<AgendamentoResponseDTO>> pesquisar(
            @RequestParam(value = "cliente-id", required = false) Integer clienteId,
            @RequestParam(value = "cabeleireiro-id", required = false) Integer cabeleireiroId,
            @RequestParam(value = "data", required = false) @DateTimeFormat(pattern = "dd/MM/yyyy HH:mm") LocalDateTime dataAgendamento,
            @RequestParam(value = "pagina", defaultValue = "0") Integer pagina,
            @RequestParam(value = "tamanho-pagina", defaultValue = "10") Integer tamanhoPagina) {

        Page<AgendamentoResponseDTO> agendamentos = agendamentoService.pesquisar(
                clienteId, cabeleireiroId, dataAgendamento, pagina, tamanhoPagina);
        return ResponseEntity.ok(agendamentos);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Integer id) {
        agendamentoService.deletar(id);
        return ResponseEntity.noContent().build();
    }

}
