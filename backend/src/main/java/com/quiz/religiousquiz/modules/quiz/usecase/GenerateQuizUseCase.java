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

    public QuizSessionResponse execute(GenerateQuizRequest request) {
        validateParameters(request);

        String religiao = request.religiao().trim();
        String tema = request.tema() != null ? request.tema().trim() : "";
        String dificuldade = request.dificuldade().trim();
        int quantidade = request.quantidade();

        List<QuestionEntity> questions = aiQuestionGenerator.generateQuestions(
                religiao, tema, dificuldade, quantidade
        );

        QuizSession session = createSession(religiao, tema, dificuldade, questions);
        sessionRepository.save(session);

        List<PublicQuestionDto> publicQuestions = mapToPublicQuestions(questions);

        return new QuizSessionResponse(
                session.getId(),
                session.getReligiao(),
                session.getTema(),
                session.getDificuldade(),
                publicQuestions
        );
    }

    private void validateParameters(GenerateQuizRequest request) {
        if (request.religiao() == null || request.religiao().isBlank()) {
            throw new InvalidQuizParametersException("A religião deve ser informada.");
        }
        if (request.dificuldade() == null || request.dificuldade().isBlank()) {
            throw new InvalidQuizParametersException("A dificuldade deve ser informada.");
        }
        if (request.quantidade() == null || !QUANTIDADES_PERMITIDAS.contains(request.quantidade())) {
            throw new InvalidQuizParametersException("A quantidade de perguntas deve ser 5, 10 ou 15.");
        }
    }

    private QuizSession createSession(String religiao, String tema, String dificuldade, List<QuestionEntity> questions) {
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
                questions
        );
    }

    private List<PublicQuestionDto> mapToPublicQuestions(List<QuestionEntity> questions) {
        return questions.stream()
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

