package com.quiz.religiousquiz.modules.quiz.usecase;

import com.quiz.religiousquiz.modules.quiz.domain.AlternativeEntity;
import com.quiz.religiousquiz.modules.quiz.domain.QuestionEntity;
import com.quiz.religiousquiz.modules.quiz.domain.QuizSession;
import com.quiz.religiousquiz.modules.quiz.dto.GenerateQuizRequest;
import com.quiz.religiousquiz.modules.quiz.dto.QuizSessionResponse;
import com.quiz.religiousquiz.modules.quiz.exception.InvalidQuizParametersException;
import com.quiz.religiousquiz.modules.quiz.repository.QuizSessionRepository;
import com.quiz.religiousquiz.modules.quiz.service.AiQuestionGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GenerateQuizUseCaseTest {

    @Mock
    private AiQuestionGenerator aiQuestionGenerator;

    @Mock
    private QuizSessionRepository sessionRepository;

    private GenerateQuizUseCase generateQuizUseCase;

    @BeforeEach
    void setUp() {
        generateQuizUseCase = new GenerateQuizUseCase(aiQuestionGenerator, sessionRepository, 60);
    }

    @Test
    @DisplayName("Deve gerar quiz com sucesso e ocultar gabarito na resposta")
    void deveGerarQuizComSucessoEOcultarGabarito() {
        GenerateQuizRequest request = new GenerateQuizRequest(
                "Cristianismo", "Parábolas", "Fácil", 5
        );

        List<QuestionEntity> mockQuestions = List.of(
                new QuestionEntity("q1", "Onde Jesus nasceu?", List.of(
                        new AlternativeEntity("A", "Belém"),
                        new AlternativeEntity("B", "Nazaré"),
                        new AlternativeEntity("C", "Jerusalém"),
                        new AlternativeEntity("D", "Roma")
                ), "A", "Jesus nasceu em Belém de Judá.", "Evangelho de Mateus 2:1", "Fácil")
        );

        when(aiQuestionGenerator.gerarQuestoes(eq("Cristianismo"), eq("Parábolas"), eq("Fácil"), eq(5)))
                .thenReturn(mockQuestions);

        QuizSessionResponse response = generateQuizUseCase.executar(request);

        assertNotNull(response);
        assertNotNull(response.quizId());
        assertEquals("Cristianismo", response.religiao());
        assertEquals(1, response.questoes().size());
        assertEquals("q1", response.questoes().get(0).id());

        ArgumentCaptor<QuizSession> sessionCaptor = ArgumentCaptor.forClass(QuizSession.class);
        verify(sessionRepository).salvar(sessionCaptor.capture());
        QuizSession savedSession = sessionCaptor.getValue();
        assertEquals("A", savedSession.getQuestoes().get(0).getRespostaCorretaId());
    }

    @Test
    @DisplayName("Deve rejeitar quantidade de perguntas inválida")
    void deveRejeitarQuantidadeInvalida() {
        GenerateQuizRequest request = new GenerateQuizRequest(
                "Islamismo", "", "Médio", 7
        );

        InvalidQuizParametersException ex = assertThrows(
                InvalidQuizParametersException.class,
                () -> generateQuizUseCase.execute(request)
        );

        assertTrue(ex.getMessage().contains("A quantidade de perguntas deve ser 5, 10 ou 15"));
        verifyNoInteractions(aiQuestionGenerator);
    }
}

