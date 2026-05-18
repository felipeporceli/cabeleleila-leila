package com.cabeleleilaleila.demo.specification;

import com.cabeleleilaleila.demo.model.HorarioDisponivel;
import com.cabeleleilaleila.demo.model.enums.DiaSemanaEnum;
import org.springframework.data.jpa.domain.Specification;

public class HorarioDisponivelSpecification {

    // SELECT * FROM horario_disponivel WHERE cabeleireiro_id = :cabeleireiroId
    public static Specification<HorarioDisponivel> cabeleireiroIdIgual(Integer cabeleireiroId) {
        return (root, query, cb) -> cb.equal(root.get("cabeleireiro").get("id"), cabeleireiroId);
    }

    // SELECT * FROM horario_disponivel WHERE dia_semana = :diaSemana
    public static Specification<HorarioDisponivel> diaSemanaIgual(DiaSemanaEnum diaSemana) {
        return (root, query, cb) -> cb.equal(root.get("diaSemana"), diaSemana);
    }

    // SELECT * FROM horario_disponivel WHERE ativo = :ativo
    public static Specification<HorarioDisponivel> ativoIgual(Boolean ativo) {
        return (root, query, cb) -> cb.equal(root.get("ativo"), ativo);
    }

}
