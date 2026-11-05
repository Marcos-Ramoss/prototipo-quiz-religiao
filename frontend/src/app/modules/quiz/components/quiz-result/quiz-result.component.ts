import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { QuizResult } from '../../models/quiz.models';

@Component({
  selector: 'app-quiz-result',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './quiz-result.component.html',
  styleUrl: './quiz-result.component.css'
})
export class QuizResultComponent {
  @Input({ required: true }) result!: QuizResult;
  @Output() restart = new EventEmitter<void>();
  @Output() retry = new EventEmitter<void>();

  getScoreFeedback(aproveitamento: number): { title: string; badgeClass: string; message: string } {
    if (aproveitamento === 100) {
      return {
        title: 'Desempenho Perfeito! 🏆',
        badgeClass: 'score-badge-gold',
        message: 'Excelente domínio histórico e teológico sobre as tradições e fundamentos desta religião.'
      };
    } else if (aproveitamento >= 70) {
      return {
        title: 'Ótimo Resultado! 🌟',
        badgeClass: 'score-badge-green',
        message: 'Você demonstrou sólido conhecimento sobre os princípios e narrativas religiosas abordadas.'
      };
    } else if (aproveitamento >= 50) {
      return {
        title: 'Bom Aproveitamento! 📚',
        badgeClass: 'score-badge-blue',
        message: 'Você tem boa noção sobre o tema, e a leitura das explicações ajudará a aprofundar ainda mais.'
      };
    } else {
      return {
        title: 'Oportunidade de Aprendizado 🌱',
        badgeClass: 'score-badge-amber',
        message: 'Um ótimo ponto de partida para estudar a diversidade, os textos e a rica história desta religião.'
      };
    }
  }
}

