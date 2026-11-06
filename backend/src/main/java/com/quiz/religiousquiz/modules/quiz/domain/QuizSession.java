package com.quiz.religiousquiz.modules.quiz.domain;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class QuizSession {
    private String id;
    private String titulo;
    private String religiao;
    private String tema;
    private String dificuldade;
    private String status = "PUBLICADO";
    private int tempoLimiteSegundos = 0;
    private Instant createdAt;
    private Instant expiresAt;
    private List<QuestionEntity> questoes = new ArrayList<>();

    public QuizSession() {
    }

    public QuizSession(String id, String religiao, String tema, String dificuldade,
                       Instant createdAt, Instant expiresAt, List<QuestionEntity> questoes) {
        this(id, "Quiz sobre " + (religiao != null ? religiao : "Tradições"), religiao, tema, dificuldade,
                "PUBLICADO", 0, createdAt, expiresAt, questoes);
    }

    public QuizSession(String id, String titulo, String religiao, String tema, String dificuldade,
                       String status, int tempoLimiteSegundos,
                       Instant createdAt, Instant expiresAt, List<QuestionEntity> questoes) {
        this.id = id;
        this.titulo = (titulo != null && !titulo.isBlank()) ? titulo : "Quiz sobre " + (religiao != null ? religiao : "Tradições");
        this.religiao = religiao;
        this.tema = tema;
        this.dificuldade = dificuldade;
        this.status = status != null ? status : "PUBLICADO";
        this.tempoLimiteSegundos = Math.max(0, tempoLimiteSegundos);
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
        this.questoes = questoes != null ? questoes : new ArrayList<>();
    }

    public boolean isExpired() {
        if ("PUBLICADO".equalsIgnoreCase(status)) {
            return false;
        }
        return expiresAt != null && Instant.now().isAfter(expiresAt);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getReligiao() {
        return religiao;
    }

    public void setReligiao(String religiao) {
        this.religiao = religiao;
    }

    public String getTema() {
        return tema;
    }

    public void setTema(String tema) {
        this.tema = tema;
    }

    public String getDificuldade() {
        return dificuldade;
    }

    public void setDificuldade(String dificuldade) {
        this.dificuldade = dificuldade;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getCriadoEm() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public Instant getExpiraEm() {
        return expiresAt;
    }

    public void setExpiresAt(Instant expiresAt) {
        this.expiresAt = expiresAt;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getTempoLimiteSegundos() {
        return tempoLimiteSegundos;
    }

    public void setTempoLimiteSegundos(int tempoLimiteSegundos) {
        this.tempoLimiteSegundos = tempoLimiteSegundos;
    }

    public List<QuestionEntity> getQuestoes() {
        return questoes;
    }

    public void setQuestoes(List<QuestionEntity> questoes) {
        this.questoes = questoes;
    }
}

