package com.quiz.religiousquiz.modules.quiz.domain;

public class AlternativeEntity {
    private String id;
    private String texto;

    public AlternativeEntity() {
    }

    public AlternativeEntity(String id, String texto) {
        this.id = id;
        this.texto = texto;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }
}

