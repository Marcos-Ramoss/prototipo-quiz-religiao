package com.quiz.religiousquiz.common.dto;

public record ErrorResponse(
        String error,
        String message,
        long timestamp
) {}

