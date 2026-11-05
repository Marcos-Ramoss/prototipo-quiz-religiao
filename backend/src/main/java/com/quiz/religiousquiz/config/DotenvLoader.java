package com.quiz.religiousquiz.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public final class DotenvLoader {

    private static final Logger log = LoggerFactory.getLogger(DotenvLoader.class);

    private DotenvLoader() {
    }

    public static void load() {
        Path[] candidatePaths = new Path[] {
                Paths.get(".env"),
                Paths.get("backend/.env"),
                Paths.get("../.env")
        };

        for (Path path : candidatePaths) {
            if (Files.isRegularFile(path)) {
                log.info("Carregando variáveis de ambiente do arquivo: {}", path.toAbsolutePath());
                applyEnvFile(path);
                return;
            }
        }
        log.info("Nenhum arquivo .env encontrado. Utilizando variáveis do sistema e defaults.");
    }

    private static void applyEnvFile(Path envFile) {
        try {
            List<String> lines = Files.readAllLines(envFile);
            for (String rawLine : lines) {
                parseAndSetProperty(rawLine);
            }
        } catch (IOException e) {
            log.warn("Falha ao ler o arquivo .env em {}: {}", envFile, e.getMessage());
        }
    }

    private static void parseAndSetProperty(String rawLine) {
        String line = rawLine.trim();
        if (line.isEmpty() || line.startsWith("#") || !line.contains("=")) {
            return;
        }

        int separatorIndex = line.indexOf('=');
        String key = line.substring(0, separatorIndex).trim();
        String value = line.substring(separatorIndex + 1).trim();

        if ((value.startsWith("\"") && value.endsWith("\"")) || (value.startsWith("'") && value.endsWith("'"))) {
            value = value.substring(1, value.length() - 1);
        }

        if (System.getProperty(key) == null && System.getenv(key) == null) {
            System.setProperty(key, value);
        }
    }
}

