package com.quiz.religiousquiz.modules.quiz.controller;

import com.quiz.religiousquiz.modules.quiz.dto.GenerateQuizRequest;
import com.quiz.religiousquiz.modules.quiz.dto.QuizResultResponse;
import com.quiz.religiousquiz.modules.quiz.dto.QuizSessionResponse;
import com.quiz.religiousquiz.modules.quiz.dto.QuizStatsResponse;
import com.quiz.religiousquiz.modules.quiz.dto.SubmitQuizRequest;
import com.quiz.religiousquiz.modules.quiz.usecase.GenerateQuizUseCase;
import com.quiz.religiousquiz.modules.quiz.usecase.GetQuizStatsUseCase;
import com.quiz.religiousquiz.modules.quiz.usecase.SubmitQuizUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/quiz")
@Tag(name = "Quiz", description = "Operações para geração e avaliação de quizzes interativos com IA")
public class QuizController {

    private final GenerateQuizUseCase generateQuizUseCase;
    private final SubmitQuizUseCase submitQuizUseCase;
    private final GetQuizStatsUseCase getQuizStatsUseCase;

    public QuizController(GenerateQuizUseCase generateQuizUseCase,
                          SubmitQuizUseCase submitQuizUseCase,
                          GetQuizStatsUseCase getQuizStatsUseCase) {
        this.generateQuizUseCase = generateQuizUseCase;
        this.submitQuizUseCase = submitQuizUseCase;
        this.getQuizStatsUseCase = getQuizStatsUseCase;
    }

    @GetMapping("/stats")
    @Operation(summary = "Retorna as estatísticas consolidadas da plataforma",
               description = "Informa em tempo real o volume de quizzes criados, total de tradições disponíveis e participações realizadas.")
    @ApiResponse(responseCode = "200", description = "Estatísticas obtidas com sucesso")
    public ResponseEntity<QuizStatsResponse> obterEstatisticas() {
        return ResponseEntity.ok(getQuizStatsUseCase.executar());
    }

    @PostMapping("/generate")
    @Operation(summary = "Gera um novo quiz personalizado com Inteligência Artificial",
               description = "Aciona o modelo Google Gemini para criar perguntas inéditas com alternativas e justificativas com base na tradição religiosa, dificuldade e tema escolhidos.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Quiz gerado com sucesso e sessão iniciada"),
            @ApiResponse(responseCode = "400", description = "Parâmetros de requisição inválidos"),
            @ApiResponse(responseCode = "502", description = "Falha de integração ou geração pela IA")
    })
    public ResponseEntity<QuizSessionResponse> gerarQuiz(@Valid @RequestBody GenerateQuizRequest request) {
        QuizSessionResponse response = generateQuizUseCase.executar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/submit")
    @Operation(summary = "Submete as respostas e calcula o resultado do quiz",
               description = "Valida as respostas do usuário contra o gabarito original gerado pela IA, calculando taxa de acerto, tempo gasto e gerando feedback detalhado por questão.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Quiz avaliado com sucesso com resultado detalhado"),
            @ApiResponse(responseCode = "404", description = "Sessão do quiz não encontrada"),
            @ApiResponse(responseCode = "410", description = "Sessão do quiz expirada pelo tempo limite (TTL)")
    })
    public ResponseEntity<QuizResultResponse> submeterQuiz(@Valid @RequestBody SubmitQuizRequest request) {
        QuizResultResponse response = submitQuizUseCase.executar(request);
        return ResponseEntity.ok(response);
    }
}
