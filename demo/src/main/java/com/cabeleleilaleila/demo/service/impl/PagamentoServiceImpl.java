package com.cabeleleilaleila.demo.service.impl;

import com.cabeleleilaleila.demo.dto.PagamentoRequestDTO;
import com.cabeleleilaleila.demo.dto.PagamentoUpdateRequestDTO;
import com.cabeleleilaleila.demo.dto.RelatorioFaturamentoDTO;
import com.cabeleleilaleila.demo.mapper.PagamentoMapper;
import com.cabeleleilaleila.demo.model.Pagamento;
import com.cabeleleilaleila.demo.model.enums.FormaPagamentoEnum;
import com.cabeleleilaleila.demo.model.enums.StatusPagamentoEnum;
import com.cabeleleilaleila.demo.repository.CabeleireiroRepository;
import com.cabeleleilaleila.demo.repository.PagamentoRepository;
import com.cabeleleilaleila.demo.service.PagamentoService;
import com.cabeleleilaleila.demo.specification.PagamentoSpecification;
import com.cabeleleilaleila.demo.validator.PagamentoValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PagamentoServiceImpl implements PagamentoService {

    private final PagamentoRepository pagamentoRepository;
    private final PagamentoValidator pagamentoValidator;
    private final PagamentoMapper pagamentoMapper;
    private final CabeleireiroRepository cabeleireiroRepository;

    @Override
    public Pagamento salvar(PagamentoRequestDTO dto) {
        pagamentoValidator.validarSalvar(dto);
        Pagamento pagamento = pagamentoMapper.toEntity(dto);
        return pagamentoRepository.save(pagamento);
    }

    @Override
    public Page<Pagamento> pesquisar(Integer agendamentoId,
                                     StatusPagamentoEnum statusPagamento,
                                     FormaPagamentoEnum formaPagamento,
                                     Integer pagina,
                                     Integer tamanhoPagina) {

        Specification<Pagamento> specification = Specification.where(
                (root, query, cb) -> cb.conjunction());

        if (agendamentoId != null) {
            specification = specification.and(PagamentoSpecification.agendamentoIdIgual(agendamentoId));
        }
        if (statusPagamento != null) {
            specification = specification.and(PagamentoSpecification.statusPagamentoIgual(statusPagamento));
        }
        if (formaPagamento != null) {
            specification = specification.and(PagamentoSpecification.formaPagamentoIgual(formaPagamento));
        }

        Pageable pageRequest = PageRequest.of(pagina, tamanhoPagina);
        return pagamentoRepository.findAll(specification, pageRequest);
    }

    @Override
    public Pagamento atualizar(Integer id, PagamentoUpdateRequestDTO dto) {
        Pagamento pagamentoExistente = pagamentoValidator.validarAtualizar(id);
        pagamentoMapper.toEntityUpdate(dto, pagamentoExistente);
        return pagamentoRepository.save(pagamentoExistente);
    }

    @Override
    public void deletar(Integer id) {
        pagamentoValidator.validarDeletar(id);
        pagamentoRepository.deleteById(id);
    }

    @Override
    public RelatorioFaturamentoDTO gerarRelatorioFaturamento(Integer cabeleireiroId,
                                                             LocalDateTime dataInicio,
                                                             LocalDateTime dataFim) {

        List<Pagamento> pagamentos = pagamentoRepository
                .findByAgendamentoCabeleireiroIdAndDataPagamentoBetween(
                        cabeleireiroId, dataInicio, dataFim);

        BigDecimal totalFaturado = pagamentoRepository
                .calcularTotalFaturado(cabeleireiroId, dataInicio, dataFim);

        String cabeleireiroNome = cabeleireiroRepository.findById(cabeleireiroId)
                .map(c -> c.getNome())
                .orElse("Cabeleireiro não encontrado");

        return RelatorioFaturamentoDTO.builder()
                .cabeleireiroNome(cabeleireiroNome)
                .dataInicio(dataInicio)
                .dataFim(dataFim)
                .totalAtendimentos(pagamentos.size())
                .totalFaturado(totalFaturado != null ? totalFaturado : BigDecimal.ZERO)
                .pagamentos(pagamentos.stream()
                        .map(pagamentoMapper::toResponseDTO)
                        .toList())
                .build();
    }

}
