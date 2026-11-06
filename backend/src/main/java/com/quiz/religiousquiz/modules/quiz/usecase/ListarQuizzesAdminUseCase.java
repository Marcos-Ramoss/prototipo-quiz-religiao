package com.quiz.religiousquiz.modules.quiz.usecase;

import com.quiz.religiousquiz.modules.quiz.domain.QuizSession;
import com.quiz.religiousquiz.modules.quiz.dto.QuizSummaryResponse;
import com.quiz.religiousquiz.modules.quiz.repository.QuizSessionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListarQuizzesAdminUseCase {

    private final QuizSessionRepository repository;

    public ListarQuizzesAdminUseCase(QuizSessionRepository repository) {
        this.repository = repository;
    }

    public List<QuizSummaryResponse> executar() {
        return repository.listarTodos().stream()
                .map(this::converterParaResumo)
                .toList();
    }

    private QuizSummaryResponse converterParaResumo(QuizSession s) {
        return new QuizSummaryResponse(
                s.getId(),
                s.getTitulo(),
                s.getReligiao(),
                s.getTema(),
                s.getDificuldade(),
                s.getQuestoes() != null ? s.getQuestoes().size() : 0,
                s.getTempoLimiteSegundos(),
                s.getStatus(),
                s.getCriadoEm()
        );
    }
}

