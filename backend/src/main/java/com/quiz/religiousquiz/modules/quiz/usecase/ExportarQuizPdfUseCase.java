package com.quiz.religiousquiz.modules.quiz.usecase;

import com.quiz.religiousquiz.modules.quiz.domain.QuizSession;
import com.quiz.religiousquiz.modules.quiz.exception.QuizNotFoundException;
import com.quiz.religiousquiz.modules.quiz.repository.QuizSessionRepository;
import com.quiz.religiousquiz.modules.quiz.service.QuizPdfGeneratorService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Normalizer;
import java.util.Locale;

@Service
public class ExportarQuizPdfUseCase {

    private final QuizSessionRepository quizSessionRepository;
    private final QuizPdfGeneratorService quizPdfGeneratorService;

    public ExportarQuizPdfUseCase(QuizSessionRepository quizSessionRepository,
                                  QuizPdfGeneratorService quizPdfGeneratorService) {
        this.quizSessionRepository = quizSessionRepository;
        this.quizPdfGeneratorService = quizPdfGeneratorService;
    }

    @Transactional(readOnly = true)
    public ResultadoExportacaoPdf executar(String quizId, boolean incluirGabarito) {
        QuizSession sessao = quizSessionRepository.buscarPorId(quizId)
                .orElseThrow(() -> new QuizNotFoundException(quizId));

        byte[] bytesPdf = quizPdfGeneratorService.gerarPdf(sessao, incluirGabarito);
        String nomeArquivo = gerarNomeArquivo(sessao, incluirGabarito);

        return new ResultadoExportacaoPdf(bytesPdf, nomeArquivo);
    }

    private String gerarNomeArquivo(QuizSession sessao, boolean incluirGabarito) {
        String tipo = incluirGabarito ? "gabarito" : "simulado";
        String base = sessao.getReligiao() != null ? sessao.getReligiao() : "quiz";
        String sanitizado = Normalizer.normalize(base, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9_-]", "-")
                .replaceAll("-+", "-");

        return String.format("%s-%s-%s.pdf", tipo, sanitizado, sessao.getId().substring(0, Math.min(8, sessao.getId().length())));
    }

    public record ResultadoExportacaoPdf(byte[] dados, String nomeArquivo) {}
}

