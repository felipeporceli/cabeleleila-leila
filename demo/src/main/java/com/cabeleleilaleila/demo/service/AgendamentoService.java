package com.cabeleleilaleila.demo.service;


import com.cabeleleilaleila.demo.dto.AgendamentoRequestDTO;
import com.cabeleleilaleila.demo.dto.AgendamentoResponseDTO;
import com.cabeleleilaleila.demo.dto.AgendamentoUpdateRequestDTO;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.time.LocalDateTime;

public interface AgendamentoService {

    AgendamentoResponseDTO salvar(AgendamentoRequestDTO dto);

    Page<AgendamentoResponseDTO> pesquisar(Integer clienteId,
                                Integer cabeleireiroId,
                                LocalDateTime dataAgendamento,
                                Integer pagina,
                                Integer tamanhoPagina);

    AgendamentoResponseDTO atualizar(Integer id, AgendamentoUpdateRequestDTO dto);

    void deletar(Integer id);

}
