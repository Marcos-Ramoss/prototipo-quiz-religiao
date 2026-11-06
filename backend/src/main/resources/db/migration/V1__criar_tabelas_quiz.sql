-- ==============================================================================
-- Migration V1: Criação da Estrutura Oficial de Quizzes no Supabase (PostgreSQL)
-- Projeto: Convivere / Diálogo Inter-religioso
-- ==============================================================================

-- 1. Tabela Principal de Quizzes (Sessões criadas com IA)
CREATE TABLE IF NOT EXISTS quizzes (
    id VARCHAR(64) PRIMARY KEY,
    religiao VARCHAR(100) NOT NULL,
    tema VARCHAR(255),
    dificuldade VARCHAR(30) NOT NULL,
    quantidade INT NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'CRIADO',
    criado_em TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    expira_em TIMESTAMPTZ NOT NULL
);

-- 2. Tabela de Perguntas de Cada Quiz
CREATE TABLE IF NOT EXISTS quiz_perguntas (
    id VARCHAR(64) PRIMARY KEY,
    quiz_id VARCHAR(64) NOT NULL REFERENCES quizzes(id) ON DELETE CASCADE,
    ordem INT NOT NULL,
    pergunta TEXT NOT NULL,
    alternativas JSONB NOT NULL,
    resposta_correta_id VARCHAR(10) NOT NULL,
    explicacao TEXT,
    referencia TEXT,
    dificuldade VARCHAR(30) NOT NULL
);

-- 3. Tabela de Participações e Resultados Concluídos
CREATE TABLE IF NOT EXISTS quiz_participacoes (
    id VARCHAR(64) PRIMARY KEY,
    quiz_id VARCHAR(64) NOT NULL REFERENCES quizzes(id) ON DELETE CASCADE,
    total_questoes INT NOT NULL,
    acertos INT NOT NULL,
    erros INT NOT NULL,
    aproveitamento NUMERIC(5, 2) NOT NULL,
    respostas_usuario JSONB,
    concluido_em TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Índices para consultas eficientes e filtros
CREATE INDEX IF NOT EXISTS idx_quizzes_religiao ON quizzes(religiao);
CREATE INDEX IF NOT EXISTS idx_quizzes_criado_em ON quizzes(criado_em DESC);
CREATE INDEX IF NOT EXISTS idx_quiz_perguntas_quiz_id ON quiz_perguntas(quiz_id);
CREATE INDEX IF NOT EXISTS idx_quiz_participacoes_quiz_id ON quiz_participacoes(quiz_id);
CREATE INDEX IF NOT EXISTS idx_quiz_participacoes_concluido_em ON quiz_participacoes(concluido_em DESC);

