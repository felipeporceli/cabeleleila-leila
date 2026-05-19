package com.cabeleleilaleila.demo.controller;

import com.cabeleleilaleila.demo.dto.ClienteRequestDTO;
import com.cabeleleilaleila.demo.dto.ClienteResponseDTO;
import com.cabeleleilaleila.demo.mapper.ClienteMapper;
import com.cabeleleilaleila.demo.model.Cliente;
import com.cabeleleilaleila.demo.service.ClienteService;
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
@RequestMapping("/clientes")
@RequiredArgsConstructor
@Tag(name = "Cliente", description = "Endpoints para gerenciamento de clientes")
public class ClienteController {

    private final ClienteService clienteService;
    private final ClienteMapper clienteMapper;

    @Operation(summary = "Cadastrar cliente", description = "Cadastra um novo cliente no sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Cliente cadastrado com sucesso"),
            @ApiResponse(responseCode = "409", description = "CPF ou email já cadastrado"),
            @ApiResponse(responseCode = "422", description = "Erro de validação nos campos")
    })

    @PostMapping
    public ResponseEntity<ClienteResponseDTO> salvar(@RequestBody @Valid ClienteRequestDTO dto) {
        Cliente cliente = clienteMapper.toEntity(dto);
        Cliente clienteSalvo = clienteService.salvar(cliente);
        return ResponseEntity.status(HttpStatus.CREATED).body(clienteMapper.toResponseDTO(clienteSalvo));
    }

    @Operation(summary = "Pesquisar clientes", description = "Pesquisa clientes com filtros opcionais")
    @ApiResponse(responseCode = "200", description = "Pesquisa realizada com sucesso")

    @GetMapping
    public ResponseEntity<Page<ClienteResponseDTO>> pesquisar(
            @RequestParam(value = "nome", required = false) String nome,
            @RequestParam(value = "cpf", required = false) String cpf,
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "telefone", required = false) String telefone,
            @RequestParam(value = "cep", required = false) String cep,
            @RequestParam(value = "pagina", defaultValue = "0") Integer pagina,
            @RequestParam(value = "tamanho-pagina", defaultValue = "10") Integer tamanhoPagina) {

        Page<Cliente> clientes = clienteService.pesquisar(
                nome, cpf, email, telefone, cep, pagina, tamanhoPagina);
        return ResponseEntity.ok(clientes.map(clienteMapper::toResponseDTO));
    }

    @Operation(summary = "Atualizar cliente", description = "Atualiza os dados de um cliente existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cliente atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado"),
            @ApiResponse(responseCode = "422", description = "Erro de validação nos campos")
    })

    @PutMapping("/{id}")
    public ResponseEntity<ClienteResponseDTO> atualizar(
            @PathVariable Integer id,
            @RequestBody ClienteRequestDTO dto) {
        Cliente clienteAtualizado = clienteService.atualizar(id, dto);
        return ResponseEntity.ok(clienteMapper.toResponseDTO(clienteAtualizado));
    }

    @Operation(summary = "Deletar cliente", description = "Remove um cliente do sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Cliente deletado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Integer id) {
        clienteService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
