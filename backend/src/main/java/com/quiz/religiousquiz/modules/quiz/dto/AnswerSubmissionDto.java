package com.quiz.religiousquiz.modules.quiz.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Resposta do usuário para uma pergunta individual")
public record AnswerSubmissionDto(
        @Schema(description = "Identificador da pergunta respondida", example = "q-1")
        @NotBlank(message = "O ID da pergunta é obrigatório")
        String perguntaId,

        @Schema(description = "Identificador da alternativa selecionada pelo usuário", example = "a")
        @NotBlank(message = "O ID da resposta selecionada é obrigatório")
        String respostaSelecionadaId
) {}

