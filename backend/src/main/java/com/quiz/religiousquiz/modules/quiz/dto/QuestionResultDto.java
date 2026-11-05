package com.quiz.religiousquiz.modules.quiz.dto;

import java.util.List;

public record QuestionResultDto(
        String perguntaId,
        String pergunta,
        List<AlternativeDto> alternativas,
        String respostaSelecionadaId,
        String respostaCorretaId,
        boolean acertou,
        String explicacao,
        String referencia
) {}

