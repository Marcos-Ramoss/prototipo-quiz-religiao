package com.quiz.religiousquiz.modules.quiz.dto;

import java.util.List;

public record QuizSessionResponse(
        String quizId,
        String religiao,
        String tema,
        String dificuldade,
        List<PublicQuestionDto> questoes
) {}

