package com.cabeleleilaleila.demo.mapper;

import com.cabeleleilaleila.demo.dto.ServicoRequestDTO;
import com.cabeleleilaleila.demo.dto.ServicoResponseDTO;
import com.cabeleleilaleila.demo.dto.ServicoUpdateRequestDTO;
import com.cabeleleilaleila.demo.model.Servico;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface ServicoMapper {

    Servico toEntity(ServicoRequestDTO dto);

    ServicoResponseDTO toResponseDTO(Servico servico);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void toEntityUpdate(ServicoUpdateRequestDTO dto, @MappingTarget Servico servico);

}