package com.quiz.religiousquiz.common.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@Tag(name = "Health Check", description = "Endpoints de verificação de integridade e status da API")
public class HealthController {

    @GetMapping("/")
    @Operation(summary = "Ponto de entrada raiz da API", description = "Informa status operacional, nome da aplicação e links de documentação.")
    public ResponseEntity<Map<String, Object>> root() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "app", "Religious Quiz API",
                "message", "API operacional e pronta para receber requisições do frontend.",
                "swagger", "/swagger-ui.html"
        ));
    }

    @GetMapping("/api/health")
    @Operation(summary = "Health check simplificado", description = "Retorna confirmação de conectividade para load balancers e monitoramento da nuvem.")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of("status", "UP"));
    }
}

