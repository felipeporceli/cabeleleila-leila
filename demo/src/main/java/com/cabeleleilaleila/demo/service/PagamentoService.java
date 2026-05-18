package com.cabeleleilaleila.demo.service;

import com.cabeleleilaleila.demo.dto.PagamentoRequestDTO;
import com.cabeleleilaleila.demo.dto.PagamentoUpdateRequestDTO;
import com.cabeleleilaleila.demo.dto.RelatorioFaturamentoDTO;
import com.cabeleleilaleila.demo.model.Pagamento;
import com.cabeleleilaleila.demo.model.enums.FormaPagamentoEnum;
import com.cabeleleilaleila.demo.model.enums.StatusPagamentoEnum;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;

public interface PagamentoService {

    Pagamento salvar(PagamentoRequestDTO dto);

    Page<Pagamento> pesquisar(Integer agendamentoId,
                              StatusPagamentoEnum statusPagamento,
                              FormaPagamentoEnum formaPagamento,
                              Integer pagina,
                              Integer tamanhoPagina);

    Pagamento atualizar(Integer id, PagamentoUpdateRequestDTO dto);

    void deletar(Integer id);

    RelatorioFaturamentoDTO gerarRelatorioFaturamento(Integer cabeleireiroId,
                                                      LocalDateTime dataInicio,
                                                      LocalDateTime dataFim);

}
