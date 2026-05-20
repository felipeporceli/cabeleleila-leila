package com.cabeleleilaleila.demo.service.impl;

import com.cabeleleilaleila.demo.dto.AgendamentoRequestDTO;
import com.cabeleleilaleila.demo.dto.AgendamentoResponseDTO;
import com.cabeleleilaleila.demo.dto.AgendamentoUpdateRequestDTO;
import com.cabeleleilaleila.demo.exception.AgendamentoNaoEncontradoException;
import com.cabeleleilaleila.demo.exception.CampoInvalidoException;
import com.cabeleleilaleila.demo.mapper.AgendamentoMapper;
import com.cabeleleilaleila.demo.model.Agendamento;
import com.cabeleleilaleila.demo.model.AgendamentoServico;
import com.cabeleleilaleila.demo.model.Pagamento;
import com.cabeleleilaleila.demo.model.Servico;
import com.cabeleleilaleila.demo.model.enums.FormaPagamentoEnum;
import com.cabeleleilaleila.demo.model.enums.StatusAgendamentoEnum;
import com.cabeleleilaleila.demo.model.enums.StatusPagamentoEnum;
import com.cabeleleilaleila.demo.repository.AgendamentoRepository;
import com.cabeleleilaleila.demo.repository.AgendamentoServicoRepository;
import com.cabeleleilaleila.demo.repository.PagamentoRepository;
import com.cabeleleilaleila.demo.repository.ServicoRepository;
import com.cabeleleilaleila.demo.service.AgendamentoService;
import com.cabeleleilaleila.demo.specification.AgendamentoSpecification;
import com.cabeleleilaleila.demo.validator.AgendamentoValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AgendamentoServiceImpl implements AgendamentoService {

    private final AgendamentoRepository agendamentoRepository;
    private final AgendamentoValidator agendamentoValidator;
    private final AgendamentoMapper agendamentoMapper;
    private final ServicoRepository servicoRepository;
    private final AgendamentoServicoRepository agendamentoServicoRepository;
    private final PagamentoRepository pagamentoRepository;

    @Override
    @Transactional
    public AgendamentoResponseDTO salvar(AgendamentoRequestDTO dto) {
        String sugestao = agendamentoValidator.validarSalvar(dto);

        Agendamento agendamento = agendamentoMapper.toEntity(dto);
        Agendamento agendamentoSalvo = agendamentoRepository.save(agendamento);

        // Vincula os serviços ao agendamento
        vincularServicos(agendamentoSalvo, dto.getServicoIds());

        // Cria o pagamento pendente automaticamente
        criarPagamentoPendente(agendamentoSalvo);

        AgendamentoResponseDTO response = agendamentoMapper.toResponseDTO(agendamentoSalvo);
        response.setSugestao(sugestao);
        return response;
    }

    private void criarPagamentoPendente(Agendamento agendamento) {
        BigDecimal valorTotal = agendamento.getServicos().stream()
                .map(as -> as.getServico().getPreco())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Pagamento pagamento = new Pagamento();
        pagamento.setAgendamento(agendamento);
        pagamento.setValor(valorTotal);
        pagamento.setStatusPagamento(StatusPagamentoEnum.PENDENTE);
        pagamento.setFormaPagamento(FormaPagamentoEnum.PIX);

        pagamentoRepository.save(pagamento);
    }

    @Override
    public Page<AgendamentoResponseDTO> pesquisar(Integer clienteId,
                                                  Integer cabeleireiroId,
                                                  LocalDateTime dataAgendamento,
                                                  Integer pagina,
                                                  Integer tamanhoPagina) {

        Specification<Agendamento> specification = Specification.where(
                (root, query, cb) -> cb.conjunction());

        if (clienteId != null) {
            specification = specification.and(AgendamentoSpecification.clienteIdIgual(clienteId));
        }
        if (cabeleireiroId != null) {
            specification = specification.and(AgendamentoSpecification.cabeleireiroIdIgual(cabeleireiroId));
        }
        if (dataAgendamento != null) {
            specification = specification.and(AgendamentoSpecification.dataAgendamentoIgual(dataAgendamento));
        }

        Pageable pageRequest = PageRequest.of(pagina, tamanhoPagina);
        return agendamentoRepository.findAll(specification, pageRequest)
                .map(agendamentoMapper::toResponseDTO);
    }

    @Override
    @Transactional
    public AgendamentoResponseDTO atualizar(Integer id, AgendamentoUpdateRequestDTO dto) {
        Agendamento agendamentoExistente = agendamentoValidator.validarAtualizar(id, dto);
        agendamentoMapper.toEntityUpdate(dto, agendamentoExistente);

        // Atualiza os serviços se foram informados (orphanRemoval cuida da deleção dos antigos)
        if (dto.getServicoIds() != null && !dto.getServicoIds().isEmpty()) {
            vincularServicos(agendamentoExistente, dto.getServicoIds());
        }

        AgendamentoResponseDTO response = agendamentoMapper.toResponseDTO(
                agendamentoRepository.save(agendamentoExistente));

        String sugestao = agendamentoValidator.gerarSugestaoAtualizacao(
                agendamentoExistente.getCliente().getId(), id, dto.getDataAgendamento());
        response.setSugestao(sugestao);

        return response;
    }

    @Override
    public void deletar(Integer id) {
        agendamentoValidator.validarDeletar(id);
        agendamentoRepository.deleteById(id);
    }

    @Override
    public List<AgendamentoResponseDTO> buscarHistorico(Integer clienteId,
                                                        LocalDateTime dataInicio,
                                                        LocalDateTime dataFim) {
        return agendamentoRepository
                .findByClienteIdAndDataAgendamentoBetween(clienteId, dataInicio, dataFim)
                .stream()
                .map(agendamentoMapper::toResponseDTO)
                .toList();
    }

    @Override
    public AgendamentoResponseDTO confirmar(Integer id) {
        Agendamento agendamento = buscarAgendamentoPorId(id);

        if (agendamento.getStatusAgendamento().equals(StatusAgendamentoEnum.CANCELADO)) {
            throw new CampoInvalidoException("statusAgendamento",
                    "Não é possível confirmar um agendamento cancelado.");
        }
        if (agendamento.getStatusAgendamento().equals(StatusAgendamentoEnum.CONFIRMADO)) {
            throw new CampoInvalidoException("statusAgendamento",
                    "Agendamento já está confirmado.");
        }

        agendamento.setStatusAgendamento(StatusAgendamentoEnum.CONFIRMADO);
        return agendamentoMapper.toResponseDTO(agendamentoRepository.save(agendamento));
    }
    @Override
    public AgendamentoResponseDTO cancelar(Integer id) {
        Agendamento agendamento = buscarAgendamentoPorId(id);

        if (agendamento.getStatusAgendamento().equals(StatusAgendamentoEnum.CANCELADO)) {
            throw new CampoInvalidoException("statusAgendamento",
                    "Agendamento já está cancelado.");
        }

        if (agendamento.getStatusAgendamento().equals(StatusAgendamentoEnum.CONCLUIDO)) {
            throw new CampoInvalidoException("statusAgendamento",
                    "Não é possível cancelar um agendamento já concluído.");
        }

        agendamento.setStatusAgendamento(StatusAgendamentoEnum.CANCELADO);
        return agendamentoMapper.toResponseDTO(agendamentoRepository.save(agendamento));
    }

    @Override
    public AgendamentoResponseDTO concluir(Integer id) {
        Agendamento agendamento = buscarAgendamentoPorId(id);

        if (agendamento.getStatusAgendamento().equals(StatusAgendamentoEnum.CANCELADO)) {
            throw new CampoInvalidoException("statusAgendamento",
                    "Não é possível concluir um agendamento cancelado.");
        }
        if (agendamento.getStatusAgendamento().equals(StatusAgendamentoEnum.CONCLUIDO)) {
            throw new CampoInvalidoException("statusAgendamento",
                    "Agendamento já está concluído.");
        }

        agendamento.setStatusAgendamento(StatusAgendamentoEnum.CONCLUIDO);
        return agendamentoMapper.toResponseDTO(agendamentoRepository.save(agendamento));
    }

    @Override
    public List<AgendamentoResponseDTO> buscarAgendamentosPorPeriodo(Integer cabeleireiroId,
                                                                      LocalDate dataInicio,
                                                                      LocalDate dataFim) {
        LocalDateTime inicio = dataInicio.atStartOfDay();
        LocalDateTime fim = dataFim.atTime(23, 59, 59);

        List<Agendamento> agendamentos = (cabeleireiroId != null)
                ? agendamentoRepository.findByCabeleireiroIdAndDataAgendamentoBetween(cabeleireiroId, inicio, fim)
                : agendamentoRepository.findByDataAgendamentoBetween(inicio, fim);

        return agendamentos.stream().map(agendamentoMapper::toResponseDTO).toList();
    }

    // Método auxiliar para vincular serviços ao agendamento
    private void vincularServicos(Agendamento agendamento, List<Integer> servicoIds) {
        List<Servico> servicos = servicoRepository.findAllById(servicoIds);

        if (servicos.size() != servicoIds.size()) {
            throw new CampoInvalidoException("servicoIds",
                    "Um ou mais serviços informados não foram encontrados.");
        }

        // Nunca substituir a referência da coleção — usar clear()+addAll()
        // para que o Hibernate (orphanRemoval) gerencie deleções e inserções corretamente
        agendamento.getServicos().clear();

        servicos.forEach(servico -> {
            AgendamentoServico as = new AgendamentoServico();
            as.setAgendamento(agendamento);
            as.setServico(servico);
            agendamento.getServicos().add(as);
        });
    }

    private Agendamento buscarAgendamentoPorId(Integer id) {
        return agendamentoRepository.findById(id)
                .orElseThrow(() ->
                        new AgendamentoNaoEncontradoException("Agendamento não encontrado."));
    }

}