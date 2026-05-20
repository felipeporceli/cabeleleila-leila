package com.cabeleleilaleila.demo.repository;

import com.cabeleleilaleila.demo.model.Servico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServicoRepository extends JpaRepository<Servico, Integer>,
        JpaSpecificationExecutor<Servico> {

    List<Servico> findByAtivo(Boolean ativo);

    boolean existsByNome(String nome);

}