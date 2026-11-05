package com.quiz.religiousquiz.modules.quiz.dto;

import java.util.List;

public record PublicQuestionDto(
        String id,
        String pergunta,
        List<AlternativeDto> alternativas,
        String dificuldade
) {}

