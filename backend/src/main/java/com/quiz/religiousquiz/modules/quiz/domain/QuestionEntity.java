package com.quiz.religiousquiz.modules.quiz.domain;

import java.util.ArrayList;
import java.util.List;

public class QuestionEntity {
    private String id;
    private String pergunta;
    private List<AlternativeEntity> alternativas = new ArrayList<>();
    private String respostaCorretaId;
    private String explicacao;
    private String referencia;
    private String dificuldade;

    public QuestionEntity() {
    }

    public QuestionEntity(String id, String pergunta, List<AlternativeEntity> alternativas,
                          String respostaCorretaId, String explicacao, String referencia, String dificuldade) {
        this.id = id;
        this.pergunta = pergunta;
        this.alternativas = alternativas != null ? alternativas : new ArrayList<>();
        this.respostaCorretaId = respostaCorretaId;
        this.explicacao = explicacao;
        this.referencia = referencia;
        this.dificuldade = dificuldade;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPergunta() {
        return pergunta;
    }

    public void setPergunta(String pergunta) {
        this.pergunta = pergunta;
    }

    public List<AlternativeEntity> getAlternativas() {
        return alternativas;
    }

    public void setAlternativas(List<AlternativeEntity> alternativas) {
        this.alternativas = alternativas;
    }

    public String getRespostaCorretaId() {
        return respostaCorretaId;
    }

    public void setRespostaCorretaId(String respostaCorretaId) {
        this.respostaCorretaId = respostaCorretaId;
    }

    public String getExplicacao() {
        return explicacao;
    }

    public void setExplicacao(String explicacao) {
        this.explicacao = explicacao;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public String getDificuldade() {
        return dificuldade;
    }

    public void setDificuldade(String dificuldade) {
        this.dificuldade = dificuldade;
    }
}

