package com.quiz.religiousquiz.modules.quiz.repository;

import com.quiz.religiousquiz.modules.quiz.domain.QuizSession;

import java.util.Optional;

public interface QuizSessionRepository {
    QuizSession save(QuizSession session);
    Optional<QuizSession> findById(String id);
    void deleteById(String id);
}

