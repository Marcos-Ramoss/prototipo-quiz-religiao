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

    @MockBean
    private com.quiz.religiousquiz.modules.quiz.usecase.ListarQuizzesPublicadosUseCase listarQuizzesPublicadosUseCase;

    @MockBean
    private com.quiz.religiousquiz.modules.quiz.usecase.ListarQuizzesAdminUseCase listarQuizzesAdminUseCase;

    @MockBean
    private com.quiz.religiousquiz.modules.quiz.usecase.PublicarQuizUseCase publicarQuizUseCase;

    @MockBean
    private com.quiz.religiousquiz.modules.quiz.usecase.ObterQuizPorIdUseCase obterQuizPorIdUseCase;

    @MockBean
    private com.quiz.religiousquiz.modules.quiz.usecase.ExcluirQuizUseCase excluirQuizUseCase;

    @MockBean
    private com.quiz.religiousquiz.modules.quiz.usecase.ExportarQuizPdfUseCase exportarQuizPdfUseCase;

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

    @Test
    @DisplayName("GET /api/quiz/publicados deve retornar lista de quizzes para o aluno")
    void deveListarQuizzesPublicados() throws Exception {
        var resumo = new com.quiz.religiousquiz.modules.quiz.dto.QuizSummaryResponse(
                "q-1", "Tradições Judaicas", "Judaísmo", "Festas", "Fácil", 10, 600, "PUBLICADO", java.time.Instant.now()
        );
        when(listarQuizzesPublicadosUseCase.executar()).thenReturn(List.of(resumo));

        mockMvc.perform(get("/api/quiz/publicados"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("q-1"))
                .andExpect(jsonPath("$[0].titulo").value("Tradições Judaicas"))
                .andExpect(jsonPath("$[0].tempoLimiteSegundos").value(600));
    }

    @Test
    @DisplayName("GET /api/quiz/{id} deve retornar 200 com QuizSessionResponse")
    void deveObterQuizPorIdComSucesso() throws Exception {
        QuizSessionResponse response = new QuizSessionResponse(
                "q-1", "Quiz: Judaísmo", "Judaísmo", "Festas", "Fácil", "PUBLICADO", 300,
                List.of(new PublicQuestionDto("p-1", "Pergunta 1", List.of(new AlternativeDto("A", "Resp A")), "Fácil"))
        );
        when(obterQuizPorIdUseCase.executar("q-1")).thenReturn(response);

        mockMvc.perform(get("/api/quiz/q-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quizId").value("q-1"))
                .andExpect(jsonPath("$.titulo").value("Quiz: Judaísmo"))
                .andExpect(jsonPath("$.questoes[0].id").value("p-1"));
    }

    @Test
    @DisplayName("DELETE /api/quiz/{id} deve retornar 204")
    void deveExcluirQuizComSucesso() throws Exception {
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete("/api/quiz/q-1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("GET /api/quiz/{id}/pdf deve retornar 200 com application/pdf")
    void deveExportarPdfComSucesso() throws Exception {
        byte[] pdfFake = "%PDF-1.4 mock".getBytes();
        var resultado = new com.quiz.religiousquiz.modules.quiz.usecase.ExportarQuizPdfUseCase.ResultadoExportacaoPdf(
                pdfFake, "simulado-judaismo.pdf"
        );
        when(exportarQuizPdfUseCase.executar("q-1", true)).thenReturn(resultado);

        mockMvc.perform(get("/api/quiz/q-1/pdf?incluirGabarito=true"))
                .andExpect(status().isOk())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.header().string(
                        org.springframework.http.HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"simulado-judaismo.pdf\""))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.content().contentType(
                        org.springframework.http.MediaType.APPLICATION_PDF))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.content().bytes(pdfFake));
    }
}

