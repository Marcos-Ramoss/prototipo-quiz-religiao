package com.quiz.religiousquiz.modules.quiz.repository;

import com.quiz.religiousquiz.modules.quiz.domain.AlternativeEntity;
import com.quiz.religiousquiz.modules.quiz.domain.QuestionEntity;
import com.quiz.religiousquiz.modules.quiz.domain.QuizSession;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
class QuizSessionRepositoryImplTest {

    @Autowired
    private QuizSessionRepository repository;

    @Test
    @DisplayName("Deve salvar e recuperar uma sessão de quiz no banco de dados com suas perguntas")
    void deveSalvarERecuperarSessao() {
        String id = UUID.randomUUID().toString();
        QuestionEntity q1 = new QuestionEntity(
                "q1", "O que significa Shabat?",
                List.of(new AlternativeEntity("A", "Descanso sagrado"), new AlternativeEntity("B", "Jejum")),
                "A", "Shabat é o dia de descanso.", "Gênesis 2", "Fácil"
        );

        QuizSession sessao = new QuizSession(
                id, "Judaísmo", "Festas", "Fácil",
                Instant.now(), Instant.now().plusSeconds(3600),
                List.of(q1)
        );

        repository.salvar(sessao);

        Optional<QuizSession> recuperado = repository.buscarPorId(id);
        assertTrue(recuperado.isPresent());
        assertEquals("Judaísmo", recuperado.get().getReligiao());
        assertEquals(1, recuperado.get().getQuestoes().size());
        assertEquals("q1", recuperado.get().getQuestoes().get(0).getId());
        assertEquals("A", recuperado.get().getQuestoes().get(0).getRespostaCorretaId());
        assertEquals(2, recuperado.get().getQuestoes().get(0).getAlternativas().size());
    }
}

