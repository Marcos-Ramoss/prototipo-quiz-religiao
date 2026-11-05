import { Component, EventEmitter, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { GenerateQuizPayload, ReligionOption } from '../../models/quiz.models';

@Component({
  selector: 'app-quiz-config',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './quiz-config.component.html',
  styleUrl: './quiz-config.component.css'
})
export class QuizConfigComponent {
  @Output() generate = new EventEmitter<GenerateQuizPayload>();

  religiaoSelecionada: string = 'Judaísmo';
  dificuldadeSelecionada: string = 'Médio';
  quantidadeSelecionada: number = 10;
  temaInformado: string = '';

  readonly religions: ReligionOption[] = [
    { id: 'Cristianismo', name: 'Cristianismo', icon: '✝', desc: 'Evangelhos, história e tradições' },
    { id: 'Islamismo', name: 'Islamismo', icon: '☪', desc: 'Alcorão, história e práticas' },
    { id: 'Budismo', name: 'Budismo', icon: '☸', desc: 'Ensinamentos e filosofia' },
    { id: 'Hinduísmo', name: 'Hinduísmo', icon: 'ॐ', desc: 'Textos, filosofias e práticas' },
    { id: 'Judaísmo', name: 'Judaísmo', icon: '✡', desc: 'Torá, costumes e história' },
    { id: 'Espiritismo', name: 'Espiritismo', icon: '✦', desc: 'Obras, filosofia e ensinamentos' },
    { id: 'Religiões de matriz africana', name: 'Matriz africana', icon: '🌿', desc: 'Ancestralidade e diversidade' },
    { id: 'Outras tradições', name: 'Outras tradições', icon: '✺', desc: 'Crenças e filosofias do mundo' }
  ];

  readonly difficulties: string[] = ['Fácil', 'Médio', 'Difícil'];
  readonly quantities: number[] = [5, 10, 15];

  readonly suggestedThemesMap: Record<string, string[]> = {
    'Cristianismo': ['Parábolas de Jesus', 'Sermão da Montanha', 'História dos Apóstolos'],
    'Islamismo': ['Os Cinco Pilares', 'Alcorão Sagrado', 'Profetas no Islã'],
    'Budismo': ['Quatro Nobres Verdades', 'Caminho Óctuplo', 'Vida de Siddhartha'],
    'Hinduísmo': ['Bhagavad Gita', 'Dharma e Karma', 'Divindades e Vedas'],
    'Judaísmo': ['Torá', 'Festas judaicas', 'História de Israel'],
    'Espiritismo': ['O Livro dos Espíritos', 'Allan Kardec', 'Causa e Efeito'],
    'Religiões de matriz africana': ['Ancestralidade', 'Orixás e Natureza', 'Sincretismo Cultural'],
    'Outras tradições': ['Taoísmo e Harmonia', 'Xintoísmo e Natureza', 'Filosofias Orientais']
  };

  get currentSuggestedThemes(): string[] {
    return this.suggestedThemesMap[this.religiaoSelecionada] || [];
  }

  get selectedReligionName(): string {
    const found = this.religions.find(r => r.id === this.religiaoSelecionada);
    return found ? found.name : this.religiaoSelecionada;
  }

  selectReligion(id: string): void {
    this.religiaoSelecionada = id;
  }

  selectDifficulty(dif: string): void {
    this.dificuldadeSelecionada = dif;
  }

  selectQuantity(qtd: number): void {
    this.quantidadeSelecionada = qtd;
  }

  selectTheme(theme: string): void {
    this.temaInformado = theme;
  }

  clearTheme(): void {
    this.temaInformado = '';
  }

  submitConfig(): void {
    this.generate.emit({
      religiao: this.religiaoSelecionada,
      tema: this.temaInformado.trim() ? this.temaInformado.trim() : undefined,
      dificuldade: this.dificuldadeSelecionada,
      quantidade: this.quantidadeSelecionada
    });
  }
}
