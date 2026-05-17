package com.cabeleleilaleila.demo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "cliente")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "nome", nullable = false, length = 200)
    private String nome;

    @Column(name = "cpf", nullable = false, unique = true, length = 15)
    private String cpf;

    @Column(name = "senha", nullable = false, length = 250)
    private String senha;

    @Column(name = "email", nullable = false, unique = true, length = 200)
    private String email;

    @Column(name = "telefone", length = 25)
    private String telefone;

    @Column(name = "logradouro", length = 200)
    private String logradouro;

    @Column(name = "bairro", length = 200)
    private String bairro;

    @Column(name = "numero", length = 5)
    private String numero;

    @Column(name = "cidade", length = 200)
    private String cidade;

    @Column(name = "complemento", length = 200)
    private String complemento;

    @Column(name = "cep", length = 10)
    private String cep;
}
