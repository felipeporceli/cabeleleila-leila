package com.cabeleleilaleila.demo.mapper;

import com.cabeleleilaleila.demo.dto.HorarioDisponivelRequestDTO;
import com.cabeleleilaleila.demo.dto.HorarioDisponivelResponseDTO;
import com.cabeleleilaleila.demo.dto.HorarioDisponivelUpdateRequestDTO;
import com.cabeleleilaleila.demo.model.HorarioDisponivel;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface HorarioDisponivelMapper {

    @Mapping(target = "cabeleireiro.id", source = "cabeleireiroId")
    @Mapping(target = "cabeleireiro.nome", ignore = true)
    @Mapping(target = "cabeleireiro.cpf", ignore = true)
    @Mapping(target = "cabeleireiro.senha", ignore = true)
    @Mapping(target = "cabeleireiro.email", ignore = true)
    @Mapping(target = "cabeleireiro.telefone", ignore = true)
    @Mapping(target = "cabeleireiro.especialidade", ignore = true)
    HorarioDisponivel toEntity(HorarioDisponivelRequestDTO dto);

    @Mapping(target = "cabeleireiroId", source = "cabeleireiro.id")
    @Mapping(target = "cabeleireiroNome", source = "cabeleireiro.nome")
    HorarioDisponivelResponseDTO toResponseDTO(HorarioDisponivel horarioDisponivel);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void toEntityUpdate(HorarioDisponivelUpdateRequestDTO dto, @MappingTarget HorarioDisponivel horarioDisponivel);

}
