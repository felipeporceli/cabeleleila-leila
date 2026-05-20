package com.cabeleleilaleila.demo.specification;

import com.cabeleleilaleila.demo.model.Servico;
import org.springframework.data.jpa.domain.Specification;

public class ServicoSpecification {

    // SELECT * FROM servico WHERE UPPER(nome) LIKE '%nome.toUpperCase%'
    public static Specification<Servico> nomeLike(String nome) {
        return (root, query, cb)
                -> cb.like(cb.upper(root.get("nome")), "%" + nome.toUpperCase() + "%");
    }

    // SELECT * FROM servico WHERE ativo = :ativo
    public static Specification<Servico> ativoIgual(Boolean ativo) {
        return (root, query, cb) -> cb.equal(root.get("ativo"), ativo);
    }

}