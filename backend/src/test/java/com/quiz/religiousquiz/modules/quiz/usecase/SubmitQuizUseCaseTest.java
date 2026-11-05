package com.quiz.religiousquiz.modules.quiz.usecase;

import com.quiz.religiousquiz.modules.quiz.domain.AlternativeEntity;
import com.quiz.religiousquiz.modules.quiz.domain.QuestionEntity;
import com.quiz.religiousquiz.modules.quiz.domain.QuizSession;
import com.quiz.religiousquiz.modules.quiz.dto.AnswerSubmissionDto;
import com.quiz.religiousquiz.modules.quiz.dto.QuestionResultDto;
import com.quiz.religiousquiz.modules.quiz.dto.QuizResultResponse;
import com.quiz.religiousquiz.modules.quiz.dto.SubmitQuizRequest;
import com.quiz.religiousquiz.modules.quiz.exception.QuizNotFoundException;
import com.quiz.religiousquiz.modules.quiz.repository.QuizSessionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SubmitQuizUseCaseTest {

    @Mock
    private QuizSessionRepository sessionRepository;

    private SubmitQuizUseCase submitQuizUseCase;

    @BeforeEach
    void setUp() {
        submitQuizUseCase = new SubmitQuizUseCase(sessionRepository);
    }

    @Test
    @DisplayName("Deve corrigir quiz corretamente calculando acertos e aproveitamento")
    void deveCorrigirQuizComSucesso() {
        String quizId = "sessao-123";

        QuestionEntity q1 = new QuestionEntity("q1", "Pergunta 1", List.of(
                new AlternativeEntity("A", "Opção A"),
                new AlternativeEntity("B", "Opção B")
        ), "A", "Explicação 1", "Ref 1", "Fácil");

        QuestionEntity q2 = new QuestionEntity("q2", "Pergunta 2", List.of(
                new AlternativeEntity("A", "Opção A"),
                new AlternativeEntity("B", "Opção B")
        ), "B", "Explicação 2", "Ref 2", "Fácil");

        QuizSession session = new QuizSession(
                quizId, "Budismo", "Nobre Caminho Óctuplo", "Fácil",
                Instant.now(), Instant.now().plusSeconds(3600),
                List.of(q1, q2)
        );

        when(sessionRepository.findById(quizId)).thenReturn(Optional.of(session));

        SubmitQuizRequest request = new SubmitQuizRequest(quizId, List.of(
                new AnswerSubmissionDto("q1", "A"),
                new AnswerSubmissionDto("q2", "A")
        ));

        QuizResultResponse response = submitQuizUseCase.execute(request);

        assertNotNull(response);
        assertEquals(2, response.totalQuestoes());
        assertEquals(1, response.acertos());
        assertEquals(1, response.erros());
        assertEquals(50.0, response.aproveitamento());
        assertEquals(2, response.detalhes().size());

        assertTrue(response.detalhes().get(0).acertou());
        assertEquals("A", response.detalhes().get(0).respostaCorretaId());
        assertEquals("Explicação 1", response.detalhes().get(0).explicacao());

        assertFalse(response.detalhes().get(1).acertou());
        assertEquals("B", response.detalhes().get(1).respostaCorretaId());
    }

    @Test
    @DisplayName("Deve lançar QuizNotFoundException se a sessão não existir")
    void deveLancarExcecaoQuandoSessaoNaoEncontrada() {
        when(sessionRepository.findById("inexistente")).thenReturn(Optional.empty());

        SubmitQuizRequest request = new SubmitQuizRequest("inexistente", List.of(
                new AnswerSubmissionDto("q1", "A")
        ));

        assertThrows(QuizNotFoundException.class, () -> submitQuizUseCase.execute(request));
    }
}

