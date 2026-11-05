package com.quiz.religiousquiz.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class DotenvLoaderTest {

    @Test
    @DisplayName("DotenvLoader deve executar sem lançar exceções mesmo se arquivos não existirem")
    void deveCarregarSemExcecao() {
        assertDoesNotThrow(DotenvLoader::load);
    }
}

