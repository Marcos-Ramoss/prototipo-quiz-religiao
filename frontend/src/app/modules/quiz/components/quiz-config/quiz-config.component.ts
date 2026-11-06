import { Component, EventEmitter, OnInit, Output, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import {
  GenerateQuizPayload,
  ReligionOption,
  QuizStats,
  ActivityItem,
  QuizSummary
} from '../../models/quiz.models';
import { QuizService } from '../../services/quiz.service';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-quiz-config',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './quiz-config.component.html',
  styleUrl: './quiz-config.component.css'
})
export class QuizConfigComponent implements OnInit {
  private readonly quizService = inject(QuizService);

  @Output() generate = new EventEmitter<GenerateQuizPayload & { titulo?: string; tempoLimiteSegundos?: number; status?: string }>();
  @Output() playQuiz = new EventEmitter<string>();

  readonly stats$: Observable<QuizStats> = this.quizService.stats$;
  readonly activities$: Observable<ActivityItem[]> = this.quizService.activities$;

  // Controle de Perfil: USER (Aluno) | ADMIN (Educador/Curador)
  userRole: 'USER' | 'ADMIN' = 'USER';
  adminTab: 'generator' | 'manage' = 'generator';

  // Autenticação de Administrador
  isAdminLoggedIn: boolean = false;
  adminLoginEmail: string = 'admin@convivere.org';
  adminLoginPassword: string = '';
  showPassword: boolean = false;
  lembrarDispositivo: boolean = true;
  loginErrorMessage: string | null = null;
  adminLoggedName: string = 'Educador / Coordenador';

  // Catálogo de Quizzes
  publishedQuizzes: QuizSummary[] = [];
  adminQuizzes: QuizSummary[] = [];
  isLoadingQuizzes: boolean = false;
  filtroReligiaoAluno: string = 'TODAS';

  // Campos de Criação do Admin
  religiaoSelecionada: string = 'Judaísmo';
  dificuldadeSelecionada: string = 'Fácil';
  quantidadeSelecionada: number = 10;
  temaInformado: string = '';
  tituloPersonalizado: string = '';
  tempoLimiteMinutos: number = 0; // 0 = Sem limite
  statusInicial: 'PUBLICADO' | 'RASCUNHO' = 'PUBLICADO';

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
  readonly timeLimitOptions: { label: string; minutes: number }[] = [
    { label: 'Sem limite', minutes: 0 },
    { label: '5 min', minutes: 5 },
    { label: '10 min', minutes: 10 },
    { label: '15 min', minutes: 15 },
    { label: '20 min', minutes: 20 }
  ];

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

  ngOnInit(): void {
    if (typeof window !== 'undefined') {
      const sessaoSalva = localStorage.getItem('convivere_admin_session');
      if (sessaoSalva === 'true') {
        this.isAdminLoggedIn = true;
      }
    }
    this.carregarQuizzesPublicados();
  }

  get currentSuggestedThemes(): string[] {
    return this.suggestedThemesMap[this.religiaoSelecionada] || [];
  }

  get selectedReligionName(): string {
    const found = this.religions.find(r => r.id === this.religiaoSelecionada);
    return found ? found.name : this.religiaoSelecionada;
  }

  get quizzesFiltradosAluno(): QuizSummary[] {
    if (this.filtroReligiaoAluno === 'TODAS') {
      return this.publishedQuizzes;
    }
    return this.publishedQuizzes.filter(q => q.religiao === this.filtroReligiaoAluno);
  }

  alternarPerfil(novoPerfil: 'USER' | 'ADMIN'): void {
    this.userRole = novoPerfil;
    this.loginErrorMessage = null;
    if (novoPerfil === 'ADMIN') {
      if (this.isAdminLoggedIn) {
        this.carregarQuizzesAdmin();
      }
    } else {
      this.carregarQuizzesPublicados();
    }
  }

  alternarVisibilidadeSenha(): void {
    this.showPassword = !this.showPassword;
  }

  voltarParaAluno(): void {
    this.alternarPerfil('USER');
  }

  executarLoginAdmin(): void {
    const emailLimpo = (this.adminLoginEmail || '').trim().toLowerCase();
    const senha = (this.adminLoginPassword || '').trim();

    // Validação de credenciais de demonstração (ou qualquer email admin com senha padrão)
    if ((emailLimpo.includes('admin') || emailLimpo === 'admin@convivere.org') &&
        (senha === 'admin123' || senha === 'admin' || senha === '123456')) {
      this.isAdminLoggedIn = true;
      if (typeof window !== 'undefined' && this.lembrarDispositivo) {
        localStorage.setItem('convivere_admin_session', 'true');
      }
      this.loginErrorMessage = null;
      this.userRole = 'ADMIN';
      this.adminTab = 'generator';
      this.carregarQuizzesAdmin();
    } else {
      this.loginErrorMessage = 'Credenciais incorretas. Dica de demonstração: admin@convivere.org com senha "admin123".';
    }
  }

  deslogarAdmin(): void {
    this.isAdminLoggedIn = false;
    if (typeof window !== 'undefined') {
      localStorage.removeItem('convivere_admin_session');
    }
    this.userRole = 'USER';
    this.carregarQuizzesPublicados();
  }

  carregarQuizzesPublicados(): void {
    this.isLoadingQuizzes = true;
    this.quizService.listarPublicados().subscribe({
      next: (lista) => {
        this.publishedQuizzes = lista || [];
        this.isLoadingQuizzes = false;
      },
      error: () => {
        this.publishedQuizzes = [];
        this.isLoadingQuizzes = false;
      }
    });
  }

  carregarQuizzesAdmin(): void {
    this.isLoadingQuizzes = true;
    this.quizService.listarTodosAdmin().subscribe({
      next: (lista) => {
        this.adminQuizzes = lista || [];
        this.isLoadingQuizzes = false;
      },
      error: () => {
        this.adminQuizzes = [];
        this.isLoadingQuizzes = false;
      }
    });
  }

  iniciarQuiz(quizId: string): void {
    this.playQuiz.emit(quizId);
  }

  publicarQuizRascunho(quiz: QuizSummary): void {
    this.quizService.publicarQuiz(quiz.id, {
      titulo: quiz.titulo,
      tempoLimiteSegundos: quiz.tempoLimiteSegundos
    }).subscribe({
      next: () => {
        this.carregarQuizzesAdmin();
        this.carregarQuizzesPublicados();
      }
    });
  }

  excluirQuiz(quizId: string): void {
    if (confirm('Tem certeza que deseja excluir este quiz permanentemente?')) {
      this.quizService.excluirQuiz(quizId).subscribe({
        next: () => {
          this.carregarQuizzesAdmin();
          this.carregarQuizzesPublicados();
        }
      });
    }
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

  selecionarTempoLimite(minutos: number): void {
    this.tempoLimiteMinutos = minutos;
  }

  selecionarTema(tema: string): void {
    this.temaInformado = tema;
  }

  limparFormulario(): void {
    this.religiaoSelecionada = 'Judaísmo';
    this.dificuldadeSelecionada = 'Fácil';
    this.quantidadeSelecionada = 10;
    this.temaInformado = '';
    this.tituloPersonalizado = '';
    this.tempoLimiteMinutos = 0;
    this.statusInicial = 'PUBLICADO';
  }

  enviarConfiguracao(): void {
    this.generate.emit({
      religiao: this.religiaoSelecionada,
      tema: this.temaInformado.trim() ? this.temaInformado.trim() : undefined,
      dificuldade: this.dificuldadeSelecionada,
      quantidade: this.quantidadeSelecionada,
      titulo: this.tituloPersonalizado.trim() ? this.tituloPersonalizado.trim() : undefined,
      tempoLimiteSegundos: this.tempoLimiteMinutos * 60,
      status: this.statusInicial
    });
  }

  obterIconeReligiao(religiao: string): string {
    const found = this.religions.find(r => r.id.toLowerCase() === religiao?.toLowerCase() || r.name.toLowerCase() === religiao?.toLowerCase());
    return found ? found.icon : '✧';
  }

  obterCorReligiao(religiao: string): string {
    const found = this.religions.find(r => r.id.toLowerCase() === religiao?.toLowerCase() || r.name.toLowerCase() === religiao?.toLowerCase());
    return found?.color ? found.color : '#168c73';
  }

  formatarTempo(segundos: number): string {
    if (!segundos || segundos <= 0) return 'Livre';
    const min = Math.round(segundos / 60);
    return `${min} min`;
  }

  // Aliases semânticos para compatibilidade
  selectReligion = (id: string) => this.selecionarReligiao(id);
  selectDifficulty = (d: string) => this.selecionarDificuldade(d);
  selectQuantity = (q: number) => this.selecionarQuantidade(q);
  selectTheme = (t: string) => this.selecionarTema(t);
  resetForm = () => this.limparFormulario();
  submitConfig = () => this.enviarConfiguracao();
}
