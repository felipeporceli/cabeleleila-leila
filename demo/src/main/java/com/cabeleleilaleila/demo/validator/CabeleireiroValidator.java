package com.cabeleleilaleila.demo.validator;

import com.cabeleleilaleila.demo.exception.CabeleireiroNaoEncontradoException;
import com.cabeleleilaleila.demo.exception.RegistroDuplicadoException;
import com.cabeleleilaleila.demo.model.Cabeleireiro;
import com.cabeleleilaleila.demo.repository.CabeleireiroRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CabeleireiroValidator {

    private final CabeleireiroRepository CabeleireiroRepository;

    public void validarSalvar (Cabeleireiro Cabeleireiro) {

        if (CabeleireiroRepository.existsByCpf(Cabeleireiro.getCpf())) {
            throw new RegistroDuplicadoException("Este CPF ja esta cadastrado no sistema.");
        }
        if (CabeleireiroRepository.existsByEmail(Cabeleireiro.getEmail())) {
            throw new RegistroDuplicadoException("Este e-mail ja esta cadastrado no sistema.");
        }
    }

    public Cabeleireiro validarAtualizar (Integer id) {
        return CabeleireiroRepository.findById(id)
                .orElseThrow(() ->
                        new CabeleireiroNaoEncontradoException("Cabeleireiro nao encontrado")
                );
    }

    public Cabeleireiro validarDeletar (Integer id) {
        return CabeleireiroRepository.findById(id)
                .orElseThrow(() ->
                        new CabeleireiroNaoEncontradoException("Cabeleireiro nao encontrado")
                );
    }
}
