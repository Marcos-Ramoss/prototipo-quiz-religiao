# POC — Quiz Religioso com Geração Automática de Perguntas por IA

Prova de Conceito (POC) de um Quiz Religioso interativo que utiliza Inteligência Artificial generativa (Google Gemini API) para gerar dinamicamente perguntas, alternativas, respostas corretas e explicações educativas com base na religião e configurações selecionadas.

Este projeto adota a **Arquitetura Modular (Package-by-Feature) e Clean Architecture** no padrão do projeto **[alvaraes-moderna](https://github.com/Marcos-Ramoss/alvaraes-moderna)**.

---

## 🏛️ Estrutura e Padrão Arquitetural

### Backend (Spring Boot 3.3.5 + Java 17)
Inspirado na organização modular de `alvaraes-moderna/backend/src`:

```
backend/
├── .env                                  # Arquivo de variáveis de ambiente locais (não versionado)
├── .env.example                          # Modelo versionado com documentação das variáveis
├── src/main/java/com/quiz/religiousquiz/
│   ├── ReligiousQuizApplication.java     # Inicializa DotenvLoader antes do Spring Boot
│   ├── config/
│   │   ├── CorsConfig.java
│   │   └── DotenvLoader.java             # Carregador automático de arquivos .env (zero dependências)
│   ├── common/                           # Utilitários, DTOs globais e tratamento de erros
│   │   ├── dto/
│   │   │   └── ErrorResponse.java
│   │   └── exception/
│   │       └── GlobalExceptionHandler.java   # @RestControllerAdvice
│   └── modules/
│       └── quiz/                         # Módulo isolado de domínio do Quiz
│           ├── controller/
│           │   └── QuizController.java   # Apenas orquestra requisições e respostas
│           ├── usecase/
│           │   ├── GenerateQuizUseCase.java  # Regra de negócio para geração de perguntas com IA
│           │   └── SubmitQuizUseCase.java    # Regra de correção e cálculo de pontuação
│           ├── repository/
│           │   ├── QuizSessionRepository.java
│           │   └── InMemoryQuizSessionRepository.java # Armazenamento temporário com TTL
│           ├── service/
│           │   ├── AiQuestionGenerator.java
│           │   └── GeminiAiQuestionGenerator.java     # Integração com Google Gemini
│           ├── dto/                      # Records imutáveis de entrada e saída
│           │   ├── GenerateQuizRequest.java
│           │   ├── SubmitQuizRequest.java
│           │   ├── AnswerSubmissionDto.java
│           │   ├── QuizSessionResponse.java
│           │   ├── PublicQuestionDto.java
│           │   ├── AlternativeDto.java
│           │   ├── QuizResultResponse.java
│           │   └── QuestionResultDto.java
│           ├── domain/                   # Entidades e enums de domínio
│           │   ├── QuizSession.java
│           │   ├── QuestionEntity.java
│           │   ├── AlternativeEntity.java
│           │   ├── Religion.java
│           │   └── Difficulty.java
│           └── exception/                # Exceções específicas do domínio
│               ├── QuizNotFoundException.java
│               ├── QuizExpiredException.java
│               ├── QuizGenerationException.java
│               └── InvalidQuizParametersException.java
```

### Frontend (Angular 20 Standalone)
Inspirado na modularidade por feature e diretrizes de UX de `alvaraes-moderna/frontend/src`:

```
frontend/src/app/
├── app.ts                                # Shell principal
├── app.html
├── app.css
├── app.config.ts                         # Configuração com provideHttpClient()
└── modules/
    └── quiz/                             # Módulo isolado do Quiz
        ├── models/
        │   └── quiz.models.ts            # Interfaces TypeScript
        ├── services/
        │   └── quiz.service.ts           # Cliente HTTP
        └── components/
            ├── quiz-config/              # Configuração com tags em carrossel horizontal
            │   ├── quiz-config.component.ts
            │   ├── quiz-config.component.html
            │   └── quiz-config.component.css
            ├── quiz-skeleton/            # Skeletons que previnem Layout Shifts
            │   ├── quiz-skeleton.component.ts
            │   ├── quiz-skeleton.component.html
            │   └── quiz-skeleton.component.css
            ├── quiz-runner/              # Execução de perguntas com touch targets
            │   ├── quiz-runner.component.ts
            │   ├── quiz-runner.component.html
            │   └── quiz-runner.component.css
            └── quiz-result/              # Tela de resultados com gabarito e explicações
                ├── quiz-result.component.ts
                ├── quiz-result.component.html
                └── quiz-result.component.css
```

---

## ⚙️ Variáveis de Ambiente (`.env`)

Seguindo o padrão do `alvaraes-moderna`, as variáveis sensíveis e configuráveis por ambiente ficam no arquivo `.env` (com modelo em `.env.example`):

```env
# Porta da API Spring Boot
SERVER_PORT=8080

# Chave de API Google Gemini (obtenha em https://aistudio.google.com/)
GEMINI_API_KEY=

# Modelo da IA Generativa
GEMINI_MODEL=gemini-1.5-flash

# Timeout em segundos para requisições de IA
AI_TIMEOUT_SECONDS=25

# Origem permitida para o Frontend Angular
CORS_ALLOWED_ORIGINS=http://localhost:4200

# Tempo de vida (TTL) da sessão temporária do Quiz em minutos
QUIZ_SESSION_TTL_MINUTES=60
```

> **Nota:** As configurações fixas do framework (como `spring.application.name=religious-quiz-api` e `quiz.ai.provider=gemini`) permanecem em `application.properties`, que referencia as variáveis do `.env` com valores de fallback seguros (`${VARIAVEL:padrao}`).

---

## 🚀 Como Executar

### 1. Iniciar o Backend
```bash
cd backend

# O arquivo .env será lido automaticamente ao inicializar!
mvn spring-boot:run
```
Disponível em: `http://localhost:8080`. Para rodar os testes: `mvn test`.

### 2. Iniciar o Frontend
```bash
cd frontend
npm start
```
Disponível em: `http://localhost:4200`.
