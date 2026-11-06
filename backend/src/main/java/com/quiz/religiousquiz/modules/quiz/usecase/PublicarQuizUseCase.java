package com.quiz.religiousquiz.modules.quiz.usecase;

import com.quiz.religiousquiz.modules.quiz.domain.QuizSession;
import com.quiz.religiousquiz.modules.quiz.dto.PublishQuizRequest;
import com.quiz.religiousquiz.modules.quiz.dto.QuizSummaryResponse;
import com.quiz.religiousquiz.modules.quiz.exception.InvalidQuizParametersException;
import com.quiz.religiousquiz.modules.quiz.exception.QuizNotFoundException;
import com.quiz.religiousquiz.modules.quiz.repository.QuizSessionRepository;
import org.springframework.stereotype.Service;

@Service
public class PublicarQuizUseCase {

    private final QuizSessionRepository repository;

    public PublicarQuizUseCase(QuizSessionRepository repository) {
        this.repository = repository;
    }

    public QuizSummaryResponse executar(String quizId, PublishQuizRequest request) {
        if (quizId == null || quizId.isBlank()) {
            throw new InvalidQuizParametersException("Identificador do quiz é obrigatório.");
        }

        QuizSession existente = repository.buscarPorId(quizId)
                .orElseThrow(() -> new QuizNotFoundException(quizId));

        String tituloFinal = (request != null && request.titulo() != null && !request.titulo().isBlank())
                ? request.titulo()
                : existente.getTitulo();

        int tempoLimite = (request != null && request.tempoLimiteSegundos() != null)
                ? Math.max(0, request.tempoLimiteSegundos())
                : existente.getTempoLimiteSegundos();

        repository.publicarQuiz(quizId, tituloFinal, tempoLimite);

        return new QuizSummaryResponse(
                existente.getId(),
                tituloFinal,
                existente.getReligiao(),
                existente.getTema(),
                existente.getDificuldade(),
                existente.getQuestoes() != null ? existente.getQuestoes().size() : 0,
                tempoLimite,
                "PUBLICADO",
                existente.getCriadoEm()
        );
    }
}

