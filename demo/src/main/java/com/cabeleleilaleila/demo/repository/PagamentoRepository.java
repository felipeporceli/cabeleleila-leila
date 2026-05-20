package com.cabeleleilaleila.demo.repository;

import com.cabeleleilaleila.demo.model.Pagamento;
import com.cabeleleilaleila.demo.model.enums.FormaPagamentoEnum;
import com.cabeleleilaleila.demo.model.enums.StatusPagamentoEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PagamentoRepository extends JpaRepository<Pagamento, Integer>,
        JpaSpecificationExecutor<Pagamento> {

    Optional<Pagamento> findByAgendamentoId(Integer agendamentoId);

    boolean existsByAgendamentoId(Integer agendamentoId);

    List<Pagamento> findByAgendamentoCabeleireiroIdAndDataPagamentoBetween(
            Integer cabeleireiroId, LocalDateTime dataInicio, LocalDateTime dataFim);

    List<Pagamento> findByStatusPagamento(StatusPagamentoEnum statusPagamento);

    List<Pagamento> findByFormaPagamento(FormaPagamentoEnum formaPagamento);

    @Query("SELECT SUM(p.valor) FROM Pagamento p " +
            "WHERE p.agendamento.cabeleireiro.id = :cabeleireiroId " +
            "AND p.dataPagamento BETWEEN :dataInicio AND :dataFim " +
            "AND p.statusPagamento = 'PAGO'")
    BigDecimal calcularTotalFaturado(
            @Param("cabeleireiroId") Integer cabeleireiroId,
            @Param("dataInicio") LocalDateTime dataInicio,
            @Param("dataFim") LocalDateTime dataFim);

    List<Pagamento> findByDataPagamentoBetweenAndStatusPagamento(
            LocalDateTime dataInicio,
            LocalDateTime dataFim,
            StatusPagamentoEnum statusPagamento);

    List<Pagamento> findByAgendamentoDataAgendamentoBetweenAndStatusPagamento(
            LocalDateTime dataInicio,
            LocalDateTime dataFim,
            StatusPagamentoEnum statusPagamento);

}
