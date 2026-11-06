package com.quiz.religiousquiz.modules.quiz.usecase;

import com.quiz.religiousquiz.modules.quiz.domain.QuestionEntity;
import com.quiz.religiousquiz.modules.quiz.domain.QuizSession;
import com.quiz.religiousquiz.modules.quiz.dto.AlternativeDto;
import com.quiz.religiousquiz.modules.quiz.dto.GenerateQuizRequest;
import com.quiz.religiousquiz.modules.quiz.dto.PublicQuestionDto;
import com.quiz.religiousquiz.modules.quiz.dto.QuizSessionResponse;
import com.quiz.religiousquiz.modules.quiz.exception.InvalidQuizParametersException;
import com.quiz.religiousquiz.modules.quiz.repository.QuizSessionRepository;
import com.quiz.religiousquiz.modules.quiz.service.AiQuestionGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class GenerateQuizUseCase {

    private static final Set<Integer> QUANTIDADES_PERMITIDAS = Set.of(5, 10, 15);

    private final AiQuestionGenerator aiQuestionGenerator;
    private final QuizSessionRepository sessionRepository;
    private final long sessionTtlMinutes;

    public GenerateQuizUseCase(
            AiQuestionGenerator aiQuestionGenerator,
            QuizSessionRepository sessionRepository,
            @Value("${quiz.session.ttl-minutes:60}") long sessionTtlMinutes) {
        this.aiQuestionGenerator = aiQuestionGenerator;
        this.sessionRepository = sessionRepository;
        this.sessionTtlMinutes = sessionTtlMinutes;
    }

    public QuizSessionResponse executar(GenerateQuizRequest requisicao) {
        validarParametros(requisicao);

        String religiao = requisicao.religiao().trim();
        String tema = requisicao.tema() != null ? requisicao.tema().trim() : "";
        String dificuldade = requisicao.dificuldade().trim();
        int quantidade = requisicao.quantidade();

        List<QuestionEntity> questoes = aiQuestionGenerator.gerarQuestoes(
                religiao, tema, dificuldade, quantidade
        );

        QuizSession sessao = criarSessao(requisicao, questoes);
        sessionRepository.salvar(sessao);

        List<PublicQuestionDto> questoesPublicas = mapearParaQuestoesPublicas(questoes);

        return new QuizSessionResponse(
                sessao.getId(),
                sessao.getTitulo(),
                sessao.getReligiao(),
                sessao.getTema(),
                sessao.getDificuldade(),
                sessao.getStatus(),
                sessao.getTempoLimiteSegundos(),
                questoesPublicas
        );
    }

    public QuizSessionResponse execute(GenerateQuizRequest requisicao) {
        return executar(requisicao);
    }

    private void validarParametros(GenerateQuizRequest requisicao) {
        if (requisicao.religiao() == null || requisicao.religiao().isBlank()) {
            throw new InvalidQuizParametersException("A religião deve ser informada.");
        }
        if (requisicao.dificuldade() == null || requisicao.dificuldade().isBlank()) {
            throw new InvalidQuizParametersException("A dificuldade deve ser informada.");
        }
        if (requisicao.quantidade() == null || !QUANTIDADES_PERMITIDAS.contains(requisicao.quantidade())) {
            throw new InvalidQuizParametersException("A quantidade de perguntas deve ser 5, 10 ou 15.");
        }
    }

    private QuizSession criarSessao(GenerateQuizRequest requisicao, List<QuestionEntity> questoes) {
        String sessionId = UUID.randomUUID().toString();
        Instant now = Instant.now();

        String religiao = requisicao.religiao().trim();
        String tema = requisicao.tema() != null ? requisicao.tema().trim() : "";
        String dificuldade = requisicao.dificuldade().trim();

        String titulo = (requisicao.titulo() != null && !requisicao.titulo().isBlank())
                ? requisicao.titulo().trim()
                : "Quiz: " + religiao + (!tema.isBlank() ? " - " + tema : "");

        int tempoLimite = requisicao.tempoLimiteSegundos() != null ? Math.max(0, requisicao.tempoLimiteSegundos()) : 0;
        String status = (requisicao.status() != null && !requisicao.status().isBlank())
                ? requisicao.status().trim()
                : "PUBLICADO";

        // Quizzes do catálogo institucional são perenes (10 anos)
        Instant expiresAt = "PUBLICADO".equalsIgnoreCase(status)
                ? now.plus(3650, ChronoUnit.DAYS)
                : now.plus(sessionTtlMinutes, ChronoUnit.MINUTES);

        return new QuizSession(
                sessionId,
                titulo,
                religiao,
                tema,
                dificuldade,
                status,
                tempoLimite,
                now,
                expiresAt,
                questoes
        );
    }

    private List<PublicQuestionDto> mapearParaQuestoesPublicas(List<QuestionEntity> questoes) {
        return questoes.stream()
                .map(q -> new PublicQuestionDto(
                        q.getId(),
                        q.getPergunta(),
                        q.getAlternativas().stream()
                                .map(alt -> new AlternativeDto(alt.getId(), alt.getTexto()))
                                .toList(),
                        q.getDificuldade()
                ))
                .toList();
    }
}
