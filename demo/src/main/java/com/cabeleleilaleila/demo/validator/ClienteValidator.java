package com.cabeleleilaleila.demo.validator;

import com.cabeleleilaleila.demo.exception.ClienteCadastradoException;
import com.cabeleleilaleila.demo.exception.ClienteNaoEncontradoException;
import com.cabeleleilaleila.demo.exception.RegistroDuplicadoException;
import com.cabeleleilaleila.demo.model.Cliente;
import com.cabeleleilaleila.demo.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ClienteValidator {

    private final ClienteRepository clienteRepository;

    public void validarSalvar (Cliente cliente) {

        if (clienteRepository.existsByCpf(cliente.getCpf())) {
            throw new RegistroDuplicadoException("Este CPF ja esta cadastrado no sistema.");
        }
        if (clienteRepository.existsByEmail(cliente.getEmail())) {
            throw new RegistroDuplicadoException("Este e-mail ja esta cadastrado no sistema.");
        }
    }

    public Cliente validarAtualizar (Integer id) {
        return clienteRepository.findById(id)
                .orElseThrow(() ->
                        new ClienteNaoEncontradoException("Cliente nao encontrado")
                );
    }

    public Cliente validarDeletar (Integer id) {
        return clienteRepository.findById(id)
                .orElseThrow(() ->
                        new ClienteNaoEncontradoException("Cliente nao encontrado")
                );
    }

}
