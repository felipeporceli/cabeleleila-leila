package com.cabeleleilaleila.demo.mapper;

import com.cabeleleilaleila.demo.dto.PagamentoRequestDTO;
import com.cabeleleilaleila.demo.dto.PagamentoResponseDTO;
import com.cabeleleilaleila.demo.dto.PagamentoUpdateRequestDTO;
import com.cabeleleilaleila.demo.model.Pagamento;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface PagamentoMapper {

    @Mapping(target = "agendamento.id", source = "agendamentoId")
    @Mapping(target = "agendamento.cliente", ignore = true)
    @Mapping(target = "agendamento.cabeleireiro", ignore = true)
    @Mapping(target = "agendamento.dataAgendamento", ignore = true)
    @Mapping(target = "agendamento.observacoes", ignore = true)
    @Mapping(target = "agendamento.statusAgendamento", ignore = true)
    Pagamento toEntity(PagamentoRequestDTO dto);

    @Mapping(target = "agendamentoId", source = "agendamento.id")
    @Mapping(target = "clienteNome", source = "agendamento.cliente.nome")
    @Mapping(target = "cabeleireiroNome", source = "agendamento.cabeleireiro.nome")
    PagamentoResponseDTO toResponseDTO(Pagamento pagamento);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void toEntityUpdate(PagamentoUpdateRequestDTO dto, @MappingTarget Pagamento pagamento);

}
