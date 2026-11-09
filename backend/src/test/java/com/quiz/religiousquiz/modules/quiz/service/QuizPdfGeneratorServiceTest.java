package com.quiz.religiousquiz.modules.quiz.service;

import com.quiz.religiousquiz.modules.quiz.domain.AlternativeEntity;
import com.quiz.religiousquiz.modules.quiz.domain.QuestionEntity;
import com.quiz.religiousquiz.modules.quiz.domain.QuizSession;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class QuizPdfGeneratorServiceTest {

    private final QuizPdfGeneratorService generator = new QuizPdfGeneratorService();

    @Test
    @DisplayName("Deve gerar PDF de simulado escolar sem gabarito")
    void deveGerarPdfSimuladoSemGabarito() {
        QuizSession sessao = criarSessaoExemplo();

        byte[] pdfBytes = generator.gerarPdf(sessao, false);

        assertNotNull(pdfBytes);
        assertTrue(pdfBytes.length > 500, "O PDF gerado deve conter bytes válidos");
        String cabecalho = new String(pdfBytes, 0, Math.min(10, pdfBytes.length), StandardCharsets.ISO_8859_1);
        assertTrue(cabecalho.startsWith("%PDF"), "O arquivo gerado deve iniciar com assinatura mágica de PDF");
    }

    @Test
    @DisplayName("Deve gerar PDF de gabarito oficial com comentários e referências")
    void deveGerarPdfGabaritoOficial() {
        QuizSession sessao = criarSessaoExemplo();

        byte[] pdfBytes = generator.gerarPdf(sessao, true);

        assertNotNull(pdfBytes);
        assertTrue(pdfBytes.length > 500, "O PDF gerado deve conter bytes válidos");
        String cabecalho = new String(pdfBytes, 0, Math.min(10, pdfBytes.length), StandardCharsets.ISO_8859_1);
        assertTrue(cabecalho.startsWith("%PDF"), "O arquivo gerado deve iniciar com assinatura mágica de PDF");
    }

    private QuizSession criarSessaoExemplo() {
        QuestionEntity q1 = new QuestionEntity(
                "q-1",
                "Qual é o texto sagrado do Judaísmo?",
                List.of(
                        new AlternativeEntity("A", "Alcorão"),
                        new AlternativeEntity("B", "Torá"),
                        new AlternativeEntity("C", "Bíblia"),
                        new AlternativeEntity("D", "Vedas")
                ),
                "B",
                "A Torá compreende os cinco primeiros livros da tradição judaica.",
                "Tradições e Textos Sagrados, Ed. 2024",
                "Fácil"
        );

        return new QuizSession(
                "test-uuid-1234",
                "Quiz Especial de Judaísmo",
                "Judaísmo",
                "Fundamentos",
                "Fácil",
                "PUBLICADO",
                300,
                Instant.now(),
                Instant.now().plusSeconds(3600),
                List.of(q1)
        );
    }
}

