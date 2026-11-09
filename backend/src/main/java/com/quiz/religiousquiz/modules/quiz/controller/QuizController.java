package com.quiz.religiousquiz.modules.quiz.controller;

import com.quiz.religiousquiz.modules.quiz.dto.GenerateQuizRequest;
import com.quiz.religiousquiz.modules.quiz.dto.PublishQuizRequest;
import com.quiz.religiousquiz.modules.quiz.dto.QuizResultResponse;
import com.quiz.religiousquiz.modules.quiz.dto.QuizSessionResponse;
import com.quiz.religiousquiz.modules.quiz.dto.QuizStatsResponse;
import com.quiz.religiousquiz.modules.quiz.dto.QuizSummaryResponse;
import com.quiz.religiousquiz.modules.quiz.dto.SubmitQuizRequest;
import com.quiz.religiousquiz.modules.quiz.usecase.ExcluirQuizUseCase;
import com.quiz.religiousquiz.modules.quiz.usecase.ExportarQuizPdfUseCase;
import com.quiz.religiousquiz.modules.quiz.usecase.GenerateQuizUseCase;
import com.quiz.religiousquiz.modules.quiz.usecase.GetQuizStatsUseCase;
import com.quiz.religiousquiz.modules.quiz.usecase.ListarQuizzesAdminUseCase;
import com.quiz.religiousquiz.modules.quiz.usecase.ListarQuizzesPublicadosUseCase;
import com.quiz.religiousquiz.modules.quiz.usecase.ObterQuizPorIdUseCase;
import com.quiz.religiousquiz.modules.quiz.usecase.PublicarQuizUseCase;
import com.quiz.religiousquiz.modules.quiz.usecase.SubmitQuizUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/quiz")
@Tag(name = "Quiz", description = "Operações para geração, curadoria e resolução de quizzes com IA")
public class QuizController {

    private final GenerateQuizUseCase generateQuizUseCase;
    private final SubmitQuizUseCase submitQuizUseCase;
    private final GetQuizStatsUseCase getQuizStatsUseCase;
    private final ListarQuizzesPublicadosUseCase listarQuizzesPublicadosUseCase;
    private final ListarQuizzesAdminUseCase listarQuizzesAdminUseCase;
    private final PublicarQuizUseCase publicarQuizUseCase;
    private final ObterQuizPorIdUseCase obterQuizPorIdUseCase;
    private final ExcluirQuizUseCase excluirQuizUseCase;
    private final ExportarQuizPdfUseCase exportarQuizPdfUseCase;

    public QuizController(GenerateQuizUseCase generateQuizUseCase,
                          SubmitQuizUseCase submitQuizUseCase,
                          GetQuizStatsUseCase getQuizStatsUseCase,
                          ListarQuizzesPublicadosUseCase listarQuizzesPublicadosUseCase,
                          ListarQuizzesAdminUseCase listarQuizzesAdminUseCase,
                          PublicarQuizUseCase publicarQuizUseCase,
                          ObterQuizPorIdUseCase obterQuizPorIdUseCase,
                          ExcluirQuizUseCase excluirQuizUseCase,
                          ExportarQuizPdfUseCase exportarQuizPdfUseCase) {
        this.generateQuizUseCase = generateQuizUseCase;
        this.submitQuizUseCase = submitQuizUseCase;
        this.getQuizStatsUseCase = getQuizStatsUseCase;
        this.listarQuizzesPublicadosUseCase = listarQuizzesPublicadosUseCase;
        this.listarQuizzesAdminUseCase = listarQuizzesAdminUseCase;
        this.publicarQuizUseCase = publicarQuizUseCase;
        this.obterQuizPorIdUseCase = obterQuizPorIdUseCase;
        this.excluirQuizUseCase = excluirQuizUseCase;
        this.exportarQuizPdfUseCase = exportarQuizPdfUseCase;
    }

    @GetMapping("/stats")
    @Operation(summary = "Retorna as estatísticas consolidadas da plataforma",
               description = "Informa em tempo real o volume de quizzes criados, total de tradições disponíveis e participações realizadas.")
    @ApiResponse(responseCode = "200", description = "Estatísticas obtidas com sucesso")
    public ResponseEntity<QuizStatsResponse> obterEstatisticas() {
        return ResponseEntity.ok(getQuizStatsUseCase.executar());
    }

    @GetMapping("/publicados")
    @Operation(summary = "Lista todos os quizzes publicados disponíveis para os alunos",
               description = "Retorna o catálogo de quizzes prontos e curados para resolução pública.")
    @ApiResponse(responseCode = "200", description = "Lista de quizzes obtida com sucesso")
    public ResponseEntity<List<QuizSummaryResponse>> listarPublicados() {
        return ResponseEntity.ok(listarQuizzesPublicadosUseCase.executar());
    }

    @GetMapping("/admin/todos")
    @Operation(summary = "Lista todos os quizzes (rascunhos e publicados) para o painel do administrador",
               description = "Visão administrativa para acompanhamento e curadoria de conteúdo.")
    @ApiResponse(responseCode = "200", description = "Lista completa obtida com sucesso")
    public ResponseEntity<List<QuizSummaryResponse>> listarTodosAdmin() {
        return ResponseEntity.ok(listarQuizzesAdminUseCase.executar());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Recupera uma sessão de quiz pelo identificador",
               description = "Carrega as perguntas da sessão para realização pelo aluno ou revisão pelo admin.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Quiz encontrado"),
            @ApiResponse(responseCode = "404", description = "Quiz não encontrado")
    })
    public ResponseEntity<QuizSessionResponse> obterPorId(@PathVariable String id) {
        return ResponseEntity.ok(obterQuizPorIdUseCase.executar(id));
    }

    @GetMapping(value = "/{id}/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    @Operation(summary = "Exporta o simulado ou gabarito comentado oficial em PDF",
               description = "Gera um documento PDF padronizado com cabeçalho institucional, questões formatadas e gabarito comentado opcional.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "PDF gerado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Quiz não encontrado")
    })
    public ResponseEntity<byte[]> exportarPdf(
            @PathVariable String id,
            @RequestParam(name = "incluirGabarito", defaultValue = "true") boolean incluirGabarito) {
        var resultado = exportarQuizPdfUseCase.executar(id, incluirGabarito);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resultado.nomeArquivo() + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(resultado.dados());
    }

    @PostMapping("/generate")
    @Operation(summary = "Gera um novo quiz personalizado com Inteligência Artificial (Modo Admin)",
               description = "Aciona a IA para criar perguntas inéditas em modo rascunho para curadoria do professor/admin.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Quiz gerado em rascunho com sucesso"),
            @ApiResponse(responseCode = "400", description = "Parâmetros inválidos"),
            @ApiResponse(responseCode = "502", description = "Falha de integração da IA")
    })
    public ResponseEntity<QuizSessionResponse> gerarQuiz(@Valid @RequestBody GenerateQuizRequest request) {
        QuizSessionResponse response = generateQuizUseCase.executar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}/publicar")
    @Operation(summary = "Publica um quiz e define tempo limite (Ação do Admin)",
               description = "Permite ao administrador aprovar o quiz, personalizar o título e configurar o cronômetro para os alunos.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Quiz publicado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Quiz não encontrado")
    })
    public ResponseEntity<QuizSummaryResponse> publicarQuiz(@PathVariable String id,
                                                            @RequestBody(required = false) PublishQuizRequest request) {
        return ResponseEntity.ok(publicarQuizUseCase.executar(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Exclui um quiz do sistema",
               description = "Remove o quiz e suas perguntas associadas.")
    @ApiResponse(responseCode = "204", description = "Quiz excluído com sucesso")
    public ResponseEntity<Void> excluirQuiz(@PathVariable String id) {
        excluirQuizUseCase.executar(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/submit")
    @Operation(summary = "Submete as respostas e calcula o resultado do quiz",
               description = "Valida as respostas do usuário contra o gabarito original, calcula pontuação e registra a participação.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Quiz avaliado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Quiz não encontrado"),
            @ApiResponse(responseCode = "410", description = "Quiz expirado")
    })
    public ResponseEntity<QuizResultResponse> submeterQuiz(@Valid @RequestBody SubmitQuizRequest request) {
        QuizResultResponse response = submitQuizUseCase.executar(request);
        return ResponseEntity.ok(response);
    }
}
