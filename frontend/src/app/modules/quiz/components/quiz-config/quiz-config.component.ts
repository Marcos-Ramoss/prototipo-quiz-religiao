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

  religiaoSelecionada: string = 'Cristianismo';
  dificuldadeSelecionada: string = 'Médio';
  quantidadeSelecionada: number = 5;
  temaInformado: string = '';

  readonly religions: ReligionOption[] = [
    { id: 'Cristianismo', name: 'Cristianismo', icon: '✝️', desc: 'Evangelhos, tradições apostólicas e história bíblica' },
    { id: 'Islamismo', name: 'Islamismo', icon: '☪️', desc: 'Pilares da fé, Alcorão e civilização islâmica' },
    { id: 'Budismo', name: 'Budismo', icon: '☸️', desc: 'Quatro Nobres Verdades, Caminho Óctuplo e compaixão' },
    { id: 'Hinduísmo', name: 'Hinduísmo', icon: '🕉️', desc: 'Vedas, Bhagavad Gita, Dharma e transcendência' },
    { id: 'Judaísmo', name: 'Judaísmo', icon: '✡️', desc: 'Torá, tradições proféticas e história hebraica' },
    { id: 'Espiritismo', name: 'Espiritismo', icon: '🕊️', desc: 'Obras de Allan Kardec, ética e caridade' },
    { id: 'Religiões de matriz africana', name: 'Religiões de matriz africana', icon: '🌊', desc: 'Candomblé, Umbanda, Orixás e ancestralidade' },
    { id: 'Outras religiões', name: 'Outras tradições', icon: '✨', desc: 'Xintoísmo, Taoísmo, Zoroastrismo e filosofia comparada' }
  ];

  readonly difficulties: string[] = ['Fácil', 'Médio', 'Difícil'];
  readonly quantities: number[] = [5, 10, 15];

  readonly suggestedThemesMap: Record<string, string[]> = {
    'Cristianismo': ['Parábolas de Jesus', 'História dos Primeiros Cristãos', 'Reforma e Tradições', 'Sermão da Montanha'],
    'Islamismo': ['Os Cinco Pilares da Fé', 'A Revelação do Alcorão', 'Profetas no Islã', 'Ramadã e Tradições'],
    'Budismo': ['As Quatro Nobres Verdades', 'O Nobre Caminho Óctuplo', 'Vida de Siddhartha Gautama', 'Conceito de Nirvana'],
    'Hinduísmo': ['Ensinamentos do Bhagavad Gita', 'Dharma, Karma e Samsara', 'Principais Divindades e Simbolismo', 'Festivais Tradicionais'],
    'Judaísmo': ['A Torá e os Dez Mandamentos', 'Grandes Festas e Rituais', 'Profetas e Reis de Israel', 'Tradição do Shabat'],
    'Espiritismo': ['O Livro dos Espíritos', 'A Lei de Causa e Efeito', 'Fundamentos de Allan Kardec', 'Prática da Fraternidade'],
    'Religiões de matriz africana': ['Orixás e Arquétipos da Natureza', 'Valores de Ancestralidade', 'Sincretismo Cultural e Histórico', 'Respeito à Terra e aos Elementos'],
    'Outras religiões': ['Taoísmo e a Busca do Equilíbrio', 'Xintoísmo e Reverência aos Kamis', 'Zoroastrismo e a Batalha Moral', 'Mitologia Nórdica e Greco-Romana']
  };

  get currentSuggestedThemes(): string[] {
    return this.suggestedThemesMap[this.religiaoSelecionada] || [];
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

