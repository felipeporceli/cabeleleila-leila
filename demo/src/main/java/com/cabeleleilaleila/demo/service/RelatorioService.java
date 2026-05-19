package com.cabeleleilaleila.demo.service;

import com.cabeleleilaleila.demo.dto.RelatorioSemanalDTO;

import java.time.LocalDateTime;

public interface RelatorioService {

    RelatorioSemanalDTO gerarRelatorioSemanal(LocalDateTime dataInicio, LocalDateTime dataFim);

}