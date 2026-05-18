package com.cabeleleilaleila.demo.validator;

import com.cabeleleilaleila.demo.dto.AgendamentoRequestDTO;
import com.cabeleleilaleila.demo.exception.AgendamentoNaoEncontradoException;
import com.cabeleleilaleila.demo.exception.RegistroDuplicadoException;
import com.cabeleleilaleila.demo.model.Agendamento;
import com.cabeleleilaleila.demo.model.HorarioDisponivel;
import com.cabeleleilaleila.demo.model.enums.DiaSemanaEnum;
import com.cabeleleilaleila.demo.repository.AgendamentoRepository;
import com.cabeleleilaleila.demo.repository.HorarioDisponivelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

@Component
@RequiredArgsConstructor
public class AgendamentoValidator {

    private final AgendamentoRepository agendamentoRepository;
    private final HorarioDisponivelRepository horarioDisponivelRepository;

    public String validarSalvar(AgendamentoRequestDTO dto) {

        if (agendamentoRepository.existsByClienteIdAndDataAgendamento(
                dto.getClienteId(), dto.getDataAgendamento())) {
            throw new RegistroDuplicadoException("Já existe um agendamento para esse cliente nesta data.");
        }

        LocalDateTime dataInicio = dto.getDataAgendamento().minusDays(7);
        LocalDateTime dataFim = dto.getDataAgendamento().plusDays(7);

        List<Agendamento> agendamentosProximos = agendamentoRepository
                .findByClienteIdAndDataAgendamentoBetween(
                        dto.getClienteId(), dataInicio, dataFim);

        if (!agendamentosProximos.isEmpty()) {
            Agendamento agendamentoProximo = agendamentosProximos.get(0);
            LocalDateTime dataSugerida = agendamentoProximo.getDataAgendamento();

            // Se a data sugerida for depois da data solicitada, sugere adiantar
            if (dataSugerida.isAfter(dto.getDataAgendamento())) {
                return "Sugestão: você já possui um agendamento em "
                        + dataSugerida
                        + ". Deseja adiantar este novo agendamento para a mesma data?";
            }

            // Se a data sugerida for antes da data solicitada, sugere juntar
            return "Sugestão: você já possui um agendamento em "
                    + dataSugerida
                    + ". Deseja juntar este novo agendamento na mesma data?";
        }

        return null;
    }

    public Agendamento validarAtualizar(Integer id) {
        return agendamentoRepository.findById(id)
                .orElseThrow(() ->
                        new AgendamentoNaoEncontradoException("Agendamento não encontrado.")
                );
    }

    public void validarDeletar(Integer id) {
        agendamentoRepository.findById(id)
                .orElseThrow(() ->
                        new AgendamentoNaoEncontradoException("Agendamento não encontrado.")
                );
    }
}
