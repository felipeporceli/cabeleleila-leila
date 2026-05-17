package com.cabeleleilaleila.demo.repository;

import com.cabeleleilaleila.demo.model.Cabeleireiro;
import com.cabeleleilaleila.demo.model.enums.EspecialidadeEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface CabeleireiroRepository extends JpaRepository<Cabeleireiro, Integer>,
        JpaSpecificationExecutor<Cabeleireiro> {

    Optional<Cabeleireiro> findByCpf(String cpf);

    Optional<Cabeleireiro> findByEmail(String email);

    boolean existsByCpf(String cpf);

    boolean existsByEmail(String email);

    List<Cabeleireiro> findByEspecialidade(EspecialidadeEnum especialidade);

}
