package com.quiz.religiousquiz.modules.quiz.repository.entity;

import com.quiz.religiousquiz.modules.quiz.domain.AlternativeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "quiz_perguntas")
public class PerguntaJpaEntity {

    @Id
    @Column(name = "id", length = 64)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id", nullable = false)
    private QuizJpaEntity quiz;

    @Column(name = "ordem", nullable = false)
    private int ordem;

    @Column(name = "pergunta", nullable = false, columnDefinition = "TEXT")
    private String pergunta;

    @Convert(converter = AlternativasJsonConverter.class)
    @Column(name = "alternativas", nullable = false, columnDefinition = "TEXT")
    private List<AlternativeEntity> alternativas = new ArrayList<>();

    @Column(name = "resposta_correta_id", length = 10, nullable = false)
    private String respostaCorretaId;

    @Column(name = "explicacao", columnDefinition = "TEXT")
    private String explicacao;

    @Column(name = "referencia", columnDefinition = "TEXT")
    private String referencia;

    @Column(name = "dificuldade", length = 30, nullable = false)
    private String dificuldade;

    public PerguntaJpaEntity() {}

    public PerguntaJpaEntity(String id, QuizJpaEntity quiz, int ordem, String pergunta,
                             List<AlternativeEntity> alternativas, String respostaCorretaId,
                             String explicacao, String referencia, String dificuldade) {
        this.id = id;
        this.quiz = quiz;
        this.ordem = ordem;
        this.pergunta = pergunta;
        this.alternativas = alternativas != null ? alternativas : new ArrayList<>();
        this.respostaCorretaId = respostaCorretaId;
        this.explicacao = explicacao;
        this.referencia = referencia;
        this.dificuldade = dificuldade;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public QuizJpaEntity getQuiz() { return quiz; }
    public void setQuiz(QuizJpaEntity quiz) { this.quiz = quiz; }

    public int getOrdem() { return ordem; }
    public void setOrdem(int ordem) { this.ordem = ordem; }

    public String getPergunta() { return pergunta; }
    public void setPergunta(String pergunta) { this.pergunta = pergunta; }

    public List<AlternativeEntity> getAlternativas() { return alternativas; }
    public void setAlternativas(List<AlternativeEntity> alternativas) { this.alternativas = alternativas; }

    public String getRespostaCorretaId() { return respostaCorretaId; }
    public void setRespostaCorretaId(String respostaCorretaId) { this.respostaCorretaId = respostaCorretaId; }

    public String getExplicacao() { return explicacao; }
    public void setExplicacao(String explicacao) { this.explicacao = explicacao; }

    public String getReferencia() { return referencia; }
    public void setReferencia(String referencia) { this.referencia = referencia; }

    public String getDificuldade() { return dificuldade; }
    public void setDificuldade(String dificuldade) { this.dificuldade = dificuldade; }
}

