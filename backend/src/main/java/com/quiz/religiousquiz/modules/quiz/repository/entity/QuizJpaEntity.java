package com.quiz.religiousquiz.modules.quiz.repository.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "quizzes")
public class QuizJpaEntity {

    @Id
    @Column(name = "id", length = 64)
    private String id;

    @Column(name = "religiao", length = 100, nullable = false)
    private String religiao;

    @Column(name = "tema", length = 255)
    private String tema;

    @Column(name = "dificuldade", length = 30, nullable = false)
    private String dificuldade;

    @Column(name = "quantidade", nullable = false)
    private int quantidade;

    @Column(name = "status", length = 30, nullable = false)
    private String status = "CRIADO";

    @Column(name = "criado_em", nullable = false)
    private Instant criadoEm;

    @Column(name = "expira_em", nullable = false)
    private Instant expiraEm;

    @OneToMany(mappedBy = "quiz", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @OrderBy("ordem ASC")
    private List<PerguntaJpaEntity> perguntas = new ArrayList<>();

    public QuizJpaEntity() {}

    public QuizJpaEntity(String id, String religiao, String tema, String dificuldade,
                         int quantidade, String status, Instant criadoEm, Instant expiraEm) {
        this.id = id;
        this.religiao = religiao;
        this.tema = tema;
        this.dificuldade = dificuldade;
        this.quantidade = quantidade;
        this.status = status != null ? status : "CRIADO";
        this.criadoEm = criadoEm != null ? criadoEm : Instant.now();
        this.expiraEm = expiraEm;
    }

    public void adicionarPergunta(PerguntaJpaEntity pergunta) {
        perguntas.add(pergunta);
        pergunta.setQuiz(this);
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getReligiao() { return religiao; }
    public void setReligiao(String religiao) { this.religiao = religiao; }

    public String getTema() { return tema; }
    public void setTema(String tema) { this.tema = tema; }

    public String getDificuldade() { return dificuldade; }
    public void setDificuldade(String dificuldade) { this.dificuldade = dificuldade; }

    public int getQuantidade() { return quantidade; }
    public void setQuantidade(int quantidade) { this.quantidade = quantidade; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Instant getCriadoEm() { return criadoEm; }
    public void setCriadoEm(Instant criadoEm) { this.criadoEm = criadoEm; }

    public Instant getExpiraEm() { return expiraEm; }
    public void setExpiraEm(Instant expiraEm) { this.expiraEm = expiraEm; }

    public List<PerguntaJpaEntity> getPerguntas() { return perguntas; }
    public void setPerguntas(List<PerguntaJpaEntity> perguntas) { this.perguntas = perguntas; }
}

