package com.quiz.religiousquiz.modules.quiz.dto;

import java.util.List;

public record QuizResultResponse(
        int totalQuestoes,
        int acertos,
        int erros,
        double aproveitamento,
        List<QuestionResultDto> detalhes
) {}

