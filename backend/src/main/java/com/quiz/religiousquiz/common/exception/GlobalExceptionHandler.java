package com.quiz.religiousquiz.common.exception;

import com.quiz.religiousquiz.common.dto.ErrorResponse;
import com.quiz.religiousquiz.modules.quiz.exception.InvalidQuizParametersException;
import com.quiz.religiousquiz.modules.quiz.exception.QuizExpiredException;
import com.quiz.religiousquiz.modules.quiz.exception.QuizGenerationException;
import com.quiz.religiousquiz.modules.quiz.exception.QuizNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(QuizNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleQuizNotFound(QuizNotFoundException ex) {
        log.warn("Sessão não encontrada: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse("QUIZ_NOT_FOUND", ex.getMessage(), System.currentTimeMillis()));
    }

    @ExceptionHandler(QuizExpiredException.class)
    public ResponseEntity<ErrorResponse> handleQuizExpired(QuizExpiredException ex) {
        log.warn("Sessão expirada: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.GONE)
                .body(new ErrorResponse("QUIZ_EXPIRED", ex.getMessage(), System.currentTimeMillis()));
    }

    @ExceptionHandler(InvalidQuizParametersException.class)
    public ResponseEntity<ErrorResponse> handleInvalidParams(InvalidQuizParametersException ex) {
        log.warn("Parâmetros inválidos: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse("INVALID_PARAMETERS", ex.getMessage(), System.currentTimeMillis()));
    }

    @ExceptionHandler(QuizGenerationException.class)
    public ResponseEntity<ErrorResponse> handleGenerationError(QuizGenerationException ex) {
        log.error("Erro na geração do quiz: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                .body(new ErrorResponse("AI_GENERATION_ERROR", ex.getMessage(), System.currentTimeMillis()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(MethodArgumentNotValidException ex) {
        String errors = ex.getBindingResult().getFieldErrors().stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .collect(Collectors.joining("; "));
        log.warn("Falha de validação de DTO: {}", errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse("VALIDATION_ERROR", errors, System.currentTimeMillis()));
    }

    @ExceptionHandler(org.springframework.http.converter.HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadable(org.springframework.http.converter.HttpMessageNotReadableException ex) {
        log.warn("Payload JSON inválido ou malformado: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse("INVALID_JSON_PAYLOAD", "O corpo da requisição contém um JSON inválido ou incompatível.", System.currentTimeMillis()));
    }

    @ExceptionHandler(org.springframework.web.servlet.resource.NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoResourceFound(org.springframework.web.servlet.resource.NoResourceFoundException ex) {
        log.warn("Recurso estático ou rota não encontrada: {}", ex.getResourcePath());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse("RESOURCE_NOT_FOUND", "Recurso não encontrado: /" + ex.getResourcePath(), System.currentTimeMillis()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        log.error("Erro inesperado no servidor: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("INTERNAL_SERVER_ERROR", "Ocorreu um erro interno ao processar a solicitação.", System.currentTimeMillis()));
    }
}

