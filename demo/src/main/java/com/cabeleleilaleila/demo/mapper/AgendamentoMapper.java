package com.cabeleleilaleila.demo.mapper;

import com.cabeleleilaleila.demo.dto.AgendamentoRequestDTO;
import com.cabeleleilaleila.demo.dto.AgendamentoResponseDTO;
import com.cabeleleilaleila.demo.dto.AgendamentoUpdateRequestDTO;
import com.cabeleleilaleila.demo.model.Agendamento;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface AgendamentoMapper {

    @Mapping(target = "cliente.id", source = "clienteId")
    @Mapping(target = "cabeleireiro.id", source = "cabeleireiroId")
    Agendamento toEntity(AgendamentoRequestDTO dto);

    @Mapping(target = "clienteId", source = "cliente.id")
    @Mapping(target = "clienteNome", source = "cliente.nome")
    @Mapping(target = "cabeleireiroId", source = "cabeleireiro.id")
    @Mapping(target = "cabeleireiroNome", source = "cabeleireiro.nome")
    AgendamentoResponseDTO toResponseDTO(Agendamento agendamento);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "cliente.id", source = "clienteId")
    @Mapping(target = "cabeleireiro.id", source = "cabeleireiroId")
    void toEntityUpdate(AgendamentoUpdateRequestDTO dto, @MappingTarget Agendamento agendamento);

}
