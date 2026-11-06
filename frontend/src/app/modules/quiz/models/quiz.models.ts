export interface Alternative {
  id: string;
  texto: string;
}

export interface PublicQuestion {
  id: string;
  pergunta: string;
  alternativas: Alternative[];
  dificuldade: string;
}

export interface QuizSession {
  quizId: string;
  titulo?: string;
  religiao: string;
  tema: string;
  dificuldade: string;
  status?: string;
  tempoLimiteSegundos?: number;
  questoes: PublicQuestion[];
}

export interface QuizSummary {
  id: string;
  titulo: string;
  religiao: string;
  tema: string;
  dificuldade: string;
  totalPerguntas: number;
  tempoLimiteSegundos: number;
  status: string;
  criadoEm: string;
}

export interface PublishQuizPayload {
  titulo?: string;
  tempoLimiteSegundos?: number;
}

export interface GenerateQuizPayload {
  religiao: string;
  tema?: string;
  dificuldade: string;
  quantidade: number;
}

export interface AnswerSubmission {
  perguntaId: string;
  respostaSelecionadaId: string;
}

export interface SubmitQuizPayload {
  quizId: string;
  respostas: AnswerSubmission[];
}

export interface QuestionResult {
  perguntaId: string;
  pergunta: string;
  alternativas: Alternative[];
  respostaSelecionadaId: string;
  respostaCorretaId: string;
  acertou: boolean;
  explicacao: string;
  referencia: string;
}

export interface QuizResult {
  totalQuestoes: number;
  acertos: number;
  erros: number;
  aproveitamento: number;
  detalhes: QuestionResult[];
}

export interface ReligionOption {
  id: string;
  name: string;
  icon: string;
  desc: string;
  color?: string;
  category?: string;
}

export interface QuizStats {
  quizzesCriados: number;
  perguntasDisponiveis: number;
  tradicoesCadastradas: number;
  participacoesRealizadas: number;
}

export interface ActivityItem {
  id: string;
  type: 'quiz_created' | 'quiz_completed' | 'questions_added' | 'tradition_updated';
  title: string;
  subtitle: string;
  timeAgo: string;
  icon: string;
  iconBgClass: string;
}


