package com.cabeleleilaleila.demo.specification;

import com.cabeleleilaleila.demo.model.Agendamento;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class AgendamentoSpecification {

    // SELECT * FROM agendamento WHERE cliente_id = :clienteId
    public static Specification<Agendamento> clienteIdIgual(Integer clienteId) {
        return (root, query, cb) -> cb.equal(root.get("cliente").get("id"), clienteId);
    }

    // SELECT * FROM agendamento WHERE cabeleireiro_id = :cabeleireiroId
    public static Specification<Agendamento> cabeleireiroIdIgual(Integer cabeleireiroId) {
        return (root, query, cb) -> cb.equal(root.get("cabeleireiro").get("id"), cabeleireiroId);
    }

    // SELECT * FROM agendamento WHERE data_agendamento = :dataAgendamento
    public static Specification<Agendamento> dataAgendamentoIgual(LocalDateTime dataAgendamento) {
        return (root, query, cb) -> cb.equal(root.get("dataAgendamento"), dataAgendamento);
    }

    // SELECT * FROM agendamento WHERE data_agendamento BETWEEN :dataInicio AND :dataFim
    public static Specification<Agendamento> dataAgendamentoBetween(LocalDateTime dataInicio, LocalDateTime dataFim) {
        return (root, query, cb) -> cb.between(root.get("dataAgendamento"), dataInicio, dataFim);
    }

    // SELECT * FROM agendamento WHERE UPPER(observacoes) LIKE '%observacoes.toUpperCase%'
    public static Specification<Agendamento> observacoesLike(String observacoes) {
        return (root, query, cb)
                -> cb.like(cb.upper(root.get("observacoes")), "%" + observacoes.toUpperCase() + "%");
    }

}
