package com.cabeleleilaleila.demo.repository;

import com.cabeleleilaleila.demo.model.AgendamentoServico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AgendamentoServicoRepository extends JpaRepository<AgendamentoServico, Integer> {

    List<AgendamentoServico> findByAgendamentoId(Integer agendamentoId);

    List<AgendamentoServico> findByServicoId(Integer servicoId);

    void deleteByAgendamentoId(Integer agendamentoId);

}