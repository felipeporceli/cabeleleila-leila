package com.cabeleleilaleila.demo.service;

import com.cabeleleilaleila.demo.dto.HorarioDisponivelRequestDTO;
import com.cabeleleilaleila.demo.dto.HorarioDisponivelUpdateRequestDTO;
import com.cabeleleilaleila.demo.model.HorarioDisponivel;
import com.cabeleleilaleila.demo.model.enums.DiaSemanaEnum;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;

public interface HorarioDisponivelService {

    HorarioDisponivel salvar(HorarioDisponivelRequestDTO dto);

    Page<HorarioDisponivel> pesquisar(Integer cabeleireiroId,
                                      DiaSemanaEnum diaSemana,
                                      Boolean ativo,
                                      Integer pagina,
                                      Integer tamanhoPagina);

    HorarioDisponivel atualizar(Integer id, HorarioDisponivelUpdateRequestDTO dto);

    void deletar(Integer id);

    List<HorarioDisponivel> buscarHorariosDisponiveisPorDia(Integer cabeleireiroId, DiaSemanaEnum diaSemana);

    List<LocalDateTime> buscarHorariosDisponiveisPorData(Integer cabeleireiroId, LocalDateTime data);

}
