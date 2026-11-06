import { Component, EventEmitter, Input, OnDestroy, OnInit, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { PublicQuestion, QuizSession } from '../../models/quiz.models';

@Component({
  selector: 'app-quiz-runner',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './quiz-runner.component.html',
  styleUrl: './quiz-runner.component.css'
})
export class QuizRunnerComponent implements OnInit, OnDestroy {
  @Input({ required: true }) session!: QuizSession;
  @Output() submitQuiz = new EventEmitter<Record<string, string>>();

  currentQuestionIndex: number = 0;
  userAnswers: Record<string, string> = {};

  tempoRestanteSegundos: number = 0;
  private timerInterval: any = null;

  isSpeaking: boolean = false;
  private currentUtterance: SpeechSynthesisUtterance | null = null;

  ngOnInit(): void {
    if (this.session.tempoLimiteSegundos && this.session.tempoLimiteSegundos > 0) {
      this.tempoRestanteSegundos = this.session.tempoLimiteSegundos;
      this.iniciarTemporizador();
    }
  }

  get tempoLimiteAtivo(): boolean {
    return (this.session.tempoLimiteSegundos || 0) > 0;
  }

  get tempoRestanteFormatado(): string {
    const minutos = Math.floor(this.tempoRestanteSegundos / 60);
    const segundos = this.tempoRestanteSegundos % 60;
    return `${minutos.toString().padStart(2, '0')}:${segundos.toString().padStart(2, '0')}`;
  }

  private iniciarTemporizador(): void {
    this.timerInterval = setInterval(() => {
      if (this.tempoRestanteSegundos > 0) {
        this.tempoRestanteSegundos--;
      } else {
        this.pararTemporizador();
        this.finalizar();
      }
    }, 1000);
  }

  private pararTemporizador(): void {
    if (this.timerInterval) {
      clearInterval(this.timerInterval);
      this.timerInterval = null;
    }
  }

  ngOnDestroy(): void {
    this.pararTemporizador();
    this.pararNarracaoVoz();
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

  selecionarAlternativa(altId: string): void {
    if (!this.currentQuestion) return;
    this.userAnswers[this.currentQuestion.id] = altId;
  }

  estaAlternativaSelecionada(altId: string): boolean {
    if (!this.currentQuestion) return false;
    return this.userAnswers[this.currentQuestion.id] === altId;
  }

  alternarNarracaoVoz(): void {
    if (typeof window === 'undefined' || !('speechSynthesis' in window)) {
      alert('Seu navegador não possui suporte nativo à síntese de voz.');
      return;
    }

    if (this.isSpeaking) {
      this.pararNarracaoVoz();
      return;
    }

    const q = this.currentQuestion;
    if (!q) return;

    const altsText = q.alternativas
      .map(a => `Alternativa ${a.id}: ${a.texto}`)
      .join('. ');

    const fullScript = `Questão número ${this.currentQuestionIndex + 1}. ${q.pergunta}. ${altsText}`;

    this.pararNarracaoVoz();

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

  pararNarracaoVoz(): void {
    if (typeof window !== 'undefined' && 'speechSynthesis' in window) {
      window.speechSynthesis.cancel();
    }
    this.isSpeaking = false;
    this.currentUtterance = null;
  }

  proximaQuestao(): void {
    this.pararNarracaoVoz();
    if (this.currentQuestionIndex < this.totalQuestions - 1) {
      this.currentQuestionIndex++;
    } else {
      this.finalizar();
    }
  }

  questaoAnterior(): void {
    this.pararNarracaoVoz();
    if (this.currentQuestionIndex > 0) {
      this.currentQuestionIndex--;
    }
  }

  irParaQuestao(index: number): void {
    this.pararNarracaoVoz();
    if (index >= 0 && index < this.totalQuestions) {
      this.currentQuestionIndex = index;
    }
  }

  finalizar(): void {
    this.pararNarracaoVoz();
    this.submitQuiz.emit(this.userAnswers);
  }

  // Aliases semânticos para templates
  selectAlternative = (id: string) => this.selecionarAlternativa(id);
  isAlternativeSelected = (id: string) => this.estaAlternativaSelecionada(id);
  toggleSpeechNarration = () => this.alternarNarracaoVoz();
  stopSpeech = () => this.pararNarracaoVoz();
  nextQuestion = () => this.proximaQuestao();
  previousQuestion = () => this.questaoAnterior();
  goToQuestion = (i: number) => this.irParaQuestao(i);
  finish = () => this.finalizar();
}
