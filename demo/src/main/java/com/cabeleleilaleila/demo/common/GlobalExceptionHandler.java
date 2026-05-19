package com.cabeleleilaleila.demo.common;

import com.cabeleleilaleila.demo.dto.ErroCampo;
import com.cabeleleilaleila.demo.dto.ErroResposta;
import com.cabeleleilaleila.demo.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import tools.jackson.databind.exc.InvalidFormatException;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /* Esse metodo captura a exceção "RegistroDuplicadoException" lançada na aplicação e retorna HTTP 409 (conflict)
    Além de construir uma resposta padronizada com o "ErroResposta" e retornar para o usuário no corpo da response */
    @ExceptionHandler(RegistroDuplicadoException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErroResposta handleRegistroDuplicado(RegistroDuplicadoException e) {
        return ErroResposta.respostaConflito(e.getMessage());
    }

    /* Esse metodo captura a exceção "CampoInvalidoException" lançada na aplicação e retorna HTTP 422 (Unprocessable
    Entity). Além de informar qual campo está válido ele também envia a resposta no corpo da response. */
    @ExceptionHandler(CampoInvalidoException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public ErroResposta handleCampoInvalido(CampoInvalidoException e) {
        return erro422(e.getCampo(), e.getMessage());
    }

    /* Esse metodo trata erros de leitura do JSON enviados na requisição nos campos ENUMs e Datas. Caso o JSON esteja
    formatado incorretamente ele retorna com mensagem personalizada. Impedindo que cheguem ao servidor.*/
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public ErroResposta handleJsonInvalido(HttpMessageNotReadableException ex) {

        if (ex.getCause() instanceof InvalidFormatException ife) {
            String campo = ife.getPath().get(0).getPropertyName();
            String tipo = ife.getTargetType().getSimpleName();

            String mensagem = tipo.equals("LocalDate")
                    ? "Data inválida. Use dd/MM/yyyy"
                    : "Valor invalido";

            return erro422(campo, mensagem);
        }

        return erro422("json", "JSON inválido");
    }

    /* Esse metodo captura erros do Jakarta Validation (@NotBlank, @NotNull, @Past, etc.) Percorre todos os campos
    inválidos e retorna com uma mensagem padronizada. */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public ErroResposta handleBeanValidation(MethodArgumentNotValidException ex) {

        List<ErroCampo> erros = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(e -> new ErroCampo(e.getField(), e.getDefaultMessage()))
                .toList();

        return new ErroResposta(422, "Erro de validação", erros);
    }

    /* Metodo auxiliar */
    private ErroResposta erro422(String campo, String mensagem) {
        return new ErroResposta(
                422,
                "Erro de validação",
                List.of(new ErroCampo(campo, mensagem))
        );
    }

    @ExceptionHandler(ClienteNaoEncontradoException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErroResposta handleClienteCadastradoNome(ClienteNaoEncontradoException e) {
        return new ErroResposta(
                404,
                "Cliente não encontrado",
                List.of(new ErroCampo("id", e.getMessage()))
        );
    }

    @ExceptionHandler(AgendamentoNaoEncontradoException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErroResposta handleAgendamentoNaoEncontrado(AgendamentoNaoEncontradoException e) {
        return new ErroResposta(
                404,
                "Agendamento nao encontrado",
                List.of(new ErroCampo("id", e.getMessage()))
        );
    }

    @ExceptionHandler(PagamentoNaoEncontradoException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErroResposta handlePagamentoNaoEncontrado(PagamentoNaoEncontradoException e) {
        return new ErroResposta(
                404,
                "Pagamento nao encontrado",
                List.of(new ErroCampo("id", e.getMessage()))
        );
    }

    @ExceptionHandler(HorarioDisponivelNaoEncontradoException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErroResposta handleHorarioDisponivelNaoEncontrado(HorarioDisponivelNaoEncontradoException e) {
        return new ErroResposta(
                404,
                "Horario disponivel nao encontrado",
                List.of(new ErroCampo("id", e.getMessage()))
        );
    }

    @ExceptionHandler(AlteracaoNaoPermitidaException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public ErroResposta handleAlteracaoNaoPermitida(AlteracaoNaoPermitidaException e) {
        return new ErroResposta(
                422,
                "Alteração não permitida",
                List.of(new ErroCampo("dataAgendamento", e.getMessage()))
        );
    }

}
