package com.quiz.religiousquiz.modules.quiz.service;

import com.quiz.religiousquiz.modules.quiz.domain.QuestionEntity;

import java.util.List;

public interface AiQuestionGenerator {
    List<QuestionEntity> gerarQuestoes(String religiao, String tema, String dificuldade, int quantidade);
}
