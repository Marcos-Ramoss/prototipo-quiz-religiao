import { Component, EventEmitter, Output, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { GenerateQuizPayload, ReligionOption, QuizStats, ActivityItem } from '../../models/quiz.models';
import { QuizService } from '../../services/quiz.service';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-quiz-config',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './quiz-config.component.html',
  styleUrl: './quiz-config.component.css'
})
export class QuizConfigComponent {
  private readonly quizService = inject(QuizService);

  @Output() generate = new EventEmitter<GenerateQuizPayload>();

  readonly stats$: Observable<QuizStats> = this.quizService.stats$;
  readonly activities$: Observable<ActivityItem[]> = this.quizService.activities$;

  religiaoSelecionada: string = 'Judaísmo';
  dificuldadeSelecionada: string = 'Fácil';
  quantidadeSelecionada: number = 10;
  temaInformado: string = '';

  readonly religions: ReligionOption[] = [
    { id: 'Cristianismo', name: 'Cristianismo', icon: '✝', desc: 'Evangelhos, história e tradições', color: '#376F8E', category: 'Abraâmica' },
    { id: 'Islamismo', name: 'Islamismo', icon: '☪', desc: 'Alcorão, história e práticas', color: '#238A83', category: 'Abraâmica' },
    { id: 'Budismo', name: 'Budismo', icon: '☸', desc: 'Ensinamentos e filosofia', color: '#BE8650', category: 'Dhármica' },
    { id: 'Hinduísmo', name: 'Hinduísmo', icon: 'ॐ', desc: 'Textos, filosofias e práticas', color: '#C77561', category: 'Dhármica' },
    { id: 'Judaísmo', name: 'Judaísmo', icon: '✡', desc: 'Torá, costumes e história', color: '#667CB4', category: 'Abraâmica' },
    { id: 'Espiritismo', name: 'Espiritismo', icon: '✦', desc: 'Obras, filosofia e ensinamentos', color: '#4B8F80', category: 'Filosófica' },
    { id: 'Religiões de matriz africana', name: 'Matriz Africana', icon: '❋', desc: 'Ancestralidade e diversidade', color: '#A16B52', category: 'Tradicionais' },
    { id: 'Outras tradições', name: 'Outras tradições', icon: '☬', desc: 'Sikhismo e tradições do mundo', color: '#A17A2F', category: 'Outras' }
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

  selecionarReligiao(id: string): void {
    this.religiaoSelecionada = id;
  }

  selecionarDificuldade(dif: string): void {
    this.dificuldadeSelecionada = dif;
  }

  selecionarQuantidade(qtd: number): void {
    this.quantidadeSelecionada = qtd;
  }

  selecionarTema(tema: string): void {
    this.temaInformado = tema;
  }

  limparFormulario(): void {
    this.religiaoSelecionada = 'Judaísmo';
    this.dificuldadeSelecionada = 'Fácil';
    this.quantidadeSelecionada = 10;
    this.temaInformado = '';
  }

  enviarConfiguracao(): void {
    this.generate.emit({
      religiao: this.religiaoSelecionada,
      tema: this.temaInformado.trim() ? this.temaInformado.trim() : undefined,
      dificuldade: this.dificuldadeSelecionada,
      quantidade: this.quantidadeSelecionada
    });
  }

  // Aliases semânticos para compatibilidade
  selectReligion = (id: string) => this.selecionarReligiao(id);
  selectDifficulty = (d: string) => this.selecionarDificuldade(d);
  selectQuantity = (q: number) => this.selecionarQuantidade(q);
  selectTheme = (t: string) => this.selecionarTema(t);
  resetForm = () => this.limparFormulario();
  submitConfig = () => this.enviarConfiguracao();
}
