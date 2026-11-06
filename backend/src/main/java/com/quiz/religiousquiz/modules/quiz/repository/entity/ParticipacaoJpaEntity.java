package com.quiz.religiousquiz.modules.quiz.repository.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "quiz_participacoes")
public class ParticipacaoJpaEntity {

    @Id
    @Column(name = "id", length = 64)
    private String id;

    @Column(name = "quiz_id", length = 64, nullable = false)
    private String quizId;

    @Column(name = "total_questoes", nullable = false)
    private int totalQuestoes;

    @Column(name = "acertos", nullable = false)
    private int acertos;

    @Column(name = "erros", nullable = false)
    private int erros;

    @Column(name = "aproveitamento", nullable = false, precision = 5, scale = 2)
    private BigDecimal aproveitamento;

    @Column(name = "respostas_usuario", columnDefinition = "TEXT")
    private String respostasUsuario;

    @Column(name = "concluido_em", nullable = false)
    private Instant concluidoEm;

    public ParticipacaoJpaEntity() {}

    public ParticipacaoJpaEntity(String id, String quizId, int totalQuestoes, int acertos,
                                 int erros, BigDecimal aproveitamento, String respostasUsuario,
                                 Instant concluidoEm) {
        this.id = id;
        this.quizId = quizId;
        this.totalQuestoes = totalQuestoes;
        this.acertos = acertos;
        this.erros = erros;
        this.aproveitamento = aproveitamento;
        this.respostasUsuario = respostasUsuario;
        this.concluidoEm = concluidoEm != null ? concluidoEm : Instant.now();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getQuizId() { return quizId; }
    public void setQuizId(String quizId) { this.quizId = quizId; }

    public int getTotalQuestoes() { return totalQuestoes; }
    public void setTotalQuestoes(int totalQuestoes) { this.totalQuestoes = totalQuestoes; }

    public int getAcertos() { return acertos; }
    public void setAcertos(int acertos) { this.acertos = acertos; }

    public int getErros() { return erros; }
    public void setErros(int erros) { this.erros = erros; }

    public BigDecimal getAproveitamento() { return aproveitamento; }
    public void setAproveitamento(BigDecimal aproveitamento) { this.aproveitamento = aproveitamento; }

    public String getRespostasUsuario() { return respostasUsuario; }
    public void setRespostasUsuario(String respostasUsuario) { this.respostasUsuario = respostasUsuario; }

    public Instant getConcluidoEm() { return concluidoEm; }
    public void setConcluidoEm(Instant concluidoEm) { this.concluidoEm = concluidoEm; }
}

