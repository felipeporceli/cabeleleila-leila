package com.cabeleleilaleila.demo.repository;

import com.cabeleleilaleila.demo.model.Agendamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AgendamentoRepository extends JpaRepository<Agendamento, Integer>,
        JpaSpecificationExecutor<Agendamento> {

    List<Agendamento> findByClienteId(Integer clienteId);

    List<Agendamento> findByCabeleireiroId(Integer cabeleireiroId);

    List<Agendamento> findByDataAgendamento(LocalDateTime dataAgendamento);

    List<Agendamento> findByClienteIdAndDataAgendamento(Integer clienteId, LocalDateTime dataAgendamento);

    Optional<Agendamento> findByCabeleireiroIdAndDataAgendamento(
            Integer cabeleireiroId, LocalDateTime dataAgendamento);

    boolean existsByClienteIdAndDataAgendamento(Integer clienteId, LocalDateTime dataAgendamento);

    List<Agendamento> findByClienteIdAndDataAgendamentoBetween(
            Integer clienteId, LocalDateTime dataInicio, LocalDateTime dataFim);

    List<Agendamento> findByCabeleireiroIdAndDataAgendamentoBetween(
            Integer cabeleireiroId, LocalDateTime dataInicio, LocalDateTime dataFim);

    boolean existsByCabeleireiroIdAndDataAgendamento(Integer cabeleireiroId, LocalDateTime dataAgendamento);

    List<Agendamento> findByDataAgendamentoBetween(LocalDateTime dataInicio, LocalDateTime dataFim);


}
