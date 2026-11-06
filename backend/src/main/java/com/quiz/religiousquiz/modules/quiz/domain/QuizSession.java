package com.quiz.religiousquiz.modules.quiz.domain;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class QuizSession {
    private String id;
    private String religiao;
    private String tema;
    private String dificuldade;
    private Instant createdAt;
    private Instant expiresAt;
    private List<QuestionEntity> questoes = new ArrayList<>();

    public QuizSession() {
    }

    public QuizSession(String id, String religiao, String tema, String dificuldade,
                       Instant createdAt, Instant expiresAt, List<QuestionEntity> questoes) {
        this.id = id;
        this.religiao = religiao;
        this.tema = tema;
        this.dificuldade = dificuldade;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
        this.questoes = questoes != null ? questoes : new ArrayList<>();
    }

    public boolean isExpired() {
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

    public List<QuestionEntity> getQuestoes() {
        return questoes;
    }

    public void setQuestoes(List<QuestionEntity> questoes) {
        this.questoes = questoes;
    }
}

