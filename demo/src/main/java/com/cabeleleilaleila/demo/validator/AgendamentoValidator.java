package com.cabeleleilaleila.demo.validator;

import com.cabeleleilaleila.demo.dto.AgendamentoRequestDTO;
import com.cabeleleilaleila.demo.dto.AgendamentoUpdateRequestDTO;
import com.cabeleleilaleila.demo.exception.AgendamentoNaoEncontradoException;
import com.cabeleleilaleila.demo.exception.AlteracaoNaoPermitidaException;
import com.cabeleleilaleila.demo.exception.CampoInvalidoException;
import com.cabeleleilaleila.demo.exception.RegistroDuplicadoException;
import com.cabeleleilaleila.demo.model.Agendamento;
import com.cabeleleilaleila.demo.model.AgendamentoServico;
import com.cabeleleilaleila.demo.model.HorarioDisponivel;
import com.cabeleleilaleila.demo.model.Servico;
import com.cabeleleilaleila.demo.model.enums.DiaSemanaEnum;
import com.cabeleleilaleila.demo.model.enums.StatusAgendamentoEnum;
import com.cabeleleilaleila.demo.repository.AgendamentoRepository;
import com.cabeleleilaleila.demo.repository.HorarioDisponivelRepository;
import com.cabeleleilaleila.demo.repository.ServicoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
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
    private final ServicoRepository servicoRepository;

    public String validarSalvar(AgendamentoRequestDTO dto) {

        if (agendamentoRepository.existsByClienteIdAndDataAgendamento(
                dto.getClienteId(), dto.getDataAgendamento())) {
            throw new RegistroDuplicadoException(
                    "Já existe um agendamento para esse cliente nesta data.");
        }

        validarHorarioCabeleireiro(
                dto.getCabeleireiroId(), dto.getDataAgendamento(), null, dto.getServicoIds());

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

        boolean isAdmin = SecurityContextHolder.getContext().getAuthentication()
                .getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin && dto.getDataAgendamento() != null &&
                dto.getDataAgendamento().isBefore(LocalDateTime.now().plusDays(2))) {
            throw new AlteracaoNaoPermitidaException(
                    "Não é possível reagendar para uma data com menos de 2 dias de antecedência. " +
                            "Para alterações, entre em contato pelo telefone do salão.");
        }

        if (dto.getDataAgendamento() != null) {
            List<Integer> servicoIds = dto.getServicoIds() != null
                    ? dto.getServicoIds()
                    : agendamento.getServicos().stream()
                      .map(as -> as.getServico().getId())
                      .toList();

            validarHorarioCabeleireiro(
                    agendamento.getCabeleireiro().getId(),
                    dto.getDataAgendamento(),
                    id,
                    servicoIds);
        }

        return agendamento;
    }

    public String gerarSugestaoAtualizacao(Integer clienteId, Integer agendamentoId, LocalDateTime novaData) {
        if (novaData == null) return null;

        List<Agendamento> proximos = agendamentoRepository
                .findByClienteIdAndDataAgendamentoBetween(
                        clienteId,
                        novaData.minusDays(7),
                        novaData.plusDays(7))
                .stream()
                .filter(a -> !a.getId().equals(agendamentoId))
                .toList();

        if (proximos.isEmpty()) return null;

        LocalDateTime dataSugerida = proximos.get(0).getDataAgendamento();

        if (dataSugerida.isAfter(novaData)) {
            return "Você já possui um agendamento em " + dataSugerida
                    + ". Deseja adiantar este agendamento para a mesma data?";
        }

        return "Você já possui um agendamento em " + dataSugerida
                + ". Deseja juntar este agendamento no mesmo dia?";
    }

    public void validarDeletar(Integer id) {
        agendamentoRepository.findById(id)
                .orElseThrow(() ->
                        new AgendamentoNaoEncontradoException("Agendamento não encontrado."));
    }

    private void validarHorarioCabeleireiro(Integer cabeleireiroId,
                                            LocalDateTime dataAgendamento,
                                            Integer agendamentoIdIgnorar,
                                            List<Integer> servicoIds) {

        DiaSemanaEnum diaSemana = converterDiaSemana(dataAgendamento);

        List<HorarioDisponivel> horariosDisponiveis = horarioDisponivelRepository
                .findByCabeleireiroIdAndDiaSemanaAndAtivo(cabeleireiroId, diaSemana, true);

        if (horariosDisponiveis.isEmpty()) {
            throw new CampoInvalidoException("dataAgendamento",
                    "O cabeleireiro não trabalha neste dia da semana.");
        }

        HorarioDisponivel horario = horariosDisponiveis.get(0);
        LocalTime horaAgendamento = dataAgendamento.toLocalTime().truncatedTo(ChronoUnit.MINUTES);

        if (horaAgendamento.isBefore(horario.getHoraInicio()) ||
                horaAgendamento.isAfter(horario.getHoraFim())) {
            throw new CampoInvalidoException("dataAgendamento",
                    "O horário escolhido está fora do horário de trabalho do cabeleireiro. " +
                            "Horário disponível: " + horario.getHoraInicio() + " às " + horario.getHoraFim());
        }

        long minutosDesdoInicio = ChronoUnit.MINUTES.between(horario.getHoraInicio(), horaAgendamento);
        if (minutosDesdoInicio % horario.getIntervaloMinutos() != 0) {
            throw new CampoInvalidoException("dataAgendamento",
                    "O horário escolhido não é válido. Os horários disponíveis são de " +
                            horario.getIntervaloMinutos() + " em " + horario.getIntervaloMinutos() + " minutos.");
        }

        int duracaoTotalNovo = servicoRepository.findAllById(servicoIds)
                .stream()
                .mapToInt(Servico::getDuracaoMinutos)
                .sum();

        LocalDateTime fimNovoAgendamento = dataAgendamento.plusMinutes(duracaoTotalNovo);

        // Busca todos os agendamentos do cabeleireiro no dia ignorando CANCELADOS
        List<Agendamento> agendamentosDoDia = agendamentoRepository
                .findByCabeleireiroIdAndDataAgendamentoBetween(
                        cabeleireiroId,
                        dataAgendamento.toLocalDate().atStartOfDay(),
                        dataAgendamento.toLocalDate().atTime(23, 59, 59))
                .stream()
                .filter(a -> !a.getStatusAgendamento().equals(StatusAgendamentoEnum.CANCELADO))
                .toList();

        for (Agendamento agendamentoExistente : agendamentosDoDia) {

            if (agendamentoExistente.getId().equals(agendamentoIdIgnorar)) {
                continue;
            }

            LocalDateTime inicioExistente = agendamentoExistente.getDataAgendamento();

            int duracaoExistente = agendamentoExistente.getServicos().stream()
                    .map(AgendamentoServico::getServico)
                    .mapToInt(Servico::getDuracaoMinutos)
                    .sum();

            LocalDateTime fimExistente = inicioExistente.plusMinutes(duracaoExistente);

            boolean haConflito = dataAgendamento.isBefore(fimExistente) &&
                    fimNovoAgendamento.isAfter(inicioExistente);

            if (haConflito) {
                throw new RegistroDuplicadoException(
                        "O cabeleireiro já possui um agendamento das " +
                                inicioExistente.toLocalTime() + " às " +
                                fimExistente.toLocalTime() +
                                ". Por favor escolha outro horário.");
            }
        }
    }

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