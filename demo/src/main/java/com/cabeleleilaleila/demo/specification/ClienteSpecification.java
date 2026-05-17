package com.cabeleleilaleila.demo.specification;

// root - Representa a entidade que está sendo consultada, no caso "Empresa".
// query - Representa a consulta que está sendo construída, mas não é usada nesse caso.
// cb - CriteriaBuilder, que cria as condições (where) dinamicamente.

import com.cabeleleilaleila.demo.model.Cliente;
import org.springframework.data.jpa.domain.Specification;

// Classe para criação de métodos Specifications para melhor filtragem na pesquisa.
public class ClienteSpecification {

    // SELECT * FROM cliente WHERE cpf = :cpf
    public static Specification<Cliente> cpfIgual(String cpf) {
        return (root, query, cb) -> cb.equal(root.get("cpf"), cpf);
    }

    // SELECT * FROM cliente WHERE UPPER (nome) LIKE '%nome.toUpperCase%'
    public static Specification<Cliente> nomeLike(String nome) {
        return (root, query, cb)
                -> cb.like(cb.upper(root.get("nome")), "%" + nome.toUpperCase() + "%");
    }

    // SELECT * FROM cliente WHERE telefone = :telefone
    public static Specification<Cliente> telefoneIgual(String telefone) {
        return (root, query, cb) -> cb.equal(root.get("telefone"), telefone);
    }

    // SELECT * FROM cliente WHERE UPPER (email) LIKE '%email.toUpperCase%'
    public static Specification<Cliente> emailLike(String email) {
        return (root, query, cb)
                -> cb.like(cb.upper(root.get("email")), "%" + email.toUpperCase() + "%");
    }

    // SELECT * FROM cliente WHERE UPPER (cep) LIKE '%cep.toUpperCase%'
    public static Specification<Cliente> cepLike(String cep) {
        return (root, query, cb) -> cb.equal(root.get("cep"), cep);
    }
}
