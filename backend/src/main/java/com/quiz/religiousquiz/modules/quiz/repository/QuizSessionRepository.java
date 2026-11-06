package com.quiz.religiousquiz.modules.quiz.repository;

import com.quiz.religiousquiz.modules.quiz.domain.QuizSession;

import java.util.Optional;

public interface QuizSessionRepository {
    QuizSession salvar(QuizSession sessao);
    Optional<QuizSession> buscarPorId(String id);
    void excluirPorId(String id);
    void registrarParticipacao();
    long obterTotalCriados();
    long obterTotalParticipacoes();
}
