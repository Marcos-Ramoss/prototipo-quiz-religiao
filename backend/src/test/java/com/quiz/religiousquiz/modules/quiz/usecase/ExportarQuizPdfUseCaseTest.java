package com.quiz.religiousquiz.modules.quiz.usecase;

import com.quiz.religiousquiz.modules.quiz.domain.AlternativeEntity;
import com.quiz.religiousquiz.modules.quiz.domain.QuestionEntity;
import com.quiz.religiousquiz.modules.quiz.domain.QuizSession;
import com.quiz.religiousquiz.modules.quiz.exception.QuizNotFoundException;
import com.quiz.religiousquiz.modules.quiz.repository.QuizSessionRepository;
import com.quiz.religiousquiz.modules.quiz.service.QuizPdfGeneratorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ExportarQuizPdfUseCaseTest {

    private QuizSessionRepository repository;
    private QuizPdfGeneratorService pdfGenerator;
    private ExportarQuizPdfUseCase useCase;

    @BeforeEach
    void setUp() {
        repository = mock(QuizSessionRepository.class);
        pdfGenerator = mock(QuizPdfGeneratorService.class);
        useCase = new ExportarQuizPdfUseCase(repository, pdfGenerator);
    }

    @Test
    @DisplayName("Deve exportar PDF com sucesso quando quiz existe")
    void deveExportarPdfComSucesso() {
        QuizSession sessao = new QuizSession(
                "abc-12345", "Quiz sobre Judaísmo", "Judaísmo", "Festas", "Fácil",
                "PUBLICADO", 300, Instant.now(), Instant.now().plusSeconds(3600),
                List.of(new QuestionEntity("q1", "Pergunta teste?", List.of(new AlternativeEntity("A", "Resp")), "A", "Explicação", "Ref", "Fácil"))
        );

        byte[] fakePdf = "%PDF-1.4 mock bytes".getBytes();
        when(repository.buscarPorId("abc-12345")).thenReturn(Optional.of(sessao));
        when(pdfGenerator.gerarPdf(any(QuizSession.class), eq(true))).thenReturn(fakePdf);

        var resultado = useCase.executar("abc-12345", true);

        assertNotNull(resultado);
        assertEquals(fakePdf, resultado.dados());
        assertTrue(resultado.nomeArquivo().startsWith("gabarito-judaismo-abc-1234"));
        assertTrue(resultado.nomeArquivo().endsWith(".pdf"));
        verify(repository).buscarPorId("abc-12345");
        verify(pdfGenerator).gerarPdf(sessao, true);
    }

    @Test
    @DisplayName("Deve lançar QuizNotFoundException quando quiz não for encontrado")
    void deveLancarExcecaoQuandoQuizNaoEncontrado() {
        when(repository.buscarPorId("inexistente")).thenReturn(Optional.empty());

        assertThrows(QuizNotFoundException.class, () -> useCase.executar("inexistente", false));
    }
}

