package com.quiz.religiousquiz.modules.quiz.dto;

import jakarta.validation.constraints.NotBlank;

public record AnswerSubmissionDto(
        @NotBlank(message = "O ID da pergunta é obrigatório")
        String perguntaId,

        @NotBlank(message = "O ID da resposta selecionada é obrigatório")
        String respostaSelecionadaId
) {}

