package com.quiz.religiousquiz.modules.quiz.controller;

import com.quiz.religiousquiz.modules.quiz.dto.GenerateQuizRequest;
import com.quiz.religiousquiz.modules.quiz.dto.QuizResultResponse;
import com.quiz.religiousquiz.modules.quiz.dto.QuizSessionResponse;
import com.quiz.religiousquiz.modules.quiz.dto.SubmitQuizRequest;
import com.quiz.religiousquiz.modules.quiz.usecase.GenerateQuizUseCase;
import com.quiz.religiousquiz.modules.quiz.usecase.SubmitQuizUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/quiz")
public class QuizController {

    private final GenerateQuizUseCase generateQuizUseCase;
    private final SubmitQuizUseCase submitQuizUseCase;

    public QuizController(GenerateQuizUseCase generateQuizUseCase, SubmitQuizUseCase submitQuizUseCase) {
        this.generateQuizUseCase = generateQuizUseCase;
        this.submitQuizUseCase = submitQuizUseCase;
    }

    @PostMapping("/generate")
    public ResponseEntity<QuizSessionResponse> generateQuiz(@Valid @RequestBody GenerateQuizRequest request) {
        QuizSessionResponse response = generateQuizUseCase.execute(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/submit")
    public ResponseEntity<QuizResultResponse> submitQuiz(@Valid @RequestBody SubmitQuizRequest request) {
        QuizResultResponse response = submitQuizUseCase.execute(request);
        return ResponseEntity.ok(response);
    }
}

