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

        QuizSession sessao = criarSessao(religiao, tema, dificuldade, questoes);
        sessionRepository.salvar(sessao);

        List<PublicQuestionDto> questoesPublicas = mapearParaQuestoesPublicas(questoes);

        return new QuizSessionResponse(
                sessao.getId(),
                sessao.getReligiao(),
                sessao.getTema(),
                sessao.getDificuldade(),
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

    private QuizSession criarSessao(String religiao, String tema, String dificuldade, List<QuestionEntity> questoes) {
        String sessionId = UUID.randomUUID().toString();
        Instant now = Instant.now();
        Instant expiresAt = now.plus(sessionTtlMinutes, ChronoUnit.MINUTES);

        return new QuizSession(
                sessionId,
                religiao,
                tema,
                dificuldade,
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
