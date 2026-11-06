package com.quiz.religiousquiz.modules.quiz.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Dados para solicitar a geração de um novo quiz por Inteligência Artificial")
public record GenerateQuizRequest(
        @Schema(description = "Tradição religiosa ou filosófica", example = "Cristianismo")
        @NotBlank(message = "A religião deve ser informada")
        String religiao,

        @Schema(description = "Tema ou subtema específico (opcional)", example = "Parábolas de Jesus")
        String tema,

        @Schema(description = "Nível de dificuldade", example = "Médio", allowableValues = {"Fácil", "Médio", "Difícil"})
        @NotBlank(message = "A dificuldade deve ser informada")
        String dificuldade,

        @Schema(description = "Quantidade de perguntas a serem geradas", example = "5")
        @NotNull(message = "A quantidade de perguntas deve ser informada")
        Integer quantidade,

        @Schema(description = "Título personalizado do quiz (opcional)", example = "Parábolas e Ensinamentos")
        String titulo,

        @Schema(description = "Tempo limite em segundos para realização (opcional, 0 = ilimitado)", example = "600")
        Integer tempoLimiteSegundos,

        @Schema(description = "Status inicial do quiz (RASCUNHO ou PUBLICADO)", example = "PUBLICADO")
        String status
) {
    public GenerateQuizRequest(String religiao, String tema, String dificuldade, Integer quantidade) {
        this(religiao, tema, dificuldade, quantidade, null, 0, "PUBLICADO");
    }
}
