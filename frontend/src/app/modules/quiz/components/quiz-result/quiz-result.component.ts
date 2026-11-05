import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { QuizResult } from '../../models/quiz.models';
import confetti from 'canvas-confetti';

export interface MedalInfo {
  type: 'ouro-diamante' | 'ouro' | 'prata' | 'bronze' | 'participacao';
  label: string;
  icon: string;
  cssClass: string;
  description: string;
}

@Component({
  selector: 'app-quiz-result',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './quiz-result.component.html',
  styleUrl: './quiz-result.component.css'
})
export class QuizResultComponent implements OnInit {
  @Input({ required: true }) result!: QuizResult;
  @Output() restart = new EventEmitter<void>();
  @Output() retry = new EventEmitter<void>();

  currentDate: string = new Date().toLocaleDateString('pt-BR');

  ngOnInit(): void {
    if (this.result && this.result.aproveitamento >= 50) {
      this.triggerConfetti();
    }
  }

  triggerCelebration(): void {
    this.triggerConfetti();
  }

  triggerConfetti(): void {
    try {
      confetti({
        particleCount: 110,
        spread: 75,
        origin: { y: 0.6 }
      });
      setTimeout(() => {
        confetti({
          particleCount: 60,
          angle: 60,
          spread: 60,
          origin: { x: 0.05, y: 0.65 }
        });
        confetti({
          particleCount: 60,
          angle: 120,
          spread: 60,
          origin: { x: 0.95, y: 0.65 }
        });
      }, 300);
    } catch (e) {
      console.warn('Erro ao disparar confetes', e);
    }
  }

  getMedal(aproveitamento: number): MedalInfo {
    if (aproveitamento === 100) {
      return {
        type: 'ouro-diamante',
        label: 'Medalha de Ouro Diamante',
        icon: '💎🥇',
        cssClass: 'medal-diamond',
        description: 'Desempenho histórico e teológico impecável com 100% de precisão!'
      };
    } else if (aproveitamento >= 80) {
      return {
        type: 'ouro',
        label: 'Medalha de Ouro',
        icon: '🥇',
        cssClass: 'medal-gold',
        description: 'Excelente domínio dos fundamentos, narrativas e tradições!'
      };
    } else if (aproveitamento >= 60) {
      return {
        type: 'prata',
        label: 'Medalha de Prata',
        icon: '🥈',
        cssClass: 'medal-silver',
        description: 'Ótimo aproveitamento com sólido entendimento dos conceitos!'
      };
    } else if (aproveitamento >= 40) {
      return {
        type: 'bronze',
        label: 'Medalha de Bronze',
        icon: '🥉',
        cssClass: 'medal-bronze',
        description: 'Bom início de aprendizado! O gabarito enriquecerá seus estudos.'
      };
    } else {
      return {
        type: 'participacao',
        label: 'Certificado de Aprendizado',
        icon: '🎖️',
        cssClass: 'medal-starter',
        description: 'Cada erro é uma chance de descobrir novos fatos e fontes históricas.'
      };
    }
  }

  getScoreFeedback(aproveitamento: number): { title: string; badgeClass: string; message: string } {
    if (aproveitamento === 100) {
      return {
        title: 'Desempenho Perfeito!',
        badgeClass: 'score-badge-gold',
        message: 'Você acertou todas as questões com maestria teológica e histórica.'
      };
    } else if (aproveitamento >= 70) {
      return {
        title: 'Excelente Resultado!',
        badgeClass: 'score-badge-green',
        message: 'Você demonstrou sólido conhecimento sobre os princípios abordados.'
      };
    } else if (aproveitamento >= 50) {
      return {
        title: 'Bom Aproveitamento!',
        badgeClass: 'score-badge-blue',
        message: 'Você possui boa noção sobre o tema; a revisão ajudará a fixar os detalhes.'
      };
    } else {
      return {
        title: 'Jornada de Conhecimento!',
        badgeClass: 'score-badge-amber',
        message: 'A leitura das explicações abaixo trará novos horizontes sobre esta religião.'
      };
    }
  }

  exportToPdf(): void {
    window.print();
  }
}
