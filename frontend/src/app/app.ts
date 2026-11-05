import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { QuizService } from './modules/quiz/services/quiz.service';
import {
  GenerateQuizPayload,
  QuizResult,
  QuizSession,
  SubmitQuizPayload
} from './modules/quiz/models/quiz.models';
import { QuizConfigComponent } from './modules/quiz/components/quiz-config/quiz-config.component';
import { QuizSkeletonComponent } from './modules/quiz/components/quiz-skeleton/quiz-skeleton.component';
import { QuizRunnerComponent } from './modules/quiz/components/quiz-runner/quiz-runner.component';
import { QuizResultComponent } from './modules/quiz/components/quiz-result/quiz-result.component';

type ViewState = 'config' | 'loading' | 'quiz' | 'submitting' | 'result';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [
    CommonModule,
    QuizConfigComponent,
    QuizSkeletonComponent,
    QuizRunnerComponent,
    QuizResultComponent
  ],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {
  private readonly quizService = inject(QuizService);

  viewState: ViewState = 'config';
  errorMessage: string | null = null;
  loadingMessage: string = '';

  lastConfigPayload: GenerateQuizPayload | null = null;
  quizSession: QuizSession | null = null;
  quizResult: QuizResult | null = null;

  dismissError(): void {
    this.errorMessage = null;
  }

  onGenerateQuiz(payload: GenerateQuizPayload): void {
    this.lastConfigPayload = payload;
    this.errorMessage = null;
    this.viewState = 'loading';
    this.loadingMessage = `Conectando à IA para formular ${payload.quantidade} questões de ${payload.religiao}...`;

    this.quizService.generateQuiz(payload).subscribe({
      next: (session) => {
        this.quizSession = session;
        this.quizResult = null;
        this.viewState = 'quiz';
      },
      error: (err) => {
        this.viewState = 'config';
        const serverMsg = err?.error?.message || err?.message || 'Falha ao conectar com o serviço de IA.';
        this.errorMessage = `Não foi possível gerar o quiz: ${serverMsg}`;
      }
    });
  }

  onSubmitQuiz(userAnswers: Record<string, string>): void {
    if (!this.quizSession) return;

    this.errorMessage = null;
    this.viewState = 'submitting';
    this.loadingMessage = 'Enviando respostas e calculando pontuação com base no gabarito oficial...';

    const respostasPayload = this.quizSession.questoes.map(q => ({
      perguntaId: q.id,
      respostaSelecionadaId: userAnswers[q.id] || ''
    }));

    const payload: SubmitQuizPayload = {
      quizId: this.quizSession.quizId,
      respostas: respostasPayload
    };

    this.quizService.submitQuiz(payload).subscribe({
      next: (result) => {
        this.quizResult = result;
        this.viewState = 'result';
      },
      error: (err) => {
        this.viewState = 'quiz';
        const serverMsg = err?.error?.message || err?.message || 'Falha ao submeter o quiz.';
        this.errorMessage = `Erro na correção do quiz: ${serverMsg}`;
      }
    });
  }

  onRestartConfig(): void {
    this.viewState = 'config';
    this.quizSession = null;
    this.quizResult = null;
    this.errorMessage = null;
  }

  onRetrySameConfig(): void {
    if (this.lastConfigPayload) {
      this.onGenerateQuiz(this.lastConfigPayload);
    } else {
      this.onRestartConfig();
    }
  }
}
