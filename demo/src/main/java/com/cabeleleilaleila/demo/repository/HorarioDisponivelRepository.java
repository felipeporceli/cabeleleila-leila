package com.cabeleleilaleila.demo.repository;

import com.cabeleleilaleila.demo.model.HorarioDisponivel;
import com.cabeleleilaleila.demo.model.enums.DiaSemanaEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HorarioDisponivelRepository extends JpaRepository<HorarioDisponivel, Integer>,
        JpaSpecificationExecutor<HorarioDisponivel> {

    List<HorarioDisponivel> findByCabeleireiroId(Integer cabeleireiroId);

    List<HorarioDisponivel> findByCabeleireiroIdAndDiaSemana(Integer cabeleireiroId, DiaSemanaEnum diaSemana);

    List<HorarioDisponivel> findByCabeleireiroIdAndAtivo(Integer cabeleireiroId, Boolean ativo);

    List<HorarioDisponivel> findByCabeleireiroIdAndDiaSemanaAndAtivo(Integer cabeleireiroId, DiaSemanaEnum diaSemana, Boolean ativo);

    boolean existsByCabeleireiroIdAndDiaSemana(Integer cabeleireiroId, DiaSemanaEnum diaSemana);
}
