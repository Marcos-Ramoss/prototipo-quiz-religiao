package com.quiz.religiousquiz.modules.quiz.usecase;

import com.quiz.religiousquiz.modules.quiz.repository.QuizSessionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ExcluirQuizUseCase {

    private final QuizSessionRepository quizSessionRepository;

    public ExcluirQuizUseCase(QuizSessionRepository quizSessionRepository) {
        this.quizSessionRepository = quizSessionRepository;
    }

    @Transactional
    public void executar(String id) {
        quizSessionRepository.excluirPorId(id);
    }
}

