import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { PublicQuestion, QuizSession } from '../../models/quiz.models';

@Component({
  selector: 'app-quiz-runner',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './quiz-runner.component.html',
  styleUrl: './quiz-runner.component.css'
})
export class QuizRunnerComponent {
  @Input({ required: true }) session!: QuizSession;
  @Output() submitQuiz = new EventEmitter<Record<string, string>>();

  currentQuestionIndex: number = 0;
  userAnswers: Record<string, string> = {};

  get totalQuestions(): number {
    return this.session.questoes?.length || 0;
  }

  get currentQuestion(): PublicQuestion | null {
    if (!this.session.questoes || this.session.questoes.length === 0) return null;
    return this.session.questoes[this.currentQuestionIndex] || null;
  }

  get isCurrentQuestionAnswered(): boolean {
    const q = this.currentQuestion;
    return !!q && !!this.userAnswers[q.id];
  }

  get progressPercentage(): number {
    if (this.totalQuestions === 0) return 0;
    return Math.round(((this.currentQuestionIndex + 1) / this.totalQuestions) * 100);
  }

  selectAlternative(altId: string): void {
    if (!this.currentQuestion) return;
    this.userAnswers[this.currentQuestion.id] = altId;
  }

  isAlternativeSelected(altId: string): boolean {
    if (!this.currentQuestion) return false;
    return this.userAnswers[this.currentQuestion.id] === altId;
  }

  nextQuestion(): void {
    if (this.currentQuestionIndex < this.totalQuestions - 1) {
      this.currentQuestionIndex++;
    } else {
      this.finish();
    }
  }

  previousQuestion(): void {
    if (this.currentQuestionIndex > 0) {
      this.currentQuestionIndex--;
    }
  }

  goToQuestion(index: number): void {
    if (index >= 0 && index < this.totalQuestions) {
      this.currentQuestionIndex = index;
    }
  }

  finish(): void {
    this.submitQuiz.emit(this.userAnswers);
  }
}

