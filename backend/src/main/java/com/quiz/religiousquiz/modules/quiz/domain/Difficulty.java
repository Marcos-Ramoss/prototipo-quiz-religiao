package com.quiz.religiousquiz.modules.quiz.domain;

import java.util.Arrays;
import java.util.Optional;

public enum Difficulty {
    FACIL("Fácil"),
    MEDIO("Médio"),
    DIFICIL("Difícil");

    private final String descricao;

    Difficulty(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public static Optional<Difficulty> fromString(String valor) {
        if (valor == null || valor.isBlank()) {
            return Optional.empty();
        }
        String valorNormalizado = valor.trim().toLowerCase();
        return Arrays.stream(values())
                .filter(d -> d.name().equalsIgnoreCase(valorNormalizado)
                        || d.descricao.equalsIgnoreCase(valor.trim()))
                .findFirst();
    }
}

