-- ==============================================================================
-- Migration V2: Suporte a Título, Tempo Limite e Status (Rascunho / Publicado)
-- Projeto: Convivere / Diálogo Inter-religioso
-- ==============================================================================

-- 1. Novas colunas na tabela quizzes
ALTER TABLE quizzes ADD COLUMN IF NOT EXISTS titulo VARCHAR(150);
ALTER TABLE quizzes ADD COLUMN IF NOT EXISTS tempo_limite_segundos INT NOT NULL DEFAULT 0;

-- 2. Atualizar quizzes existentes com título padrão e status 'PUBLICADO'
UPDATE quizzes 
SET titulo = 'Quiz sobre ' || religiao 
WHERE titulo IS NULL OR titulo = '';

UPDATE quizzes 
SET status = 'PUBLICADO' 
WHERE status = 'CRIADO';

-- 3. Índices adicionais para consultas filtradas
CREATE INDEX IF NOT EXISTS idx_quizzes_status ON quizzes(status);
CREATE INDEX IF NOT EXISTS idx_quizzes_status_criado_em ON quizzes(status, criado_em DESC);

