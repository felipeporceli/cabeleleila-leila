package com.cabeleleilaleila.demo.service;

import com.cabeleleilaleila.demo.dto.CabeleireiroRequestDTO;
import com.cabeleleilaleila.demo.dto.CabeleireiroUpdateRequestDTO;
import com.cabeleleilaleila.demo.model.Cabeleireiro;
import com.cabeleleilaleila.demo.model.enums.EspecialidadeEnum;
import org.springframework.data.domain.Page;

public interface CabeleireiroService {

    Cabeleireiro salvar(Cabeleireiro cabeleireiro);

    Page<Cabeleireiro> pesquisar(String nome,
                                 String cpf,
                                 String email,
                                 String telefone,
                                 EspecialidadeEnum especialidade,
                                 Integer pagina,
                                 Integer tamanhoPagina);

    Cabeleireiro atualizar(Integer id, CabeleireiroUpdateRequestDTO dto);

    void deletar(Integer id);

}
