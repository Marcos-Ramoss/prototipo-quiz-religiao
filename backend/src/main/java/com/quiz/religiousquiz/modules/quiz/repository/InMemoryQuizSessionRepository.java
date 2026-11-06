package com.quiz.religiousquiz.modules.quiz.repository;

import com.quiz.religiousquiz.modules.quiz.domain.QuizSession;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InMemoryQuizSessionRepository implements QuizSessionRepository {

    private final Map<String, QuizSession> armazenamento = new ConcurrentHashMap<>();
    private final AtomicLong totalCriadosContador = new AtomicLong(12);
    private final AtomicLong totalParticipacoesContador = new AtomicLong(5280);

    @Override
    public QuizSession salvar(QuizSession sessao) {
        boolean ehNovo = !armazenamento.containsKey(sessao.getId());
        armazenamento.put(sessao.getId(), sessao);
        if (ehNovo) {
            totalCriadosContador.incrementAndGet();
        }
        return sessao;
    }

    @Override
    public Optional<QuizSession> buscarPorId(String id) {
        QuizSession sessao = armazenamento.get(id);
        if (sessao == null) {
            return Optional.empty();
        }
        if (sessao.isExpired()) {
            armazenamento.remove(id);
            return Optional.empty();
        }
        return Optional.of(sessao);
    }

    @Override
    public void excluirPorId(String id) {
        armazenamento.remove(id);
    }

    @Override
    public void registrarParticipacao() {
        totalParticipacoesContador.incrementAndGet();
    }

    @Override
    public long obterTotalCriados() {
        return totalCriadosContador.get();
    }

    @Override
    public long obterTotalParticipacoes() {
        return totalParticipacoesContador.get();
    }
}
