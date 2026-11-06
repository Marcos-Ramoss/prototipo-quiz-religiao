package com.quiz.religiousquiz.modules.quiz.usecase;

import com.quiz.religiousquiz.modules.quiz.domain.QuestionEntity;
import com.quiz.religiousquiz.modules.quiz.domain.QuizSession;
import com.quiz.religiousquiz.modules.quiz.dto.AlternativeDto;
import com.quiz.religiousquiz.modules.quiz.dto.AnswerSubmissionDto;
import com.quiz.religiousquiz.modules.quiz.dto.QuestionResultDto;
import com.quiz.religiousquiz.modules.quiz.dto.QuizResultResponse;
import com.quiz.religiousquiz.modules.quiz.dto.SubmitQuizRequest;
import com.quiz.religiousquiz.modules.quiz.exception.InvalidQuizParametersException;
import com.quiz.religiousquiz.modules.quiz.exception.QuizNotFoundException;
import com.quiz.religiousquiz.modules.quiz.repository.QuizSessionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class SubmitQuizUseCase {

    private final QuizSessionRepository sessionRepository;

    public SubmitQuizUseCase(QuizSessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    public QuizResultResponse executar(SubmitQuizRequest requisicao) {
        validarRequisicao(requisicao);

        QuizSession sessao = sessionRepository.buscarPorId(requisicao.quizId())
                .orElseThrow(() -> new QuizNotFoundException(requisicao.quizId()));

        sessionRepository.registrarParticipacao();

        Map<String, String> mapaRespostasUsuario = new java.util.HashMap<>();
        if (requisicao.respostas() != null) {
            for (AnswerSubmissionDto ans : requisicao.respostas()) {
                if (ans != null && ans.perguntaId() != null) {
                    mapaRespostasUsuario.put(ans.perguntaId(), ans.respostaSelecionadaId() != null ? ans.respostaSelecionadaId() : "");
                }
            }
        }

        List<QuestionResultDto> detalhes = sessao.getQuestoes().stream()
                .map(questao -> avaliarQuestao(questao, mapaRespostasUsuario))
                .toList();

        int total = detalhes.size();
        int acertos = (int) detalhes.stream().filter(QuestionResultDto::acertou).count();
        int erros = total - acertos;
        double aproveitamento = total > 0
                ? Math.round(((double) acertos / total) * 10000.0) / 100.0
                : 0.0;

        return new QuizResultResponse(total, acertos, erros, aproveitamento, detalhes);
    }

    public QuizResultResponse execute(SubmitQuizRequest requisicao) {
        return executar(requisicao);
    }

    private void validarRequisicao(SubmitQuizRequest requisicao) {
        if (requisicao.quizId() == null || requisicao.quizId().isBlank()) {
            throw new InvalidQuizParametersException("O identificador do quiz é obrigatório.");
        }
        if (requisicao.respostas() == null || requisicao.respostas().isEmpty()) {
            throw new InvalidQuizParametersException("A lista de respostas não pode ser vazia.");
        }
    }

    private QuestionResultDto avaliarQuestao(QuestionEntity questao, Map<String, String> mapaRespostasUsuario) {
        String respostaSelecionada = mapaRespostasUsuario.getOrDefault(questao.getId(), "");
        boolean acertou = questao.getRespostaCorretaId() != null
                && questao.getRespostaCorretaId().trim().equalsIgnoreCase(respostaSelecionada.trim());

        List<AlternativeDto> alternativas = questao.getAlternativas().stream()
                .map(alt -> new AlternativeDto(alt.getId(), alt.getTexto()))
                .toList();

        return new QuestionResultDto(
                questao.getId(),
                questao.getPergunta(),
                alternativas,
                respostaSelecionada,
                questao.getRespostaCorretaId(),
                acertou,
                questao.getExplicacao(),
                questao.getReferencia()
        );
    }
}
