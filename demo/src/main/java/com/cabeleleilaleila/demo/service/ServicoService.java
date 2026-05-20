package com.cabeleleilaleila.demo.service;

import com.cabeleleilaleila.demo.dto.ServicoRequestDTO;
import com.cabeleleilaleila.demo.dto.ServicoResponseDTO;
import com.cabeleleilaleila.demo.dto.ServicoUpdateRequestDTO;
import org.springframework.data.domain.Page;

public interface ServicoService {

    ServicoResponseDTO salvar(ServicoRequestDTO dto);

    Page<ServicoResponseDTO> pesquisar(String nome,
                                       Boolean ativo,
                                       Integer pagina,
                                       Integer tamanhoPagina);

    ServicoResponseDTO atualizar(Integer id, ServicoUpdateRequestDTO dto);

    void deletar(Integer id);

}
