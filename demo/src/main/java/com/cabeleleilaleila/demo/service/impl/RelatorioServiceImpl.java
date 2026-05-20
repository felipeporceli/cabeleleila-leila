package com.cabeleleilaleila.demo.service.impl;

import com.cabeleleilaleila.demo.dto.RelatorioSemanalDTO;
import com.cabeleleilaleila.demo.model.Agendamento;
import com.cabeleleilaleila.demo.model.Pagamento;
import com.cabeleleilaleila.demo.model.enums.StatusAgendamentoEnum;
import com.cabeleleilaleila.demo.model.enums.StatusPagamentoEnum;
import com.cabeleleilaleila.demo.repository.AgendamentoRepository;
import com.cabeleleilaleila.demo.repository.PagamentoRepository;
import com.cabeleleilaleila.demo.service.RelatorioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RelatorioServiceImpl implements RelatorioService {

    private final AgendamentoRepository agendamentoRepository;
    private final PagamentoRepository pagamentoRepository;

    @Override
    public RelatorioSemanalDTO gerarRelatorioSemanal(LocalDateTime dataInicio, LocalDateTime dataFim) {

        // Busca todos os agendamentos do período
        List<Agendamento> agendamentos = agendamentoRepository
                .findByDataAgendamentoBetween(dataInicio, dataFim);

        // Busca todos os pagamentos pagos do período
        List<Pagamento> pagamentos = pagamentoRepository
                .findByAgendamentoDataAgendamentoBetweenAndStatusPagamento(
                        dataInicio, dataFim, StatusPagamentoEnum.PAGO);

        // Conta por status
        int totalConfirmados = contarPorStatus(agendamentos, StatusAgendamentoEnum.CONFIRMADO);
        int totalCancelados = contarPorStatus(agendamentos, StatusAgendamentoEnum.CANCELADO);
        int totalConcluidos = contarPorStatus(agendamentos, StatusAgendamentoEnum.CONCLUIDO);

        // Calcula total faturado
        BigDecimal totalFaturado = pagamentos.stream()
                .map(Pagamento::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Faturamento por forma de pagamento
        Map<String, BigDecimal> faturamentoPorFormaPagamento = pagamentos.stream()
                .collect(Collectors.groupingBy(
                        p -> p.getFormaPagamento().name(),
                        Collectors.reducing(BigDecimal.ZERO, Pagamento::getValor, BigDecimal::add)));

        // Cabeleireiro com mais atendimentos
        String cabeleireiroMaisAtendimentos = agendamentos.stream()
                .collect(Collectors.groupingBy(
                        a -> a.getCabeleireiro().getNome(), Collectors.counting()))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("Nenhum atendimento");

        // Dia da semana com mais atendimentos
        String diaMaisAtendimentos = agendamentos.stream()
                .collect(Collectors.groupingBy(
                        a -> a.getDataAgendamento().getDayOfWeek()
                                .getDisplayName(TextStyle.FULL, new Locale("pt", "BR")),
                        Collectors.counting()))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("Nenhum atendimento");

        return RelatorioSemanalDTO.builder()
                .dataInicio(dataInicio)
                .dataFim(dataFim)
                .totalAgendamentos(agendamentos.size())
                .totalConfirmados(totalConfirmados)
                .totalCancelados(totalCancelados)
                .totalConcluidos(totalConcluidos)
                .totalFaturado(totalFaturado)
                .faturamentoPorFormaPagamento(faturamentoPorFormaPagamento)
                .cabeleireiroMaisAtendimentos(cabeleireiroMaisAtendimentos)
                .diaMaisAtendimentos(diaMaisAtendimentos)
                .build();
    }

    private int contarPorStatus(List<Agendamento> agendamentos, StatusAgendamentoEnum status) {
        return (int) agendamentos.stream()
                .filter(a -> a.getStatusAgendamento().equals(status))
                .count();
    }

}