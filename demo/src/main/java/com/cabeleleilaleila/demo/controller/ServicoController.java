package com.cabeleleilaleila.demo.controller;

import com.cabeleleilaleila.demo.dto.ServicoRequestDTO;
import com.cabeleleilaleila.demo.dto.ServicoResponseDTO;
import com.cabeleleilaleila.demo.dto.ServicoUpdateRequestDTO;
import com.cabeleleilaleila.demo.service.ServicoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
@RequestMapping("/servicos")
@RequiredArgsConstructor
@Tag(name = "Serviço", description = "Endpoints para gerenciamento de serviços do salão")
public class ServicoController {

    private final ServicoService servicoService;

    @Operation(summary = "Cadastrar serviço", description = "Cadastra um novo serviço no sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Serviço cadastrado com sucesso"),
            @ApiResponse(responseCode = "409", description = "Serviço já cadastrado com esse nome"),
            @ApiResponse(responseCode = "422", description = "Erro de validação nos campos")
    })
    @PostMapping
    public ResponseEntity<ServicoResponseDTO> salvar(@RequestBody @Valid ServicoRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(servicoService.salvar(dto));
    }

    @Operation(summary = "Pesquisar serviços", description = "Pesquisa serviços com filtros opcionais")
    @ApiResponse(responseCode = "200", description = "Pesquisa realizada com sucesso")
    @GetMapping
    public ResponseEntity<Page<ServicoResponseDTO>> pesquisar(
            @Parameter(description = "Nome do serviço") @RequestParam(value = "nome-servico", required = false) String nome,
            @Parameter(description = "Ativo") @RequestParam(value = "ativo", required = false) Boolean ativo,
            @Parameter(description = "Número da página") @RequestParam(value = "pagina", defaultValue = "0") Integer pagina,
            @Parameter(description = "Tamanho da página") @RequestParam(value = "tamanho-pagina", defaultValue = "10") Integer tamanhoPagina) {
        return ResponseEntity.ok(servicoService.pesquisar(nome, ativo, pagina, tamanhoPagina));
    }

    @Operation(summary = "Atualizar serviço", description = "Atualiza os dados de um serviço existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Serviço atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Serviço não encontrado"),
            @ApiResponse(responseCode = "422", description = "Erro de validação nos campos")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ServicoResponseDTO> atualizar(
            @Parameter(description = "ID do serviço") @PathVariable Integer id,
            @RequestBody @Valid ServicoUpdateRequestDTO dto) {
        return ResponseEntity.ok(servicoService.atualizar(id, dto));
    }

    @Operation(summary = "Deletar serviço", description = "Remove um serviço do sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Serviço deletado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Serviço não encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(
            @Parameter(description = "ID do serviço") @PathVariable Integer id) {
        servicoService.deletar(id);
        return ResponseEntity.noContent().build();
    }

}