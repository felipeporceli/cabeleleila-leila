package com.cabeleleilaleila.demo.service.impl;

import com.cabeleleilaleila.demo.dto.ClienteRequestDTO;
import com.cabeleleilaleila.demo.mapper.ClienteMapper;
import com.cabeleleilaleila.demo.model.Cliente;
import com.cabeleleilaleila.demo.repository.ClienteRepository;
import com.cabeleleilaleila.demo.service.ClienteService;
import com.cabeleleilaleila.demo.specification.ClienteSpecification;
import com.cabeleleilaleila.demo.validator.ClienteValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClienteServiceImpl implements ClienteService {

    private final ClienteRepository clienteRepository;
    private final ClienteValidator clienteValidator;
    private final ClienteMapper clienteMapper;

    @Override
    public Cliente salvar(Cliente cliente) {
        clienteValidator.validarSalvar(cliente);
        return clienteRepository.save(cliente);
    }

    @Override
    public Page<Cliente> pesquisar(String nome,
                                   String cpf,
                                   String email,
                                   String telefone,
                                   String cep,
                                   Integer pagina,
                                   Integer tamanhoPagina) {

        // SELECT * FROM empresa WHERE 0 = 0
        Specification<Cliente> specification = Specification.where
                ((root, query, cb) -> cb.conjunction());

        if (nome != null) {
            specification = specification.and(ClienteSpecification.nomeLike(nome));
        }

        if (cpf != null) {
            specification = specification.and(ClienteSpecification.cpfIgual(cpf));

        }
        if (email != null) {
            specification = specification.and(ClienteSpecification.emailLike(email));
        }

        if (telefone != null) {
            specification = specification.and(ClienteSpecification.telefoneIgual(telefone));
        }

        if (cep != null) {
            specification = specification.and(ClienteSpecification.cepLike(cep));
        }

        Pageable pageRequest = PageRequest.of(pagina, tamanhoPagina);
        return clienteRepository.findAll(specification, pageRequest);
    }

    @Override
    public Cliente atualizar(Integer id, ClienteRequestDTO dto) {
        Cliente clienteExistente = clienteValidator.validarAtualizar(id);
        clienteMapper.toEntityUpdate(dto, clienteExistente);
        return clienteRepository.save(clienteExistente);
    }

    @Override
    public void deletar(Integer id) {
        clienteValidator.validarDeletar(id);
        clienteRepository.deleteById(id);
    }

}
