package com.quiz.religiousquiz.modules.quiz.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record GenerateQuizRequest(
        @NotBlank(message = "A religião deve ser informada")
        String religiao,

        String tema,

        @NotBlank(message = "A dificuldade deve ser informada")
        String dificuldade,

        @NotNull(message = "A quantidade de perguntas deve ser informada")
        Integer quantidade
) {}

