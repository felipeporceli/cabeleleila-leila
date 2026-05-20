package com.cabeleleilaleila.demo.validator;

import com.cabeleleilaleila.demo.dto.ServicoRequestDTO;
import com.cabeleleilaleila.demo.exception.CampoInvalidoException;
import com.cabeleleilaleila.demo.exception.RegistroDuplicadoException;
import com.cabeleleilaleila.demo.exception.ServicoNaoEncontradoException;
import com.cabeleleilaleila.demo.model.Servico;
import com.cabeleleilaleila.demo.repository.ServicoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ServicoValidator {

    private final ServicoRepository servicoRepository;

    public void validarSalvar(ServicoRequestDTO dto) {
        if (servicoRepository.existsByNome(dto.getNome())) {
            throw new RegistroDuplicadoException("Já existe um serviço cadastrado com esse nome.");
        }
    }

    public Servico validarAtualizar(Integer id) {
        return servicoRepository.findById(id)
                .orElseThrow(() ->
                        new ServicoNaoEncontradoException("Serviço não encontrado."));
    }

    public void validarDeletar(Integer id) {
        servicoRepository.findById(id)
                .orElseThrow(() ->
                        new ServicoNaoEncontradoException("Serviço não encontrado."));
    }

}