package com.cabeleleilaleila.demo.controller;

import com.cabeleleilaleila.demo.dto.CabeleireiroRequestDTO;
import com.cabeleleilaleila.demo.dto.CabeleireiroResponseDTO;
import com.cabeleleilaleila.demo.dto.CabeleireiroUpdateRequestDTO;
import com.cabeleleilaleila.demo.mapper.CabeleireiroMapper;
import com.cabeleleilaleila.demo.model.Cabeleireiro;
import com.cabeleleilaleila.demo.model.enums.EspecialidadeEnum;
import com.cabeleleilaleila.demo.service.CabeleireiroService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cabeleireiros")
@RequiredArgsConstructor
@Tag(name = "Cabeleireiro", description = "Endpoints para gerenciamento de cabeleireiros")
public class CabeleireiroController {

    private final CabeleireiroService cabeleireiroService;
    private final CabeleireiroMapper cabeleireiroMapper;

    @Operation(summary = "Cadastrar cabeleireiro", description = "Cadastra um novo cabeleireiro no sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Cabeleireiro cadastrado com sucesso"),
            @ApiResponse(responseCode = "409", description = "CPF ou email já cadastrado"),
            @ApiResponse(responseCode = "422", description = "Erro de validação nos campos")
    })
    @PostMapping
    public ResponseEntity<CabeleireiroResponseDTO> salvar(@RequestBody @Valid CabeleireiroRequestDTO dto) {
        Cabeleireiro cabeleireiro = cabeleireiroMapper.toEntity(dto);
        Cabeleireiro cabelereiroSalvo = cabeleireiroService.salvar(cabeleireiro);
        return ResponseEntity.status(HttpStatus.CREATED).body(cabeleireiroMapper.toResponseDTO(cabelereiroSalvo));
    }

    @Operation(summary = "Pesquisar cabeleireiros", description = "Pesquisa cabeleireiros com filtros opcionais")
    @ApiResponse(responseCode = "200", description = "Pesquisa realizada com sucesso")
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

    @Operation(summary = "Atualizar cabeleireiro", description = "Atualiza os dados de um cabeleireiro existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cabeleireiro atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Cabeleireiro não encontrado"),
            @ApiResponse(responseCode = "422", description = "Erro de validação nos campos")
    })
    @PutMapping("/{id}")
    public ResponseEntity<CabeleireiroResponseDTO> atualizar(
            @PathVariable Integer id,
            @RequestBody @Valid CabeleireiroUpdateRequestDTO dto) {
        Cabeleireiro cabeleireiroAtualizado = cabeleireiroService.atualizar(id, dto);
        return ResponseEntity.ok(cabeleireiroMapper.toResponseDTO(cabeleireiroAtualizado));
    }

    @Operation(summary = "Deletar cabeleireiro", description = "Remove um cabeleireiro do sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Cabeleireiro deletado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Cabeleireiro não encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Integer id) {
        cabeleireiroService.deletar(id);
        return ResponseEntity.noContent().build();
    }

}
