package com.cabeleleilaleila.demo.mapper;

import com.cabeleleilaleila.demo.dto.ClienteRequestDTO;
import com.cabeleleilaleila.demo.dto.ClienteResponseDTO;
import com.cabeleleilaleila.demo.model.Cliente;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface ClienteMapper {

    Cliente toEntity(ClienteRequestDTO dto);

    ClienteResponseDTO toResponseDTO(Cliente cliente);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void toEntityUpdate(ClienteRequestDTO dto, @MappingTarget Cliente cliente);
}