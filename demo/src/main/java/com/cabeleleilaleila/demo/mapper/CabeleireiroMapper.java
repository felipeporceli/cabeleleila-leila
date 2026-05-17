package com.cabeleleilaleila.demo.mapper;

import com.cabeleleilaleila.demo.dto.CabeleireiroRequestDTO;
import com.cabeleleilaleila.demo.dto.CabeleireiroResponseDTO;
import com.cabeleleilaleila.demo.dto.CabeleireiroUpdateRequestDTO;
import com.cabeleleilaleila.demo.model.Cabeleireiro;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface CabeleireiroMapper {

    Cabeleireiro toEntity(CabeleireiroRequestDTO dto);

    CabeleireiroResponseDTO toResponseDTO(Cabeleireiro cabeleireiro);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void toEntityUpdate(CabeleireiroUpdateRequestDTO dto, @MappingTarget Cabeleireiro cabeleireiro);

}