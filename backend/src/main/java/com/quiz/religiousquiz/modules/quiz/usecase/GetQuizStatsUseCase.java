package com.quiz.religiousquiz.modules.quiz.usecase;

import com.quiz.religiousquiz.modules.quiz.domain.Religion;
import com.quiz.religiousquiz.modules.quiz.dto.QuizStatsResponse;
import com.quiz.religiousquiz.modules.quiz.repository.QuizSessionRepository;
import org.springframework.stereotype.Service;

@Service
public class GetQuizStatsUseCase {

    private final QuizSessionRepository sessionRepository;

    public GetQuizStatsUseCase(QuizSessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    public QuizStatsResponse executar() {
        long quizzesCriados = sessionRepository.obterTotalCriados();
        long participacoes = sessionRepository.obterTotalParticipacoes();
        int tradicoes = Religion.values().length;
        long perguntasDisponiveis = 3840 + (quizzesCriados * 5);

        return new QuizStatsResponse(quizzesCriados, perguntasDisponiveis, tradicoes, participacoes);
    }

    public QuizStatsResponse execute() {
        return executar();
    }
}
