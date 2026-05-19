package com.cabeleleilaleila.demo.service.impl;

import com.cabeleleilaleila.demo.dto.AgendamentoRequestDTO;
import com.cabeleleilaleila.demo.dto.AgendamentoResponseDTO;
import com.cabeleleilaleila.demo.dto.AgendamentoUpdateRequestDTO;
import com.cabeleleilaleila.demo.exception.AgendamentoNaoEncontradoException;
import com.cabeleleilaleila.demo.exception.CampoInvalidoException;
import com.cabeleleilaleila.demo.mapper.AgendamentoMapper;
import com.cabeleleilaleila.demo.model.Agendamento;
import com.cabeleleilaleila.demo.model.enums.StatusAgendamentoEnum;
import com.cabeleleilaleila.demo.repository.AgendamentoRepository;
import com.cabeleleilaleila.demo.service.AgendamentoService;
import com.cabeleleilaleila.demo.specification.AgendamentoSpecification;
import com.cabeleleilaleila.demo.validator.AgendamentoValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AgendamentoServiceImpl implements AgendamentoService {

    private final AgendamentoRepository agendamentoRepository;
    private final AgendamentoValidator agendamentoValidator;
    private final AgendamentoMapper agendamentoMapper;

    @Override
    public AgendamentoResponseDTO salvar(AgendamentoRequestDTO dto) {
        String sugestao = agendamentoValidator.validarSalvar(dto);
        Agendamento agendamentoSalvo = agendamentoRepository.save(agendamentoMapper.toEntity(dto));
        AgendamentoResponseDTO response = agendamentoMapper.toResponseDTO(agendamentoSalvo);
        response.setSugestao(sugestao);
        return response;
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
    public AgendamentoResponseDTO atualizar(Integer id, AgendamentoUpdateRequestDTO dto) {
        Agendamento agendamentoExistente = agendamentoValidator.validarAtualizar(id, dto);
        agendamentoMapper.toEntityUpdate(dto, agendamentoExistente);
        return agendamentoMapper.toResponseDTO(agendamentoRepository.save(agendamentoExistente));
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
    public List<AgendamentoResponseDTO> buscarAgendamentosDoDia(Integer cabeleireiroId, LocalDate data) {
        return agendamentoRepository
                .findByCabeleireiroIdAndDataAgendamentoBetween(
                        cabeleireiroId,
                        data.atStartOfDay(),
                        data.atTime(23, 59, 59))
                .stream()
                .map(agendamentoMapper::toResponseDTO)
                .toList();
    }

    // Método auxiliar para evitar repetição de busca por id
    private Agendamento buscarAgendamentoPorId(Integer id) {
        return agendamentoRepository.findById(id)
                .orElseThrow(() ->
                        new AgendamentoNaoEncontradoException("Agendamento não encontrado."));
    }

}