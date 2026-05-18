package com.cabeleleilaleila.demo.specification;

import com.cabeleleilaleila.demo.model.Pagamento;
import com.cabeleleilaleila.demo.model.enums.FormaPagamentoEnum;
import com.cabeleleilaleila.demo.model.enums.StatusPagamentoEnum;
import org.springframework.data.jpa.domain.Specification;

public class PagamentoSpecification {

    // SELECT * FROM pagamento WHERE agendamento_id = :agendamentoId
    public static Specification<Pagamento> agendamentoIdIgual(Integer agendamentoId) {
        return (root, query, cb) -> cb.equal(root.get("agendamento").get("id"), agendamentoId);
    }

    // SELECT * FROM pagamento WHERE status_pagamento = :statusPagamento
    public static Specification<Pagamento> statusPagamentoIgual(StatusPagamentoEnum statusPagamento) {
        return (root, query, cb) -> cb.equal(root.get("statusPagamento"), statusPagamento);
    }

    // SELECT * FROM pagamento WHERE forma_pagamento = :formaPagamento
    public static Specification<Pagamento> formaPagamentoIgual(FormaPagamentoEnum formaPagamento) {
        return (root, query, cb) -> cb.equal(root.get("formaPagamento"), formaPagamento);
    }

}
