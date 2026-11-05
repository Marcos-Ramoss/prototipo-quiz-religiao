package com.quiz.religiousquiz.modules.quiz.repository;

import com.quiz.religiousquiz.modules.quiz.domain.QuizSession;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryQuizSessionRepository implements QuizSessionRepository {

    private final Map<String, QuizSession> storage = new ConcurrentHashMap<>();

    @Override
    public QuizSession save(QuizSession session) {
        storage.put(session.getId(), session);
        return session;
    }

    @Override
    public Optional<QuizSession> findById(String id) {
        QuizSession session = storage.get(id);
        if (session == null) {
            return Optional.empty();
        }
        if (session.isExpired()) {
            storage.remove(id);
            return Optional.empty();
        }
        return Optional.of(session);
    }

    @Override
    public void deleteById(String id) {
        storage.remove(id);
    }
}

