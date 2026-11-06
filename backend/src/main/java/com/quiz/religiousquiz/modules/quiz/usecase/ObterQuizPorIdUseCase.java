package com.quiz.religiousquiz.modules.quiz.usecase;

import com.quiz.religiousquiz.modules.quiz.domain.QuizSession;
import com.quiz.religiousquiz.modules.quiz.dto.AlternativeDto;
import com.quiz.religiousquiz.modules.quiz.dto.PublicQuestionDto;
import com.quiz.religiousquiz.modules.quiz.dto.QuizSessionResponse;
import com.quiz.religiousquiz.modules.quiz.exception.QuizNotFoundException;
import com.quiz.religiousquiz.modules.quiz.repository.QuizSessionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ObterQuizPorIdUseCase {

    private final QuizSessionRepository quizSessionRepository;

    public ObterQuizPorIdUseCase(QuizSessionRepository quizSessionRepository) {
        this.quizSessionRepository = quizSessionRepository;
    }

    @Transactional(readOnly = true)
    public QuizSessionResponse executar(String id) {
        QuizSession sessao = quizSessionRepository.buscarPorId(id)
                .orElseThrow(() -> new QuizNotFoundException(id));

        List<PublicQuestionDto> questoesPublicas = sessao.getQuestoes().stream()
                .map(q -> new PublicQuestionDto(
                        q.getId(),
                        q.getPergunta(),
                        q.getAlternativas().stream()
                                .map(alt -> new AlternativeDto(alt.getId(), alt.getTexto()))
                                .toList(),
                        q.getDificuldade()
                ))
                .toList();

        return new QuizSessionResponse(
                sessao.getId(),
                sessao.getTitulo(),
                sessao.getReligiao(),
                sessao.getTema(),
                sessao.getDificuldade(),
                sessao.getStatus(),
                sessao.getTempoLimiteSegundos(),
                questoesPublicas
        );
    }
}

