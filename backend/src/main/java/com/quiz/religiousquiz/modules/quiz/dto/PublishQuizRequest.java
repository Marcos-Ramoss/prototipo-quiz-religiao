package com.quiz.religiousquiz.modules.quiz.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Requisição para publicação e configuração de regras do quiz pelo administrador")
public record PublishQuizRequest(
        @Schema(description = "Título público do quiz", example = "Parábolas e Ensinamentos do Cristianismo")
        String titulo,

        @Schema(description = "Tempo limite em segundos para realização (ex: 600 para 10 minutos, 0 para ilimitado)", example = "600")
        Integer tempoLimiteSegundos
) {}

