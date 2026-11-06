package com.quiz.religiousquiz.modules.quiz.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.quiz.religiousquiz.modules.quiz.domain.QuestionEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GeminiAiQuestionGeneratorTest {

    private GeminiAiQuestionGenerator generator;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        generator = new GeminiAiQuestionGenerator("", "gemini-flash-lite-latest", 25, objectMapper);
    }

    @Test
    @DisplayName("Deve gerar questões de fallback quando a chave de API estiver vazia")
    void shouldGenerateFallbackQuestionsWhenApiKeyIsBlank() {
        List<QuestionEntity> questions = generator.gerarQuestoes("Cristianismo", "Parábolas", "Médio", 5);

        assertNotNull(questions);
        assertEquals(5, questions.size());
        assertEquals("Médio", questions.get(0).getDificuldade());
        assertEquals(4, questions.get(0).getAlternativas().size());
        assertNotNull(questions.get(0).getRespostaCorretaId());
    }

    @Test
    @DisplayName("Deve limpar markdown e remover trailing commas do JSON retornado pela IA")
    void shouldCleanMarkdownAndTrailingCommas() {
        String jsonWithTrailingComma = """
                ```json
                [
                  {
                    "pergunta": "Qual parábola fala sobre o semeador?",
                    "alternativas": [
                      {"id": "A", "texto": "Parábola do Semeador"},
                      {"id": "B", "texto": "Parábola do Filho Pródigo"},
                      {"id": "C", "texto": "Parábola dos Talentos"},
                      {"id": "D", "texto": "Parábola do Bom Samaritano"},
                    ],
                    "respostaCorretaId": "A",
                    "explicacao": "A parábola do semeador trata da semente e dos diferentes tipos de solo.",
                    "referencia": "Mateus 13",
                    "dificuldade": "Médio",
                  },
                ]
                ```
                """;

        String cleaned = generator.cleanMarkdownJson(jsonWithTrailingComma);

        assertFalse(cleaned.contains("```"));
        assertFalse(cleaned.contains(",\n    ]"));
        assertFalse(cleaned.contains(",\n  }"));
        assertTrue(cleaned.startsWith("["));
        assertTrue(cleaned.endsWith("]"));
    }
}

