package com.quiz.religiousquiz.modules.quiz.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Estatísticas dinâmicas globais da plataforma de quizzes")
public record QuizStatsResponse(
        @Schema(description = "Total de quizzes criados no sistema", example = "12")
        long quizzesCriados,

        @Schema(description = "Total de perguntas catalogadas e disponíveis", example = "3842")
        long perguntasDisponiveis,

        @Schema(description = "Total de tradições religiosas cadastradas", example = "8")
        int tradicoesCadastradas,

        @Schema(description = "Total de participações/respostas concluídas", example = "5280")
        long participacoesRealizadas
) {}

