package com.quiz.religiousquiz.modules.quiz.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record SubmitQuizRequest(
        @NotBlank(message = "O identificador do quiz é obrigatório")
        String quizId,

        @NotEmpty(message = "As respostas não podem ser vazias")
        List<AnswerSubmissionDto> respostas
) {}

