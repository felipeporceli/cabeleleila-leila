package com.cabeleleilaleila.demo.service.impl;

import com.cabeleleilaleila.demo.dto.ServicoRequestDTO;
import com.cabeleleilaleila.demo.dto.ServicoResponseDTO;
import com.cabeleleilaleila.demo.dto.ServicoUpdateRequestDTO;
import com.cabeleleilaleila.demo.mapper.ServicoMapper;
import com.cabeleleilaleila.demo.model.Servico;
import com.cabeleleilaleila.demo.repository.ServicoRepository;
import com.cabeleleilaleila.demo.service.ServicoService;
import com.cabeleleilaleila.demo.specification.ServicoSpecification;
import com.cabeleleilaleila.demo.validator.ServicoValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ServicoServiceImpl implements ServicoService {

    private final ServicoRepository servicoRepository;
    private final ServicoValidator servicoValidator;
    private final ServicoMapper servicoMapper;

    @Override
    public ServicoResponseDTO salvar(ServicoRequestDTO dto) {
        servicoValidator.validarSalvar(dto);
        return servicoMapper.toResponseDTO(servicoRepository.save(servicoMapper.toEntity(dto)));
    }

    @Override
    public Page<ServicoResponseDTO> pesquisar(String nome,
                                              Boolean ativo,
                                              Integer pagina,
                                              Integer tamanhoPagina) {

        Specification<Servico> specification = Specification.where(
                (root, query, cb) -> cb.conjunction());

        if (nome != null) {
            specification = specification.and(ServicoSpecification.nomeLike(nome));
        }
        if (ativo != null) {
            specification = specification.and(ServicoSpecification.ativoIgual(ativo));
        }

        Pageable pageRequest = PageRequest.of(pagina, tamanhoPagina);
        return servicoRepository.findAll(specification, pageRequest)
                .map(servicoMapper::toResponseDTO);
    }

    @Override
    public ServicoResponseDTO atualizar(Integer id, ServicoUpdateRequestDTO dto) {
        Servico servicoExistente = servicoValidator.validarAtualizar(id);
        servicoMapper.toEntityUpdate(dto, servicoExistente);
        return servicoMapper.toResponseDTO(servicoRepository.save(servicoExistente));
    }

    @Override
    public void deletar(Integer id) {
        servicoValidator.validarDeletar(id);
        servicoRepository.deleteById(id);
    }

}