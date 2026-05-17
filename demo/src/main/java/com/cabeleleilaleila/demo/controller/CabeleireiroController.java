package com.cabeleleilaleila.demo.controller;

import com.cabeleleilaleila.demo.dto.CabeleireiroRequestDTO;
import com.cabeleleilaleila.demo.dto.CabeleireiroResponseDTO;
import com.cabeleleilaleila.demo.dto.CabeleireiroUpdateRequestDTO;
import com.cabeleleilaleila.demo.mapper.CabeleireiroMapper;
import com.cabeleleilaleila.demo.model.Cabeleireiro;
import com.cabeleleilaleila.demo.model.enums.EspecialidadeEnum;
import com.cabeleleilaleila.demo.service.CabeleireiroService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cabeleireiros")
@RequiredArgsConstructor
public class CabeleireiroController {

    private final CabeleireiroService cabeleireiroService;
    private final CabeleireiroMapper cabeleireiroMapper;

    @PostMapping
    public ResponseEntity<CabeleireiroResponseDTO> salvar(@RequestBody @Valid CabeleireiroRequestDTO dto) {
        Cabeleireiro cabeleireiro = cabeleireiroMapper.toEntity(dto);
        Cabeleireiro cabelereiroSalvo = cabeleireiroService.salvar(cabeleireiro);
        return ResponseEntity.status(HttpStatus.CREATED).body(cabeleireiroMapper.toResponseDTO(cabelereiroSalvo));
    }

    @GetMapping
    public ResponseEntity<Page<CabeleireiroResponseDTO>> pesquisar(
            @RequestParam(value = "nome", required = false) String nome,
            @RequestParam(value = "cpf", required = false) String cpf,
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "telefone", required = false) String telefone,
            @RequestParam(value = "especialidade", required = false) EspecialidadeEnum especialidade,
            @RequestParam(value = "pagina", defaultValue = "0") Integer pagina,
            @RequestParam(value = "tamanho-pagina", defaultValue = "10") Integer tamanhoPagina) {

        Page<Cabeleireiro> cabeleireiros = cabeleireiroService.pesquisar(
                nome, cpf, email, telefone, especialidade, pagina, tamanhoPagina);
        return ResponseEntity.ok(cabeleireiros.map(cabeleireiroMapper::toResponseDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CabeleireiroResponseDTO> atualizar(
            @PathVariable Integer id,
            @RequestBody @Valid CabeleireiroUpdateRequestDTO dto) {
        Cabeleireiro cabeleireiroAtualizado = cabeleireiroService.atualizar(id, dto);
        return ResponseEntity.ok(cabeleireiroMapper.toResponseDTO(cabeleireiroAtualizado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Integer id) {
        cabeleireiroService.deletar(id);
        return ResponseEntity.noContent().build();
    }

}
