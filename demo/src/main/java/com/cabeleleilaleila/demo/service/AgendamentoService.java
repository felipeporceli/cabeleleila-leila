package com.cabeleleilaleila.demo.service;


import com.cabeleleilaleila.demo.dto.AgendamentoRequestDTO;
import com.cabeleleilaleila.demo.dto.AgendamentoResponseDTO;
import com.cabeleleilaleila.demo.dto.AgendamentoUpdateRequestDTO;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface AgendamentoService {

    AgendamentoResponseDTO salvar(AgendamentoRequestDTO dto);

    Page<AgendamentoResponseDTO> pesquisar(Integer clienteId,
                                Integer cabeleireiroId,
                                LocalDateTime dataAgendamento,
                                Integer pagina,
                                Integer tamanhoPagina);

    AgendamentoResponseDTO atualizar(Integer id, AgendamentoUpdateRequestDTO dto);

    void deletar(Integer id);

    List<AgendamentoResponseDTO> buscarHistorico(Integer clienteId,
                                                 LocalDateTime dataInicio,
                                                 LocalDateTime dataFim);

    AgendamentoResponseDTO confirmar(Integer id);

    AgendamentoResponseDTO concluir(Integer id);

    AgendamentoResponseDTO cancelar(Integer id);

    List<AgendamentoResponseDTO> buscarAgendamentosPorPeriodo(Integer cabeleireiroId, LocalDate dataInicio, LocalDate dataFim);

}
