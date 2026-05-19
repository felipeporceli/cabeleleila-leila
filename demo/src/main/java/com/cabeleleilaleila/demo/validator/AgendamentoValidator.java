package com.cabeleleilaleila.demo.validator;

import com.cabeleleilaleila.demo.dto.AgendamentoRequestDTO;
import com.cabeleleilaleila.demo.dto.AgendamentoUpdateRequestDTO;
import com.cabeleleilaleila.demo.exception.AgendamentoNaoEncontradoException;
import com.cabeleleilaleila.demo.exception.AlteracaoNaoPermitidaException;
import com.cabeleleilaleila.demo.exception.CampoInvalidoException;
import com.cabeleleilaleila.demo.exception.RegistroDuplicadoException;
import com.cabeleleilaleila.demo.model.Agendamento;
import com.cabeleleilaleila.demo.model.HorarioDisponivel;
import com.cabeleleilaleila.demo.model.enums.DiaSemanaEnum;
import com.cabeleleilaleila.demo.repository.AgendamentoRepository;
import com.cabeleleilaleila.demo.repository.HorarioDisponivelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
@RequiredArgsConstructor
public class AgendamentoValidator {

    private final AgendamentoRepository agendamentoRepository;
    private final HorarioDisponivelRepository horarioDisponivelRepository;

    public String validarSalvar(AgendamentoRequestDTO dto) {

        // 1. Verifica se já existe agendamento para o mesmo cliente na mesma data
        if (agendamentoRepository.existsByClienteIdAndDataAgendamento(
                dto.getClienteId(), dto.getDataAgendamento())) {
            throw new RegistroDuplicadoException("Já existe um agendamento para esse cliente nesta data.");
        }

        // 2. Valida horário do cabeleireiro
        validarHorarioCabeleireiro(dto.getCabeleireiroId(), dto.getDataAgendamento(), null);

        // 3. Verifica se o horário já está ocupado por outro cliente
        if (agendamentoRepository.existsByCabeleireiroIdAndDataAgendamento(
                dto.getCabeleireiroId(), dto.getDataAgendamento())) {
            throw new RegistroDuplicadoException(
                    "Este horário já está ocupado. Por favor escolha outro horário.");
        }

        // 4. Verifica se existe agendamento nos próximos ou últimos 7 dias
        List<Agendamento> agendamentosProximos = agendamentoRepository
                .findByClienteIdAndDataAgendamentoBetween(
                        dto.getClienteId(),
                        dto.getDataAgendamento().minusDays(7),
                        dto.getDataAgendamento().plusDays(7));

        if (!agendamentosProximos.isEmpty()) {
            LocalDateTime dataSugerida = agendamentosProximos.get(0).getDataAgendamento();

            if (dataSugerida.isAfter(dto.getDataAgendamento())) {
                return "Sugestão: você já possui um agendamento em "
                        + dataSugerida
                        + ". Deseja adiantar este novo agendamento para a mesma data?";
            }

            return "Sugestão: você já possui um agendamento em "
                    + dataSugerida
                    + ". Deseja juntar este novo agendamento na mesma data?";
        }

        return null;
    }

    public Agendamento validarAtualizar(Integer id, AgendamentoUpdateRequestDTO dto) {
        Agendamento agendamento = agendamentoRepository.findById(id)
                .orElseThrow(() ->
                        new AgendamentoNaoEncontradoException("Agendamento não encontrado."));

        if (LocalDateTime.now().isAfter(agendamento.getDataAgendamento().minusDays(2))) {
            throw new AlteracaoNaoPermitidaException(
                    "Alteração não permitida. O agendamento está a menos de 2 dias. " +
                            "Para alterações, entre em contato pelo telefone do salão.");
        }

        if (dto.getDataAgendamento() != null) {
            validarHorarioCabeleireiro(
                    agendamento.getCabeleireiro().getId(), dto.getDataAgendamento(), id);
        }

        return agendamento;
    }

    public void validarDeletar(Integer id) {
        agendamentoRepository.findById(id)
                .orElseThrow(() ->
                        new AgendamentoNaoEncontradoException("Agendamento não encontrado."));
    }

    // Método privado que centraliza toda a validação de horário
    private void validarHorarioCabeleireiro(Integer cabeleireiroId,
                                            LocalDateTime dataAgendamento,
                                            Integer agendamentoIdIgnorar) {

        // Converte dia da semana
        DiaSemanaEnum diaSemana = converterDiaSemana(dataAgendamento);

        // Verifica se o cabeleireiro trabalha nesse dia
        List<HorarioDisponivel> horariosDisponiveis = horarioDisponivelRepository
                .findByCabeleireiroIdAndDiaSemanaAndAtivo(cabeleireiroId, diaSemana, true);

        if (horariosDisponiveis.isEmpty()) {
            throw new CampoInvalidoException("dataAgendamento",
                    "O cabeleireiro não trabalha neste dia da semana.");
        }

        HorarioDisponivel horario = horariosDisponiveis.get(0);
        LocalTime horaAgendamento = dataAgendamento.toLocalTime().truncatedTo(ChronoUnit.MINUTES);

        // Verifica se o horário está dentro do horário de trabalho
        if (horaAgendamento.isBefore(horario.getHoraInicio()) ||
                horaAgendamento.isAfter(horario.getHoraFim())) {
            throw new CampoInvalidoException("dataAgendamento",
                    "O horário escolhido está fora do horário de trabalho do cabeleireiro. " +
                            "Horário disponível: " + horario.getHoraInicio() + " às " + horario.getHoraFim());
        }

        // Verifica se o horário é válido dentro do intervalo
        long minutosDesdoInicio = ChronoUnit.MINUTES.between(horario.getHoraInicio(), horaAgendamento);
        if (minutosDesdoInicio % horario.getIntervaloMinutos() != 0) {
            throw new CampoInvalidoException("dataAgendamento",
                    "O horário escolhido não é válido. Os horários disponíveis são de " +
                            horario.getIntervaloMinutos() + " em " + horario.getIntervaloMinutos() + " minutos.");
        }

        // Verifica se o horário já está ocupado ignorando o agendamento atual se for atualização
        agendamentoRepository
                .findByCabeleireiroIdAndDataAgendamento(cabeleireiroId, dataAgendamento)
                .ifPresent(a -> {
                    if (!a.getId().equals(agendamentoIdIgnorar)) {
                        throw new RegistroDuplicadoException(
                                "Este horário já está ocupado. Por favor escolha outro horário.");
                    }
                });
    }

    // Método privado para converter dia da semana
    private DiaSemanaEnum converterDiaSemana(LocalDateTime data) {
        return DiaSemanaEnum.valueOf(
                data.getDayOfWeek().name()
                        .replace("MONDAY", "SEGUNDA")
                        .replace("TUESDAY", "TERCA")
                        .replace("WEDNESDAY", "QUARTA")
                        .replace("THURSDAY", "QUINTA")
                        .replace("FRIDAY", "SEXTA")
                        .replace("SATURDAY", "SABADO")
                        .replace("SUNDAY", "DOMINGO")
        );
    }
}