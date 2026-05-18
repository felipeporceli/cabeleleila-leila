package com.cabeleleilaleila.demo.controller;

import com.cabeleleilaleila.demo.dto.HorarioDisponivelRequestDTO;
import com.cabeleleilaleila.demo.dto.HorarioDisponivelResponseDTO;
import com.cabeleleilaleila.demo.dto.HorarioDisponivelUpdateRequestDTO;
import com.cabeleleilaleila.demo.mapper.HorarioDisponivelMapper;
import com.cabeleleilaleila.demo.model.HorarioDisponivel;
import com.cabeleleilaleila.demo.model.enums.DiaSemanaEnum;
import com.cabeleleilaleila.demo.service.HorarioDisponivelService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/horarios-disponiveis")
@RequiredArgsConstructor
public class HorarioDisponivelController {

    private final HorarioDisponivelService horarioDisponivelService;
    private final HorarioDisponivelMapper horarioDisponivelMapper;

    @PostMapping
    public ResponseEntity<HorarioDisponivelResponseDTO> salvar(
            @RequestBody @Valid HorarioDisponivelRequestDTO dto) {
        HorarioDisponivel horarioSalvo = horarioDisponivelService.salvar(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(horarioDisponivelMapper.toResponseDTO(horarioSalvo));
    }

    @GetMapping
    public ResponseEntity<Page<HorarioDisponivelResponseDTO>> pesquisar(
            @RequestParam(required = false) Integer cabeleireiroId,
            @RequestParam(required = false) DiaSemanaEnum diaSemana,
            @RequestParam(required = false) Boolean ativo,
            @RequestParam(defaultValue = "0") Integer pagina,
            @RequestParam(defaultValue = "10") Integer tamanhoPagina) {

        Page<HorarioDisponivel> horarios = horarioDisponivelService.pesquisar(
                cabeleireiroId, diaSemana, ativo, pagina, tamanhoPagina);
        return ResponseEntity.ok(horarios.map(horarioDisponivelMapper::toResponseDTO));
    }

    @GetMapping("/cabeleireiro/{cabeleireiroId}/dia/{diaSemana}")
    public ResponseEntity<List<HorarioDisponivelResponseDTO>> buscarPorDia(
            @PathVariable Integer cabeleireiroId,
            @PathVariable DiaSemanaEnum diaSemana) {
        List<HorarioDisponivel> horarios = horarioDisponivelService
                .buscarHorariosDisponiveisPorDia(cabeleireiroId, diaSemana);
        return ResponseEntity.ok(horarios.stream()
                .map(horarioDisponivelMapper::toResponseDTO)
                .toList());
    }

    @PutMapping("/{id}")
    public ResponseEntity<HorarioDisponivelResponseDTO> atualizar(
            @PathVariable Integer id,
            @RequestBody @Valid HorarioDisponivelUpdateRequestDTO dto) {
        HorarioDisponivel horarioAtualizado = horarioDisponivelService.atualizar(id, dto);
        return ResponseEntity.ok(horarioDisponivelMapper.toResponseDTO(horarioAtualizado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Integer id) {
        horarioDisponivelService.deletar(id);
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/cabeleireiro/{cabeleireiroId}/data")
    public ResponseEntity<List<String>> buscarHorariosDisponiveisPorData(
            @PathVariable Integer cabeleireiroId,
            @RequestParam @DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate data) {

        List<String> horarios = horarioDisponivelService
                .buscarHorariosDisponiveisPorData(cabeleireiroId, data.atStartOfDay())
                .stream()
                .map(h -> h.format(DateTimeFormatter.ofPattern("HH:mm")))
                .toList();

        return ResponseEntity.ok(horarios);
    }

}
