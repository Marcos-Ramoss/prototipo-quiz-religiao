package com.quiz.religiousquiz.modules.quiz.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;

@Schema(description = "Resumo do quiz para catálogo de alunos e gestão do administrador")
public record QuizSummaryResponse(
        @Schema(description = "Identificador único do quiz", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
        String id,

        @Schema(description = "Título descritivo do quiz", example = "Tradições e Festas Judaicas")
        String titulo,

        @Schema(description = "Tradição religiosa ou filosófica", example = "Judaísmo")
        String religiao,

        @Schema(description = "Tema específico trabalhado", example = "Festas e Rituais")
        String tema,

        @Schema(description = "Nível de dificuldade pedagógica", example = "Fácil")
        String dificuldade,

        @Schema(description = "Total de perguntas cadastradas", example = "10")
        int totalPerguntas,

        @Schema(description = "Tempo limite em segundos para realização (0 = sem limite)", example = "600")
        int tempoLimiteSegundos,

        @Schema(description = "Status do quiz no sistema", example = "PUBLICADO")
        String status,

        @Schema(description = "Data e hora de criação")
        Instant criadoEm
) {}

