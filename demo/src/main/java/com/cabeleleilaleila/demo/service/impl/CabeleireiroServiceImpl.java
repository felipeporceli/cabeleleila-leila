package com.cabeleleilaleila.demo.service.impl;

import com.cabeleleilaleila.demo.dto.CabeleireiroRequestDTO;
import com.cabeleleilaleila.demo.dto.CabeleireiroUpdateRequestDTO;
import com.cabeleleilaleila.demo.mapper.CabeleireiroMapper;
import com.cabeleleilaleila.demo.model.Cabeleireiro;
import com.cabeleleilaleila.demo.model.enums.EspecialidadeEnum;
import com.cabeleleilaleila.demo.repository.CabeleireiroRepository;
import com.cabeleleilaleila.demo.service.CabeleireiroService;

import com.cabeleleilaleila.demo.specification.CabeleireiroSpecification;
import com.cabeleleilaleila.demo.validator.CabeleireiroValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CabeleireiroServiceImpl implements CabeleireiroService {

    private final CabeleireiroRepository cabeleireiroRepository;
    private final CabeleireiroValidator cabeleireiroValidator;
    private final CabeleireiroMapper cabeleireiroMapper;

    @Override
    public Cabeleireiro salvar(Cabeleireiro cabeleireiro) {
        cabeleireiroValidator.validarSalvar(cabeleireiro);
        return cabeleireiroRepository.save(cabeleireiro);
    }

    @Override
    public Page<Cabeleireiro> pesquisar(String nome,
                                        String cpf,
                                        String email,
                                        String telefone,
                                        EspecialidadeEnum especialidade,
                                        Integer pagina,
                                        Integer tamanhoPagina) {

        Specification<Cabeleireiro> specification = Specification.where(
                (root, query, cb) -> cb.conjunction());

        if (nome != null) {
            specification = specification.and(CabeleireiroSpecification.nomeLike(nome));
        }
        if (cpf != null) {
            specification = specification.and(CabeleireiroSpecification.cpfIgual(cpf));
        }
        if (email != null) {
            specification = specification.and(CabeleireiroSpecification.emailLike(email));
        }
        if (telefone != null) {
            specification = specification.and(CabeleireiroSpecification.telefoneIgual(telefone));
        }
        if (especialidade != null) {
            specification = specification.and(CabeleireiroSpecification.especialidadeIgual(especialidade));
        }

        Pageable pageRequest = PageRequest.of(pagina, tamanhoPagina);
        return cabeleireiroRepository.findAll(specification, pageRequest);
    }

    @Override
    public Cabeleireiro atualizar(Integer id, CabeleireiroUpdateRequestDTO dto) {
        Cabeleireiro cabeleireiroExistente = cabeleireiroValidator.validarAtualizar(id);
        cabeleireiroMapper.toEntityUpdate(dto, cabeleireiroExistente);
        return cabeleireiroRepository.save(cabeleireiroExistente);
    }

    @Override
    public void deletar(Integer id) {
        cabeleireiroValidator.validarDeletar(id);
        cabeleireiroRepository.deleteById(id);
    }

}
