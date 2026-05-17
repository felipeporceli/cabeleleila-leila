package com.cabeleleilaleila.demo.service;

import com.cabeleleilaleila.demo.dto.ClienteRequestDTO;
import com.cabeleleilaleila.demo.model.Cliente;
import org.springframework.data.domain.Page;

public interface ClienteService {

    Cliente salvar(Cliente cliente);

    Page<Cliente> pesquisar(String nome,
                            String cpf,
                            String email,
                            String telefone,
                            String cep,
                            Integer pagina,
                            Integer tamanhoPagina);

    Cliente atualizar(Integer id, ClienteRequestDTO dto);

    void deletar(Integer id);

}
