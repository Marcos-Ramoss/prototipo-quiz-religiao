package com.quiz.religiousquiz.modules.quiz.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

@Schema(description = "Payload para submissão e correção de um quiz concluído")
public record SubmitQuizRequest(
        @Schema(description = "UUID da sessão do quiz", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
        @NotBlank(message = "O identificador do quiz é obrigatório")
        String quizId,

        @Schema(description = "Lista contendo as respostas para cada pergunta do quiz")
        @NotEmpty(message = "As respostas não podem ser vazias")
        List<AnswerSubmissionDto> respostas
) {}

