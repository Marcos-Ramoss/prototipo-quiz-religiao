package com.quiz.religiousquiz.modules.quiz.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "Dados da sessão ativa do quiz para resolução pelo aluno ou revisão do admin")
public record QuizSessionResponse(
        @Schema(description = "Identificador único da sessão do quiz", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
        String quizId,

        @Schema(description = "Título público do quiz", example = "Tradições e Festas Judaicas")
        String titulo,

        @Schema(description = "Tradição religiosa selecionada", example = "Judaísmo")
        String religiao,

        @Schema(description = "Tema específico trabalhado", example = "Festas e Rituais")
        String tema,

        @Schema(description = "Nível de dificuldade pedagógica", example = "Fácil")
        String dificuldade,

        @Schema(description = "Status do quiz", example = "PUBLICADO")
        String status,

        @Schema(description = "Tempo limite em segundos (0 = ilimitado)", example = "600")
        int tempoLimiteSegundos,

        @Schema(description = "Lista de questões públicas (sem gabarito para proteção de respostas)")
        List<PublicQuestionDto> questoes
) {
    public QuizSessionResponse(String quizId, String religiao, String tema, String dificuldade, List<PublicQuestionDto> questoes) {
        this(quizId, "Quiz sobre " + (religiao != null ? religiao : "Tradições"), religiao, tema, dificuldade, "PUBLICADO", 0, questoes);
    }
}
