package com.cabeleleilaleila.demo.controller;

import com.cabeleleilaleila.demo.dto.HorarioDisponivelRequestDTO;
import com.cabeleleilaleila.demo.dto.HorarioDisponivelResponseDTO;
import com.cabeleleilaleila.demo.dto.HorarioDisponivelUpdateRequestDTO;
import com.cabeleleilaleila.demo.mapper.HorarioDisponivelMapper;
import com.cabeleleilaleila.demo.model.HorarioDisponivel;
import com.cabeleleilaleila.demo.model.enums.DiaSemanaEnum;
import com.cabeleleilaleila.demo.service.HorarioDisponivelService;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/horarios-disponiveis")
@RequiredArgsConstructor
@Tag(name = "Horário Disponível", description = "Endpoints para gerenciamento de horários disponíveis dos cabeleireiros")
public class HorarioDisponivelController {

    private final HorarioDisponivelService horarioDisponivelService;
    private final HorarioDisponivelMapper horarioDisponivelMapper;

    @Operation(summary = "Cadastrar horário", description = "Cadastra um novo horário disponível para um cabeleireiro")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Horário cadastrado com sucesso"),
            @ApiResponse(responseCode = "409", description = "Já existe horário cadastrado para este dia"),
            @ApiResponse(responseCode = "422", description = "Erro de validação nos campos")
    })
    @PostMapping
    public ResponseEntity<HorarioDisponivelResponseDTO> salvar(
            @RequestBody @Valid HorarioDisponivelRequestDTO dto) {
        HorarioDisponivel horarioSalvo = horarioDisponivelService.salvar(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(horarioDisponivelMapper.toResponseDTO(horarioSalvo));
    }

    @Operation(summary = "Pesquisar horários", description = "Pesquisa horários disponíveis com filtros opcionais")
    @ApiResponse(responseCode = "200", description = "Pesquisa realizada com sucesso")
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

    @Operation(summary = "Atualizar horário", description = "Atualiza os dados de um horário disponível")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Horário atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Horário não encontrado"),
            @ApiResponse(responseCode = "422", description = "Erro de validação nos campos")
    })
    @PutMapping("/{id}")
    public ResponseEntity<HorarioDisponivelResponseDTO> atualizar(
            @PathVariable Integer id,
            @RequestBody @Valid HorarioDisponivelUpdateRequestDTO dto) {
        HorarioDisponivel horarioAtualizado = horarioDisponivelService.atualizar(id, dto);
        return ResponseEntity.ok(horarioDisponivelMapper.toResponseDTO(horarioAtualizado));
    }

    @Operation(summary = "Deletar horário", description = "Remove um horário disponível do sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Horário deletado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Horário não encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Integer id) {
        horarioDisponivelService.deletar(id);
        return ResponseEntity.noContent().build();
    }


    @Operation(summary = "Buscar horários por data", description = "Retorna os horários disponíveis de um cabeleireiro em uma data específica")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Horários retornados com sucesso"),
            @ApiResponse(responseCode = "404", description = "Cabeleireiro não encontrado")
    })
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
