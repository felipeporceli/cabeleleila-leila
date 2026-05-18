package com.cabeleleilaleila.demo.service.impl;

import com.cabeleleilaleila.demo.dto.AgendamentoRequestDTO;
import com.cabeleleilaleila.demo.dto.AgendamentoResponseDTO;
import com.cabeleleilaleila.demo.dto.AgendamentoUpdateRequestDTO;
import com.cabeleleilaleila.demo.mapper.AgendamentoMapper;
import com.cabeleleilaleila.demo.model.Agendamento;
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

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AgendamentoServiceImpl implements AgendamentoService {

    private final AgendamentoRepository agendamentoRepository;
    private final AgendamentoValidator agendamentoValidator;
    private final AgendamentoMapper agendamentoMapper;

    @Override
    public AgendamentoResponseDTO salvar(AgendamentoRequestDTO dto) {
        String sugestao = agendamentoValidator.validarSalvar(dto);
        Agendamento agendamento = agendamentoMapper.toEntity(dto);
        Agendamento agendamentoSalvo = agendamentoRepository.save(agendamento);
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
        Agendamento agendamentoExistente = agendamentoValidator.validarAtualizar(id);
        agendamentoMapper.toEntityUpdate(dto, agendamentoExistente);
        return agendamentoMapper.toResponseDTO(agendamentoRepository.save(agendamentoExistente));
    }

    @Override
    public void deletar(Integer id) {
        agendamentoValidator.validarDeletar(id);
        agendamentoRepository.deleteById(id);
    }

}
