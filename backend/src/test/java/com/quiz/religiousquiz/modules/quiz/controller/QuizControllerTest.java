package com.quiz.religiousquiz.modules.quiz.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.quiz.religiousquiz.modules.quiz.dto.AlternativeDto;
import com.quiz.religiousquiz.modules.quiz.dto.AnswerSubmissionDto;
import com.quiz.religiousquiz.modules.quiz.dto.GenerateQuizRequest;
import com.quiz.religiousquiz.modules.quiz.dto.PublicQuestionDto;
import com.quiz.religiousquiz.modules.quiz.dto.QuizResultResponse;
import com.quiz.religiousquiz.modules.quiz.dto.QuizSessionResponse;
import com.quiz.religiousquiz.modules.quiz.dto.QuizStatsResponse;
import com.quiz.religiousquiz.modules.quiz.dto.SubmitQuizRequest;
import com.quiz.religiousquiz.modules.quiz.usecase.GenerateQuizUseCase;
import com.quiz.religiousquiz.modules.quiz.usecase.GetQuizStatsUseCase;
import com.quiz.religiousquiz.modules.quiz.usecase.SubmitQuizUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(QuizController.class)
class QuizControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private GenerateQuizUseCase generateQuizUseCase;

    @MockBean
    private SubmitQuizUseCase submitQuizUseCase;

    @MockBean
    private GetQuizStatsUseCase getQuizStatsUseCase;

    @Test
    @DisplayName("GET /api/quiz/stats deve retornar 200 com QuizStatsResponse")
    void deveRetornarEstatisticasComSucesso() throws Exception {
        when(getQuizStatsUseCase.executar()).thenReturn(new QuizStatsResponse(12, 3842, 8, 5280));

        mockMvc.perform(get("/api/quiz/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quizzesCriados").value(12))
                .andExpect(jsonPath("$.perguntasDisponiveis").value(3842))
                .andExpect(jsonPath("$.tradicoesCadastradas").value(8))
                .andExpect(jsonPath("$.participacoesRealizadas").value(5280));
    }

    @Test
    @DisplayName("POST /api/quiz/generate deve retornar 201 com QuizSessionResponse")
    void deveGerarQuizComSucesso() throws Exception {
        GenerateQuizRequest request = new GenerateQuizRequest("Cristianismo", "História", "Fácil", 5);

        QuizSessionResponse response = new QuizSessionResponse(
                "session-1",
                "Cristianismo",
                "História",
                "Fácil",
                List.of(new PublicQuestionDto(
                        "q1", "Quem foi o apóstolo Pedro?",
                        List.of(new AlternativeDto("A", "Pescador da Galileia")),
                        "Fácil"
                ))
        );

        when(generateQuizUseCase.executar(any(GenerateQuizRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/quiz/generate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.quizId").value("session-1"))
                .andExpect(jsonPath("$.religiao").value("Cristianismo"))
                .andExpect(jsonPath("$.questoes[0].id").value("q1"));
    }

    @Test
    @DisplayName("POST /api/quiz/submit deve retornar 200 com QuizResultResponse")
    void deveSubmeterQuizComSucesso() throws Exception {
        SubmitQuizRequest request = new SubmitQuizRequest(
                "session-1",
                List.of(new AnswerSubmissionDto("q1", "A"))
        );

        QuizResultResponse response = new QuizResultResponse(
                1, 1, 0, 100.0, List.of()
        );

        when(submitQuizUseCase.executar(any(SubmitQuizRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/quiz/submit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalQuestoes").value(1))
                .andExpect(jsonPath("$.acertos").value(1))
                .andExpect(jsonPath("$.aproveitamento").value(100.0));
    }
}

