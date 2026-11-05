package com.quiz.religiousquiz.modules.quiz.service;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.quiz.religiousquiz.modules.quiz.domain.AlternativeEntity;
import com.quiz.religiousquiz.modules.quiz.domain.QuestionEntity;
import com.quiz.religiousquiz.modules.quiz.exception.QuizGenerationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
public class GeminiAiQuestionGenerator implements AiQuestionGenerator {

    private static final Logger log = LoggerFactory.getLogger(GeminiAiQuestionGenerator.class);
    private static final String GEMINI_BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/";
    private static final String FALLBACK_STABLE_MODEL = "gemini-flash-lite-latest";
    private static final int MAX_RETRIES = 2;
    private static final long RETRY_BACKOFF_MS = 1000L;

    private final String apiKey;
    private final String model;
    private final int timeoutSeconds;
    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    public GeminiAiQuestionGenerator(
            @Value("${quiz.ai.gemini.api-key:}") String apiKey,
            @Value("${quiz.ai.gemini.model:gemini-flash-lite-latest}") String model,
            @Value("${quiz.ai.gemini.timeout-seconds:25}") int timeoutSeconds,
            ObjectMapper objectMapper) {
        this.apiKey = apiKey != null ? apiKey.trim() : "";
        this.model = model != null && !model.isBlank() ? model.trim() : FALLBACK_STABLE_MODEL;
        this.timeoutSeconds = timeoutSeconds > 0 ? timeoutSeconds : 25;
        this.objectMapper = objectMapper.copy()
                .enable(JsonReadFeature.ALLOW_TRAILING_COMMA.mappedFeature())
                .enable(JsonReadFeature.ALLOW_UNQUOTED_FIELD_NAMES.mappedFeature())
                .enable(JsonReadFeature.ALLOW_SINGLE_QUOTES.mappedFeature())
                .enable(JsonParser.Feature.ALLOW_COMMENTS)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(10000);
        requestFactory.setReadTimeout(this.timeoutSeconds * 1000);

        this.restClient = RestClient.builder()
                .requestFactory(requestFactory)
                .build();
    }

    @Override
    public List<QuestionEntity> generateQuestions(String religiao, String tema, String dificuldade, int quantidade) {
        if (apiKey.isBlank()) {
            log.warn("GEMINI_API_KEY não configurada. Usando gerador fallback para fins de demonstração.");
            return generateFallbackQuestions(religiao, tema, dificuldade, quantidade);
        }

        long startTime = System.currentTimeMillis();
        String prompt = buildPrompt(religiao, tema, dificuldade, quantidade);
        Map<String, Object> requestPayload = buildRequestPayload(prompt);

        log.info("Chamando Gemini API (modelo: {}) para religião: {}, tema: {}, dificuldade: {}, quantidade: {}",
                model, religiao, tema, dificuldade, quantidade);

        String responseBody = executeWithRetry(model, requestPayload);
        long elapsedMs = System.currentTimeMillis() - startTime;
        log.info("Resposta da Gemini API recebida com sucesso em {} ms", elapsedMs);

        return parseAndValidateResponse(responseBody, religiao, dificuldade, quantidade);
    }

    private String executeWithRetry(String targetModel, Map<String, Object> requestPayload) {
        Exception lastException = null;

        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
            try {
                return callGeminiApi(targetModel, requestPayload);
            } catch (GeminiTransientException ex) {
                lastException = ex;
                log.warn("Tentativa {}/{} falhou com pico de demanda no modelo {}: {}.",
                        attempt, MAX_RETRIES, targetModel, ex.getMessage());

                if (!targetModel.equalsIgnoreCase(FALLBACK_STABLE_MODEL)) {
                    log.info("Tentando modelo estável alternativo: {}", FALLBACK_STABLE_MODEL);
                    try {
                        return callGeminiApi(FALLBACK_STABLE_MODEL, requestPayload);
                    } catch (Exception fallbackEx) {
                        log.warn("Falha também no modelo fallback {}: {}", FALLBACK_STABLE_MODEL, fallbackEx.getMessage());
                    }
                }

                if (attempt < MAX_RETRIES) {
                    try {
                        Thread.sleep(RETRY_BACKOFF_MS * attempt);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new QuizGenerationException("Processamento interrompido.");
                    }
                }
            } catch (Exception ex) {
                throw new QuizGenerationException("Falha na geração das perguntas via IA: " + ex.getMessage(), ex);
            }
        }

        throw new QuizGenerationException("O modelo da IA está sob alta demanda temporária no Google. Por favor, tente novamente em alguns segundos.", lastException);
    }

    private String callGeminiApi(String selectedModel, Map<String, Object> requestPayload) {
        String targetUrl = GEMINI_BASE_URL + selectedModel + ":generateContent?key=" + apiKey;

        byte[] responseBytes = restClient.post()
                .uri(URI.create(targetUrl))
                .contentType(MediaType.APPLICATION_JSON)
                .body(requestPayload)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (req, resp) -> {
                    byte[] errorBytes = resp.getBody().readAllBytes();
                    String errorText = new String(errorBytes, StandardCharsets.UTF_8);
                    String parsedMessage = extractErrorMessage(errorText);
                    int statusCode = resp.getStatusCode().value();

                    log.warn("Google Gemini retornou HTTP {}: {}", statusCode, parsedMessage);

                    if (statusCode == 503 || statusCode == 429) {
                        throw new GeminiTransientException(parsedMessage);
                    }
                    throw new QuizGenerationException(parsedMessage);
                })
                .body(byte[].class);

        if (responseBytes == null || responseBytes.length == 0) {
            throw new QuizGenerationException("A resposta da API do Google Gemini veio vazia.");
        }

        return new String(responseBytes, StandardCharsets.UTF_8);
    }

    private String extractErrorMessage(String rawErrorJson) {
        try {
            JsonNode root = objectMapper.readTree(rawErrorJson);
            String message = root.path("error").path("message").asText();
            if (!message.isBlank()) {
                return message;
            }
        } catch (Exception ignored) {
        }
        return rawErrorJson;
    }

    private Map<String, Object> buildRequestPayload(String prompt) {
        Map<String, Object> part = Map.of("text", prompt);
        Map<String, Object> content = Map.of("parts", List.of(part));

        Map<String, Object> alternativaSchema = Map.of(
                "type", "OBJECT",
                "properties", Map.of(
                        "id", Map.of("type", "STRING"),
                        "texto", Map.of("type", "STRING")
                ),
                "required", List.of("id", "texto")
        );

        Map<String, Object> questionSchema = Map.of(
                "type", "OBJECT",
                "properties", Map.of(
                        "pergunta", Map.of("type", "STRING"),
                        "alternativas", Map.of("type", "ARRAY", "items", alternativaSchema),
                        "respostaCorretaId", Map.of("type", "STRING"),
                        "explicacao", Map.of("type", "STRING"),
                        "referencia", Map.of("type", "STRING"),
                        "dificuldade", Map.of("type", "STRING")
                ),
                "required", List.of("pergunta", "alternativas", "respostaCorretaId", "explicacao", "referencia", "dificuldade")
        );

        Map<String, Object> responseSchema = Map.of(
                "type", "ARRAY",
                "items", questionSchema
        );

        Map<String, Object> generationConfig = Map.of(
                "responseMimeType", "application/json",
                "responseSchema", responseSchema,
                "temperature", 0.4
        );

        return Map.of(
                "contents", List.of(content),
                "generationConfig", generationConfig
        );
    }

    private String buildPrompt(String religiao, String tema, String dificuldade, int quantidade) {
        String temaInstrucao = (tema != null && !tema.isBlank())
                ? "Aborde especificamente o tema: \"" + tema + "\" no contexto desta religião."
                : "Aborde conhecimentos gerais, história, tradições e fundamentos da religião.";

        return """
                Você é um especialista em história das religiões, teologia e estudos comparados de religião.
                Sua tarefa é gerar exatamente %d perguntas inéditas de múltipla escolha para um Quiz Educativo.
                
                Parâmetros:
                - Religião: %s
                - Dificuldade: %s
                - %s
                
                Diretrizes fundamentais:
                1. Gere APENAS perguntas relacionadas à religião solicitada.
                2. Respeite rigorosamente a dificuldade (%s).
                3. Crie exatamente 4 alternativas para cada pergunta com IDs "A", "B", "C", "D".
                4. Exatamente UMA alternativa deve ser correta (indicada em 'respostaCorretaId').
                5. As alternativas incorretas devem ser plausíveis mas claramente distintas.
                6. Forneça uma explicação educativa clara e detalhada para a resposta correta.
                7. Forneça uma referência histórica, canônica ou textual verificável (ex: livro sagrado, autor histórico, documento conciliar).
                8. Mantenha tom neutro, respeitoso, ecumênico e estritamente educativo.
                9. Não invente citações ou dados teológicos.
                10. IMPORTANTE: Retorne JSON estritamente válido. Não adicione vírgula após a última alternativa (sem trailing comma).
                
                Formato de saída:
                Retorne estritamente um JSON Array de objetos conforme o formato:
                [
                  {
                    "pergunta": "Texto claro da pergunta?",
                    "alternativas": [
                      {"id": "A", "texto": "Opção A"},
                      {"id": "B", "texto": "Opção B"},
                      {"id": "C", "texto": "Opção C"},
                      {"id": "D", "texto": "Opção D"}
                    ],
                    "respostaCorretaId": "A",
                    "explicacao": "Explicação educacional sobre o porquê desta resposta estar correta.",
                    "referencia": "Nome do livro sagrado ou documento de referência",
                    "dificuldade": "%s"
                  }
                ]
                """.formatted(quantidade, religiao, dificuldade, temaInstrucao, dificuldade, dificuldade);
    }

    private List<QuestionEntity> parseAndValidateResponse(String responseBody, String religiao, String dificuldade, int quantidade) {
        String cleanJson = "";
        try {
            JsonNode rootNode = objectMapper.readTree(responseBody);
            
            JsonNode usageNode = rootNode.path("usageMetadata");
            if (!usageNode.isMissingNode()) {
                int promptTokens = usageNode.path("promptTokenCount").asInt(0);
                int candidateTokens = usageNode.path("candidatesTokenCount").asInt(0);
                int totalTokens = usageNode.path("totalTokenCount").asInt(0);
                log.info("Consumo de Tokens: Prompt={}, Candidatos={}, Total={}", promptTokens, candidateTokens, totalTokens);
            }

            JsonNode candidates = rootNode.path("candidates");
            if (candidates.isEmpty()) {
                throw new QuizGenerationException("A IA não retornou nenhuma resposta candidata.");
            }

            String jsonText = candidates.get(0).path("content").path("parts").get(0).path("text").asText();
            if (jsonText.isBlank()) {
                throw new QuizGenerationException("Conteúdo gerado pela IA está vazio.");
            }

            cleanJson = cleanMarkdownJson(jsonText);
            List<QuestionEntity> questions = objectMapper.readValue(cleanJson, new TypeReference<List<QuestionEntity>>() {});

            if (questions == null || questions.isEmpty()) {
                throw new QuizGenerationException("A IA não gerou uma lista válida de perguntas.");
            }

            return validateAndNormalizeQuestions(questions, dificuldade);
        } catch (QuizGenerationException qe) {
            throw qe;
        } catch (Exception e) {
            log.error("Erro ao analisar JSON retornado pelo Gemini: {}. Payload recebido: {}", e.getMessage(), cleanJson);
            throw new QuizGenerationException("Erro ao processar dados retornados pela IA: " + e.getMessage(), e);
        }
    }

    protected String cleanMarkdownJson(String rawText) {
        if (rawText == null) {
            return "";
        }
        String trimmed = rawText.trim();
        if (trimmed.startsWith("```json")) {
            trimmed = trimmed.substring(7);
        } else if (trimmed.startsWith("```")) {
            trimmed = trimmed.substring(3);
        }
        if (trimmed.endsWith("```")) {
            trimmed = trimmed.substring(0, trimmed.length() - 3);
        }
        trimmed = trimmed.trim();

        int startIndex = trimmed.indexOf('[');
        int endIndex = trimmed.lastIndexOf(']');
        if (startIndex != -1 && endIndex != -1 && endIndex > startIndex) {
            trimmed = trimmed.substring(startIndex, endIndex + 1);
        }

        trimmed = trimmed.replaceAll(",+\\s*([\\]}])", "$1");

        return trimmed.trim();
    }

    private List<QuestionEntity> validateAndNormalizeQuestions(List<QuestionEntity> questions, String defaultDificuldade) {
        List<QuestionEntity> normalized = new ArrayList<>();
        int index = 1;

        for (QuestionEntity q : questions) {
            if (q.getPergunta() == null || q.getPergunta().isBlank()) {
                continue;
            }

            if (q.getId() == null || q.getId().isBlank()) {
                q.setId(UUID.randomUUID().toString());
            }

            if (q.getDificuldade() == null || q.getDificuldade().isBlank()) {
                q.setDificuldade(defaultDificuldade);
            }

            List<AlternativeEntity> alts = q.getAlternativas();
            if (alts == null || alts.size() != 4) {
                log.warn("Questão '{}' descartada por não conter 4 alternativas", q.getPergunta());
                continue;
            }

            Set<String> altIds = new HashSet<>();
            for (AlternativeEntity alt : alts) {
                if (alt.getId() == null || alt.getId().isBlank() || alt.getTexto() == null || alt.getTexto().isBlank()) {
                    throw new QuizGenerationException("Alternativa inválida detectada na questão " + index);
                }
                altIds.add(alt.getId().trim().toUpperCase());
            }

            String correta = q.getRespostaCorretaId() != null ? q.getRespostaCorretaId().trim().toUpperCase() : "";
            if (!altIds.contains(correta)) {
                log.warn("Resposta correta '{}' não encontrada nas alternativas da questão", correta);
                continue;
            }

            q.setRespostaCorretaId(correta);
            normalized.add(q);
            index++;
        }

        if (normalized.isEmpty()) {
            throw new QuizGenerationException("Nenhuma questão atendeu aos critérios de validação de integridade.");
        }

        return normalized;
    }

    private List<QuestionEntity> generateFallbackQuestions(String religiao, String tema, String dificuldade, int quantidade) {
        List<QuestionEntity> list = new ArrayList<>();
        String refTema = (tema != null && !tema.isBlank()) ? " (" + tema + ")" : "";

        for (int i = 1; i <= quantidade; i++) {
            String qId = "demo-q" + i + "-" + UUID.randomUUID().toString().substring(0, 8);
            List<AlternativeEntity> alts = List.of(
                    new AlternativeEntity("A", "Conceito fundamental A sobre " + religiao + " no nível " + dificuldade),
                    new AlternativeEntity("B", "Perspectiva histórica B relacionada a tradições de " + religiao),
                    new AlternativeEntity("C", "Interpretação filosófica C associada a ensinamentos clássicos"),
                    new AlternativeEntity("D", "Aspecto cultural D comumente estudado na teologia comparada")
            );

            list.add(new QuestionEntity(
                    qId,
                    "[" + religiao + refTema + "] Questão " + i + ": Qual dos princípios a seguir é historicamente atribuído a este tema?",
                    alts,
                    "A",
                    "A alternativa A apresenta o ensinamento histórico documentado nos textos tradicionais de " + religiao + ".",
                    "Fontes históricas e acadêmicas de " + religiao,
                    dificuldade
            ));
        }
        return list;
    }

    private static class GeminiTransientException extends RuntimeException {
        public GeminiTransientException(String message) {
            super(message);
        }
    }
}
