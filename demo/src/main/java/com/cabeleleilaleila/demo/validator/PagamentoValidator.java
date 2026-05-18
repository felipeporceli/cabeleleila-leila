package com.cabeleleilaleila.demo.validator;

import com.cabeleleilaleila.demo.dto.PagamentoRequestDTO;
import com.cabeleleilaleila.demo.exception.PagamentoNaoEncontradoException;
import com.cabeleleilaleila.demo.exception.RegistroDuplicadoException;
import com.cabeleleilaleila.demo.model.Pagamento;
import com.cabeleleilaleila.demo.repository.PagamentoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PagamentoValidator {

    private final PagamentoRepository pagamentoRepository;

    public void validarSalvar(PagamentoRequestDTO dto) {
        if (pagamentoRepository.existsByAgendamentoId(dto.getAgendamentoId())) {
            throw new RegistroDuplicadoException(
                    "Já existe um pagamento para este agendamento.");
        }
    }

    public Pagamento validarAtualizar(Integer id) {
        return pagamentoRepository.findById(id)
                .orElseThrow(() ->
                        new PagamentoNaoEncontradoException("Pagamento não encontrado.")
                );
    }

    public void validarDeletar(Integer id) {
        pagamentoRepository.findById(id)
                .orElseThrow(() ->
                        new PagamentoNaoEncontradoException("Pagamento não encontrado.")
                );
    }

}