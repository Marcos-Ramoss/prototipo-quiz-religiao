package com.quiz.religiousquiz.modules.quiz.exception;

public class QuizExpiredException extends RuntimeException {
    public QuizExpiredException(String quizId) {
        super("A sessão do quiz expirou. Por favor, inicie um novo quiz: " + quizId);
    }
}

