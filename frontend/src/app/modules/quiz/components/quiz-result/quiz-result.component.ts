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
      this.dispararConfetes();
    }
  }

  dispararCelebracao(): void {
    this.dispararConfetes();
  }

  dispararConfetes(): void {
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

  exportarParaPdf(): void {
    if (typeof window !== 'undefined') {
      window.print();
    }
  }

  obterFeedbackPontuacao(aproveitamento: number): { title: string; message: string; badgeClass: string } {
    if (aproveitamento === 100) {
      return {
        title: 'Excelente! Domínio Absoluto!',
        message: 'Você demonstrou conhecimento profundo sobre os ensinamentos, a história e as tradições.',
        badgeClass: 'score-perfect'
      };
    }
    if (aproveitamento >= 80) {
      return {
        title: 'Muito Bom! Ótimo Aproveitamento!',
        message: 'Você tem uma sólida compreensão dos princípios e práticas dessa tradição religiosa.',
        badgeClass: 'score-great'
      };
    }
    if (aproveitamento >= 60) {
      return {
        title: 'Bom Trabalho! Bom Conhecimento!',
        message: 'Você compreende os aspectos centrais. Revise as questões abaixo para enriquecer ainda mais.',
        badgeClass: 'score-good'
      };
    }
    return {
      title: 'Desafio Concluído! Continue Aprendendo!',
      message: 'O diálogo inter-religioso é uma jornada contínua. Explore o gabarito comentado para aprofundar.',
      badgeClass: 'score-learning'
    };
  }

  obterMedalha(aproveitamento: number): MedalInfo {
    if (aproveitamento === 100) {
      return {
        type: 'ouro-diamante',
        label: 'Medalha de Diamante ✦',
        icon: '💎',
        cssClass: 'medal-diamond',
        description: 'Pontuação perfeita! Erudição e domínio exemplar sobre a tradição.'
      };
    }
    if (aproveitamento >= 80) {
      return {
        type: 'ouro',
        label: 'Medalha de Ouro 🥇',
        icon: '🥇',
        cssClass: 'medal-gold',
        description: 'Excelente resultado! Alto nível de compreensão e respeito aos saberes.'
      };
    }
    if (aproveitamento >= 60) {
      return {
        type: 'prata',
        label: 'Medalha de Prata 🥈',
        icon: '🥈',
        cssClass: 'medal-silver',
        description: 'Bom aproveitamento! Conhecimento consolidado nos temas essenciais.'
      };
    }
    if (aproveitamento >= 40) {
      return {
        type: 'bronze',
        label: 'Medalha de Bronze 🥉',
        icon: '🥉',
        cssClass: 'medal-bronze',
        description: 'Boa participação! O diálogo e a curiosidade são o primeiro passo.'
      };
    }
    return {
      type: 'participacao',
      label: 'Certificado de Participação 📜',
      icon: '🌱',
      cssClass: 'medal-starter',
      description: 'Participação registrada! Explore os comentários educativos para expandir sua visão.'
    };
  }

  // Aliases para templates existentes
  triggerCelebration = () => this.dispararCelebracao();
  triggerConfetti = () => this.dispararConfetes();
  exportToPdf = () => this.exportarParaPdf();
  getScoreFeedback = (a: number) => this.obterFeedbackPontuacao(a);
  getMedal = (a: number) => this.obterMedalha(a);
}
