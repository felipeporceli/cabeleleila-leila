package com.cabeleleilaleila.demo.validator;

import com.cabeleleilaleila.demo.dto.HorarioDisponivelRequestDTO;
import com.cabeleleilaleila.demo.exception.HorarioDisponivelNaoEncontradoException;
import com.cabeleleilaleila.demo.exception.RegistroDuplicadoException;
import com.cabeleleilaleila.demo.model.HorarioDisponivel;
import com.cabeleleilaleila.demo.repository.HorarioDisponivelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class HorarioDisponivelValidator {

    private final HorarioDisponivelRepository horarioDisponivelRepository;

    public void validarSalvar(HorarioDisponivelRequestDTO dto) {
        if (horarioDisponivelRepository.existsByCabeleireiroIdAndDiaSemana(
                dto.getCabeleireiroId(), dto.getDiaSemana())) {
            throw new RegistroDuplicadoException(
                    "Já existe um horário cadastrado para este cabeleireiro neste dia da semana.");
        }
    }

    public HorarioDisponivel validarAtualizar(Integer id) {
        return horarioDisponivelRepository.findById(id)
                .orElseThrow(() ->
                        new HorarioDisponivelNaoEncontradoException("Horário não encontrado.")
                );
    }

    public void validarDeletar(Integer id) {
        horarioDisponivelRepository.findById(id)
                .orElseThrow(() ->
                        new HorarioDisponivelNaoEncontradoException("Horário não encontrado.")
                );
    }

}
