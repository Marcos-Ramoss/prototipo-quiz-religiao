import { Component, EventEmitter, Input, OnDestroy, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { PublicQuestion, QuizSession } from '../../models/quiz.models';

@Component({
  selector: 'app-quiz-runner',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './quiz-runner.component.html',
  styleUrl: './quiz-runner.component.css'
})
export class QuizRunnerComponent implements OnDestroy {
  @Input({ required: true }) session!: QuizSession;
  @Output() submitQuiz = new EventEmitter<Record<string, string>>();

  currentQuestionIndex: number = 0;
  userAnswers: Record<string, string> = {};

  isSpeaking: boolean = false;
  private currentUtterance: SpeechSynthesisUtterance | null = null;

  ngOnDestroy(): void {
    this.stopSpeech();
  }

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

  toggleSpeechNarration(): void {
    if (typeof window === 'undefined' || !('speechSynthesis' in window)) {
      alert('Seu navegador não possui suporte nativo à síntese de voz.');
      return;
    }

    if (this.isSpeaking) {
      this.stopSpeech();
      return;
    }

    const q = this.currentQuestion;
    if (!q) return;

    const altsText = q.alternativas
      .map(a => `Alternativa ${a.id}: ${a.texto}`)
      .join('. ');

    const fullScript = `Questão número ${this.currentQuestionIndex + 1}. ${q.pergunta}. ${altsText}`;

    this.stopSpeech();

    const utterance = new SpeechSynthesisUtterance(fullScript);
    utterance.lang = 'pt-BR';
    utterance.rate = 1.05;
    utterance.pitch = 1.0;

    const voices = window.speechSynthesis.getVoices();
    const ptVoice = voices.find(v => v.lang.includes('pt-BR') || v.lang.includes('pt_BR') || v.lang.startsWith('pt'));
    if (ptVoice) {
      utterance.voice = ptVoice;
    }

    utterance.onstart = () => {
      this.isSpeaking = true;
    };
    utterance.onend = () => {
      this.isSpeaking = false;
      this.currentUtterance = null;
    };
    utterance.onerror = () => {
      this.isSpeaking = false;
      this.currentUtterance = null;
    };

    this.currentUtterance = utterance;
    window.speechSynthesis.speak(utterance);
  }

  stopSpeech(): void {
    if (typeof window !== 'undefined' && 'speechSynthesis' in window) {
      window.speechSynthesis.cancel();
    }
    this.isSpeaking = false;
    this.currentUtterance = null;
  }

  nextQuestion(): void {
    this.stopSpeech();
    if (this.currentQuestionIndex < this.totalQuestions - 1) {
      this.currentQuestionIndex++;
    } else {
      this.finish();
    }
  }

  previousQuestion(): void {
    this.stopSpeech();
    if (this.currentQuestionIndex > 0) {
      this.currentQuestionIndex--;
    }
  }

  goToQuestion(index: number): void {
    this.stopSpeech();
    if (index >= 0 && index < this.totalQuestions) {
      this.currentQuestionIndex = index;
    }
  }

  finish(): void {
    this.stopSpeech();
    this.submitQuiz.emit(this.userAnswers);
  }
}
