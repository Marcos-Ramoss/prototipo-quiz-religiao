-- ==============================================================================
-- Migration V3: Ajustar Expiração para Quizzes Publicados (Catálogo Perene)
-- Projeto: Convivere / Diálogo Inter-religioso
-- ==============================================================================

-- 1. Quizzes do catálogo (PUBLICADOS) são perenes e válidos a longo prazo (100 anos)
UPDATE quizzes 
SET expira_em = criado_em + INTERVAL '100 years'
WHERE status = 'PUBLICADO' OR expira_em <= NOW();

