package com.quiz.religiousquiz.modules.quiz.exception;

public class QuizNotFoundException extends RuntimeException {
    public QuizNotFoundException(String quizId) {
        super("Sessão de quiz não encontrada para o identificador: " + quizId);
    }
}

