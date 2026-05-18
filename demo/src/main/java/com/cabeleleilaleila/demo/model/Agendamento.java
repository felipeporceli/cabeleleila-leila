package com.cabeleleilaleila.demo.model;

import com.cabeleleilaleila.demo.model.enums.StatusAgendamentoEnum;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "agendamento")
public class Agendamento {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Integer id;

        @ManyToOne
        @JoinColumn(name = "cliente_id", nullable = false)
        private Cliente cliente;

        @ManyToOne
        @JoinColumn(name = "cabeleireiro_id", nullable = false)
        private Cabeleireiro cabeleireiro;

        @Column(name = "data_agendamento", nullable = false)
        @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
        private LocalDateTime dataAgendamento;

        @Column(name = "observacoes", length = 200)
        private String observacoes;

        @Enumerated(EnumType.STRING)
        @Column(name = "status_agendamento", nullable = false)
        private StatusAgendamentoEnum statusAgendamento = StatusAgendamentoEnum.AGENDADO;


}
