package com.quiz.religiousquiz.modules.quiz.usecase;

import com.quiz.religiousquiz.modules.quiz.domain.QuestionEntity;
import com.quiz.religiousquiz.modules.quiz.domain.QuizSession;
import com.quiz.religiousquiz.modules.quiz.dto.AlternativeDto;
import com.quiz.religiousquiz.modules.quiz.dto.AnswerSubmissionDto;
import com.quiz.religiousquiz.modules.quiz.dto.QuestionResultDto;
import com.quiz.religiousquiz.modules.quiz.dto.QuizResultResponse;
import com.quiz.religiousquiz.modules.quiz.dto.SubmitQuizRequest;
import com.quiz.religiousquiz.modules.quiz.exception.InvalidQuizParametersException;
import com.quiz.religiousquiz.modules.quiz.exception.QuizNotFoundException;
import com.quiz.religiousquiz.modules.quiz.repository.QuizSessionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SubmitQuizUseCase {

    private final QuizSessionRepository sessionRepository;

    public SubmitQuizUseCase(QuizSessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    public QuizResultResponse execute(SubmitQuizRequest request) {
        validateRequest(request);

        QuizSession session = sessionRepository.findById(request.quizId())
                .orElseThrow(() -> new QuizNotFoundException(request.quizId()));

        Map<String, String> userAnswersMap = new java.util.HashMap<>();
        if (request.respostas() != null) {
            for (AnswerSubmissionDto ans : request.respostas()) {
                if (ans != null && ans.perguntaId() != null) {
                    userAnswersMap.put(ans.perguntaId(), ans.respostaSelecionadaId() != null ? ans.respostaSelecionadaId() : "");
                }
            }
        }

        List<QuestionResultDto> detalhes = session.getQuestoes().stream()
                .map(question -> evaluateQuestion(question, userAnswersMap))
                .toList();

        int total = detalhes.size();
        int acertos = (int) detalhes.stream().filter(QuestionResultDto::acertou).count();
        int erros = total - acertos;
        double aproveitamento = total > 0
                ? Math.round(((double) acertos / total) * 10000.0) / 100.0
                : 0.0;

        return new QuizResultResponse(total, acertos, erros, aproveitamento, detalhes);
    }

    private void validateRequest(SubmitQuizRequest request) {
        if (request.quizId() == null || request.quizId().isBlank()) {
            throw new InvalidQuizParametersException("O identificador do quiz é obrigatório.");
        }
        if (request.respostas() == null || request.respostas().isEmpty()) {
            throw new InvalidQuizParametersException("A lista de respostas não pode ser vazia.");
        }
    }

    private QuestionResultDto evaluateQuestion(QuestionEntity question, Map<String, String> userAnswersMap) {
        String respostaSelecionada = userAnswersMap.getOrDefault(question.getId(), "");
        boolean acertou = question.getRespostaCorretaId() != null
                && question.getRespostaCorretaId().trim().equalsIgnoreCase(respostaSelecionada.trim());

        List<AlternativeDto> alternativas = question.getAlternativas().stream()
                .map(alt -> new AlternativeDto(alt.getId(), alt.getTexto()))
                .toList();

        return new QuestionResultDto(
                question.getId(),
                question.getPergunta(),
                alternativas,
                respostaSelecionada,
                question.getRespostaCorretaId(),
                acertou,
                question.getExplicacao(),
                question.getReferencia()
        );
    }
}

