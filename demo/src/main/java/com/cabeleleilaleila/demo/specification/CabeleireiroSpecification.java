package com.cabeleleilaleila.demo.specification;

// root - Representa a entidade que está sendo consultada, no caso "Empresa".
// query - Representa a consulta que está sendo construída, mas não é usada nesse caso.
// cb - CriteriaBuilder, que cria as condições (where) dinamicamente.

import com.cabeleleilaleila.demo.model.Cabeleireiro;
import com.cabeleleilaleila.demo.model.enums.EspecialidadeEnum;
import org.springframework.data.jpa.domain.Specification;

// Classe para criação de métodos Specifications para melhor filtragem na pesquisa.
public class CabeleireiroSpecification {

    // SELECT * FROM cabeleireiro WHERE cpf = :cpf
    public static Specification<Cabeleireiro> cpfIgual(String cpf) {
        return (root, query, cb) -> cb.equal(root.get("cpf"), cpf);
    }

    // SELECT * FROM cabeleireiro WHERE UPPER (nome) LIKE '%nome.toUpperCase%'
    public static Specification<Cabeleireiro> nomeLike(String nome) {
        return (root, query, cb)
                -> cb.like(cb.upper(root.get("nome")), "%" + nome.toUpperCase() + "%");
    }

    // SELECT * FROM cabeleireiro WHERE telefone = :telefone
    public static Specification<Cabeleireiro> telefoneIgual(String telefone) {
        return (root, query, cb) -> cb.equal(root.get("telefone"), telefone);
    }

    // SELECT * FROM cabeleireiro WHERE UPPER (email) LIKE '%email.toUpperCase%'
    public static Specification<Cabeleireiro> emailLike(String email) {
        return (root, query, cb)
                -> cb.like(cb.upper(root.get("email")), "%" + email.toUpperCase() + "%");
    }

    // SELECT * FROM cabeleireiro WHERE especialidade = : especialidade
    public static Specification<Cabeleireiro> especialidadeIgual (EspecialidadeEnum especialidade) {
        return (root, query, cb) -> cb.equal(root.get("especialidade"), especialidade);
    }
}
