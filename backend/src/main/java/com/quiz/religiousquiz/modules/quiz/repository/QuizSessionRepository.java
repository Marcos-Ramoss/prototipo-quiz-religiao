package com.quiz.religiousquiz.modules.quiz.repository;

import com.quiz.religiousquiz.modules.quiz.domain.QuizSession;

import java.util.List;
import java.util.Optional;

public interface QuizSessionRepository {
    QuizSession salvar(QuizSession sessao);
    Optional<QuizSession> buscarPorId(String id);
    List<QuizSession> listarPublicados();
    List<QuizSession> listarTodos();
    void publicarQuiz(String id, String titulo, int tempoLimiteSegundos);
    void excluirPorId(String id);
    void registrarParticipacao();
    default void registrarParticipacao(String quizId, int total, int acertos, int erros, double aproveitamento) {
        registrarParticipacao();
    }
    long obterTotalCriados();
    long obterTotalParticipacoes();
}
