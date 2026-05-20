package com.cabeleleilaleila.demo.mapper;

import com.cabeleleilaleila.demo.dto.AgendamentoRequestDTO;
import com.cabeleleilaleila.demo.dto.AgendamentoResponseDTO;
import com.cabeleleilaleila.demo.dto.AgendamentoUpdateRequestDTO;
import com.cabeleleilaleila.demo.dto.ServicoResponseDTO;
import com.cabeleleilaleila.demo.model.Agendamento;
import com.cabeleleilaleila.demo.model.AgendamentoServico;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.math.BigDecimal;
import java.util.List;

@Mapper(componentModel = "spring")
public interface AgendamentoMapper {

    @Mapping(target = "cliente.id", source = "clienteId")
    @Mapping(target = "cabeleireiro.id", source = "cabeleireiroId")
    @Mapping(target = "servicos", ignore = true)
    Agendamento toEntity(AgendamentoRequestDTO dto);

    @Mapping(target = "clienteId", source = "cliente.id")
    @Mapping(target = "clienteNome", source = "cliente.nome")
    @Mapping(target = "cabeleireiroId", source = "cabeleireiro.id")
    @Mapping(target = "cabeleireiroNome", source = "cabeleireiro.nome")
    @Mapping(target = "servicos", expression = "java(mapServicos(agendamento.getServicos()))")
    @Mapping(target = "valorTotal", expression = "java(calcularValorTotal(agendamento.getServicos()))")
    @Mapping(target = "sugestao", ignore = true)
    AgendamentoResponseDTO toResponseDTO(Agendamento agendamento);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "cliente.id", source = "clienteId")
    @Mapping(target = "cabeleireiro.id", source = "cabeleireiroId")
    @Mapping(target = "servicos", ignore = true)
    void toEntityUpdate(AgendamentoUpdateRequestDTO dto, @MappingTarget Agendamento agendamento);

    default List<ServicoResponseDTO> mapServicos(List<AgendamentoServico> servicos) {
        if (servicos == null) return List.of();
        return servicos.stream()
                .map(as -> {
                    ServicoResponseDTO dto = new ServicoResponseDTO();
                    dto.setId(as.getServico().getId());
                    dto.setNome(as.getServico().getNome());
                    dto.setDescricao(as.getServico().getDescricao());
                    dto.setPreco(as.getServico().getPreco());
                    dto.setDuracaoMinutos(as.getServico().getDuracaoMinutos());
                    dto.setAtivo(as.getServico().getAtivo());
                    return dto;
                })
                .toList();
    }

    default BigDecimal calcularValorTotal(List<AgendamentoServico> servicos) {
        if (servicos == null) return BigDecimal.ZERO;
        return servicos.stream()
                .map(as -> as.getServico().getPreco())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

}
