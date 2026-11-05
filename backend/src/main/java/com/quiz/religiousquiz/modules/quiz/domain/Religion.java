package com.quiz.religiousquiz.modules.quiz.domain;

import java.util.Arrays;
import java.util.Optional;

public enum Religion {
    CRISTIANISMO("Cristianismo"),
    ISLAMISMO("Islamismo"),
    BUDISMO("Budismo"),
    HINDUISMO("Hinduísmo"),
    JUDAISMO("Judaísmo"),
    ESPIRITISMO("Espiritismo"),
    MATRIZ_AFRICANA("Religiões de matriz africana"),
    OUTRAS("Outras religiões");

    private final String descricao;

    Religion(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public static Optional<Religion> fromString(String valor) {
        if (valor == null || valor.isBlank()) {
            return Optional.empty();
        }
        String valorNormalizado = valor.trim().toLowerCase();
        return Arrays.stream(values())
                .filter(r -> r.name().equalsIgnoreCase(valorNormalizado)
                        || r.descricao.equalsIgnoreCase(valor.trim())
                        || r.name().replace("_", " ").equalsIgnoreCase(valorNormalizado))
                .findFirst();
    }
}

