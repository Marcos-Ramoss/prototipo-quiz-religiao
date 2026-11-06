package com.quiz.religiousquiz.modules.quiz.usecase;

import com.quiz.religiousquiz.modules.quiz.dto.QuizStatsResponse;
import com.quiz.religiousquiz.modules.quiz.repository.QuizSessionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetQuizStatsUseCaseTest {

    @Mock
    private QuizSessionRepository sessionRepository;

    @InjectMocks
    private GetQuizStatsUseCase getQuizStatsUseCase;

    @Test
    @DisplayName("Deve retornar estatísticas calculadas dinamicamente")
    void deveRetornarEstatisticasCorretamente() {
        when(sessionRepository.obterTotalCriados()).thenReturn(15L);
        when(sessionRepository.obterTotalParticipacoes()).thenReturn(5300L);

        QuizStatsResponse response = getQuizStatsUseCase.executar();

        assertNotNull(response);
        assertEquals(15L, response.quizzesCriados());
        assertEquals(5300L, response.participacoesRealizadas());
        assertEquals(8, response.tradicoesCadastradas());
        assertEquals(3840 + (15 * 5), response.perguntasDisponiveis());
    }
}

