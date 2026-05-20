package com.cabeleleilaleila.demo.service.impl;

import com.cabeleleilaleila.demo.dto.HorarioDisponivelRequestDTO;
import com.cabeleleilaleila.demo.dto.HorarioDisponivelUpdateRequestDTO;
import com.cabeleleilaleila.demo.mapper.HorarioDisponivelMapper;
import com.cabeleleilaleila.demo.model.Agendamento;
import com.cabeleleilaleila.demo.model.HorarioDisponivel;
import com.cabeleleilaleila.demo.model.enums.DiaSemanaEnum;
import com.cabeleleilaleila.demo.model.enums.StatusAgendamentoEnum;
import com.cabeleleilaleila.demo.repository.AgendamentoRepository;
import com.cabeleleilaleila.demo.repository.HorarioDisponivelRepository;
import com.cabeleleilaleila.demo.service.HorarioDisponivelService;
import com.cabeleleilaleila.demo.specification.HorarioDisponivelSpecification;
import com.cabeleleilaleila.demo.validator.HorarioDisponivelValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HorarioDisponivelServiceImpl implements HorarioDisponivelService {

    private final HorarioDisponivelRepository horarioDisponivelRepository;
    private final HorarioDisponivelValidator horarioDisponivelValidator;
    private final HorarioDisponivelMapper horarioDisponivelMapper;
    private final AgendamentoRepository agendamentoRepository;

    @Override
    public HorarioDisponivel salvar(HorarioDisponivelRequestDTO dto) {
        horarioDisponivelValidator.validarSalvar(dto);
        HorarioDisponivel horarioDisponivel = horarioDisponivelMapper.toEntity(dto);
        return horarioDisponivelRepository.save(horarioDisponivel);
    }

    @Override
    public Page<HorarioDisponivel> pesquisar(Integer cabeleireiroId,
                                             DiaSemanaEnum diaSemana,
                                             Boolean ativo,
                                             Integer pagina,
                                             Integer tamanhoPagina) {

        Specification<HorarioDisponivel> specification = Specification.where(
                (root, query, cb) -> cb.conjunction());

        if (cabeleireiroId != null) {
            specification = specification.and(HorarioDisponivelSpecification.cabeleireiroIdIgual(cabeleireiroId));
        }
        if (diaSemana != null) {
            specification = specification.and(HorarioDisponivelSpecification.diaSemanaIgual(diaSemana));
        }
        if (ativo != null) {
            specification = specification.and(HorarioDisponivelSpecification.ativoIgual(ativo));
        }

        Pageable pageRequest = PageRequest.of(pagina, tamanhoPagina);
        return horarioDisponivelRepository.findAll(specification, pageRequest);
    }

    @Override
    public HorarioDisponivel atualizar(Integer id, HorarioDisponivelUpdateRequestDTO dto) {
        HorarioDisponivel horarioExistente = horarioDisponivelValidator.validarAtualizar(id);
        horarioDisponivelMapper.toEntityUpdate(dto, horarioExistente);
        return horarioDisponivelRepository.save(horarioExistente);
    }

    @Override
    public void deletar(Integer id) {
        horarioDisponivelValidator.validarDeletar(id);
        horarioDisponivelRepository.deleteById(id);
    }

    @Override
    public List<HorarioDisponivel> buscarHorariosDisponiveisPorDia(Integer cabeleireiroId, DiaSemanaEnum diaSemana) {
        return horarioDisponivelRepository.findByCabeleireiroIdAndDiaSemanaAndAtivo(
                cabeleireiroId, diaSemana, true);
    }

    @Override
    public List<LocalDateTime> buscarHorariosDisponiveisPorData(Integer cabeleireiroId, LocalDateTime data) {

        // 1. Busca o dia da semana da data escolhida
        DiaSemanaEnum diaSemana = DiaSemanaEnum.valueOf(
                data.getDayOfWeek().name()
                        .replace("MONDAY", "SEGUNDA")
                        .replace("TUESDAY", "TERCA")
                        .replace("WEDNESDAY", "QUARTA")
                        .replace("THURSDAY", "QUINTA")
                        .replace("FRIDAY", "SEXTA")
                        .replace("SATURDAY", "SABADO")
                        .replace("SUNDAY", "DOMINGO")
        );

        // 2. Busca o horário cadastrado do cabeleireiro para aquele dia
        List<HorarioDisponivel> horariosDia = horarioDisponivelRepository
                .findByCabeleireiroIdAndDiaSemanaAndAtivo(cabeleireiroId, diaSemana, true);

        if (horariosDia.isEmpty()) {
            return List.of();
        }

        HorarioDisponivel horario = horariosDia.get(0);

        // 3. Gera os slots de tempo baseado no intervalo
        List<LocalDateTime> slots = new ArrayList<>();
        LocalDateTime slotAtual = data.with(horario.getHoraInicio());
        LocalDateTime horaFim = data.with(horario.getHoraFim());

        while (slotAtual.isBefore(horaFim)) {
            slots.add(slotAtual.truncatedTo(ChronoUnit.MINUTES));
            slotAtual = slotAtual.plusMinutes(horario.getIntervaloMinutos());
        }

        // 4. Busca todos os agendamentos do cabeleireiro no dia
        // 4. Busca todos os agendamentos do cabeleireiro no dia ignorando cancelados
        List<Agendamento> agendamentosDoDia = agendamentoRepository
                .findByCabeleireiroIdAndDataAgendamentoBetween(
                        cabeleireiroId,
                        data.with(horario.getHoraInicio()),
                        data.with(horario.getHoraFim()))
                .stream()
                .filter(a -> !a.getStatusAgendamento().equals(StatusAgendamentoEnum.CANCELADO))
                .toList();

        // 5. Remove os slots ocupados considerando a duração dos serviços
        for (Agendamento agendamento : agendamentosDoDia) {
            LocalDateTime inicioAgendamento = agendamento.getDataAgendamento()
                    .truncatedTo(ChronoUnit.MINUTES);

            // Calcula a duração total dos serviços do agendamento
            int duracaoTotal = agendamento.getServicos().stream()
                    .map(as -> as.getServico().getDuracaoMinutos())
                    .mapToInt(Integer::intValue)
                    .sum();

            LocalDateTime fimAgendamento = inicioAgendamento.plusMinutes(duracaoTotal);

            // Remove todos os slots que estão dentro do período do agendamento
            slots.removeIf(slot ->
                    !slot.isBefore(inicioAgendamento) && slot.isBefore(fimAgendamento));
        }

        return slots;
    }

}
