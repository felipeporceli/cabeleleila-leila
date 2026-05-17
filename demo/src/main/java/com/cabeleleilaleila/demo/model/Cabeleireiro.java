package com.cabeleleilaleila.demo.model;

import com.cabeleleilaleila.demo.model.enums.EspecialidadeEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "cabeleireiro")
public class Cabeleireiro {

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

    @Enumerated(EnumType.STRING)
    @Column(name = "especialidade", nullable = false)
    private EspecialidadeEnum especialidade;
}
