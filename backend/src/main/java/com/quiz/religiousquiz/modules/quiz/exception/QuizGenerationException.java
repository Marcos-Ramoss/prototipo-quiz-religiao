package com.quiz.religiousquiz.modules.quiz.exception;

public class QuizGenerationException extends RuntimeException {
    public QuizGenerationException(String message) {
        super(message);
    }

    public QuizGenerationException(String message, Throwable cause) {
        super(message, cause);
    }
}

