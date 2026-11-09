import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { QuizService } from './modules/quiz/services/quiz.service';
import {
  GenerateQuizPayload,
  QuizResult,
  QuizSession,
  SubmitQuizPayload
} from './modules/quiz/models/quiz.models';
import { QuizConfigComponent } from './modules/quiz/components/quiz-config/quiz-config.component';
import { QuizSkeletonComponent } from './modules/quiz/components/quiz-skeleton/quiz-skeleton.component';
import { QuizRunnerComponent } from './modules/quiz/components/quiz-runner/quiz-runner.component';
import { QuizResultComponent } from './modules/quiz/components/quiz-result/quiz-result.component';

type EstadoVisualizacao = 'config' | 'loading' | 'quiz' | 'submitting' | 'result';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [
    CommonModule,
    QuizConfigComponent,
    QuizSkeletonComponent,
    QuizRunnerComponent,
    QuizResultComponent
  ],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {
  private readonly quizService = inject(QuizService);

  viewState: EstadoVisualizacao = 'config';
  errorMessage: string | null = null;
  loadingMessage: string = '';

  lastConfigPayload: GenerateQuizPayload | null = null;
  quizSession: QuizSession | null = null;
  quizResult: QuizResult | null = null;
  currentQuizId: string | null = null;

  isDrawerOpen: boolean = false;
  isSidebarCollapsed: boolean = false;
  toastMessage: string | null = null;
  infoModalOpen: boolean = false;
  private toastTimer: any = null;

  alternarGaveta(): void {
    this.isDrawerOpen = !this.isDrawerOpen;
  }

  fecharGaveta(): void {
    this.isDrawerOpen = false;
  }

  alternarColapsoBarraLateral(): void {
    this.isSidebarCollapsed = !this.isSidebarCollapsed;
  }

  alternarMenu(): void {
    if (typeof window !== 'undefined' && window.innerWidth <= 820) {
      this.alternarGaveta();
    } else {
      this.alternarColapsoBarraLateral();
    }
  }

  aoNavegar(modulo: string): void {
    this.fecharGaveta();
    if (modulo === 'quiz' || modulo === 'home') {
      this.aoReiniciarConfig();
      return;
    }
    const nomesModulos: Record<string, string> = {
      library: 'Enciclopédia Religiosa',
      compare: 'Comparador de Religiões',
      timeline: 'Linha do Tempo',
      courses: 'Cursos e Formações',
      simulations: 'Simulações e Role-play',
      certificates: 'Certificados',
      events: 'Agenda de Eventos',
      forum: 'Espaço de Diálogo',
      corporate: 'Painel Corporativo',
      team: 'Colaboradores',
      admin: 'Administração',
      profile: 'O Meu Perfil',
      help: 'Sobre o Projeto'
    };
    const nome = nomesModulos[modulo] || modulo;
    this.exibirToast(`O módulo "${nome}" será implementado na próxima etapa. Estamos focados no Quiz com IA!`);
  }

  exibirToast(mensagem: string): void {
    this.toastMessage = mensagem;
    if (this.toastTimer) {
      clearTimeout(this.toastTimer);
    }
    this.toastTimer = setTimeout(() => {
      this.toastMessage = null;
    }, 3500);
  }

  abrirModalInfo(): void {
    this.infoModalOpen = true;
  }

  fecharModalInfo(): void {
    this.infoModalOpen = false;
  }

  fecharErro(): void {
    this.errorMessage = null;
  }

  aoGerarQuiz(payload: GenerateQuizPayload): void {
    this.lastConfigPayload = payload;
    this.errorMessage = null;
    this.viewState = 'loading';
    this.loadingMessage = `Conectando à IA para formular ${payload.quantidade} questões de ${payload.religiao}...`;

    this.quizService.gerarQuiz(payload).subscribe({
      next: (session) => {
        this.quizSession = session;
        this.currentQuizId = session.quizId;
        this.quizResult = null;
        this.viewState = 'quiz';
      },
      error: (err) => {
        this.viewState = 'config';
        const serverMsg = err?.error?.message || err?.message || 'Falha ao conectar com o serviço de IA.';
        this.errorMessage = `Não foi possível gerar o quiz: ${serverMsg}`;
      }
    });
  }

  aoJogarQuizExistente(quizId: string): void {
    this.errorMessage = null;
    this.viewState = 'loading';
    this.loadingMessage = 'Carregando o quiz do banco de dados...';

    this.quizService.obterQuizPorId(quizId).subscribe({
      next: (session) => {
        this.quizSession = session;
        this.currentQuizId = session.quizId;
        this.quizResult = null;
        this.viewState = 'quiz';
      },
      error: (err) => {
        this.viewState = 'config';
        const serverMsg = err?.error?.message || err?.message || 'Falha ao carregar o quiz.';
        this.errorMessage = `Não foi possível carregar o quiz: ${serverMsg}`;
      }
    });
  }

  aoSubmeterQuiz(respostasUsuario: Record<string, string>): void {
    if (!this.quizSession) return;

    this.errorMessage = null;
    this.viewState = 'submitting';
    this.loadingMessage = 'Enviando respostas e calculando pontuação com base no gabarito oficial...';

    const respostasPayload = this.quizSession.questoes.map(q => ({
      perguntaId: q.id,
      respostaSelecionadaId: respostasUsuario[q.id] || ''
    }));

    const payload: SubmitQuizPayload = {
      quizId: this.quizSession.quizId,
      respostas: respostasPayload
    };

    this.quizService.submeterQuiz(payload).subscribe({
      next: (result) => {
        this.quizResult = result;
        this.viewState = 'result';
      },
      error: (err) => {
        this.viewState = 'quiz';
        const serverMsg = err?.error?.message || err?.message || 'Falha ao submeter o quiz.';
        this.errorMessage = `Erro na correção do quiz: ${serverMsg}`;
      }
    });
  }

  aoReiniciarConfig(): void {
    this.viewState = 'config';
    this.quizSession = null;
    this.quizResult = null;
    this.errorMessage = null;
  }

  aoTentarNovamenteMesmaConfig(): void {
    if (this.lastConfigPayload) {
      this.aoGerarQuiz(this.lastConfigPayload);
    } else {
      this.aoReiniciarConfig();
    }
  }

  // Aliases para compatibilidade
  toggleDrawer = () => this.alternarGaveta();
  closeDrawer = () => this.fecharGaveta();
  toggleSidebarCollapse = () => this.alternarColapsoBarraLateral();
  onToggleMenu = () => this.alternarMenu();
  onNavigate = (m: string) => this.aoNavegar(m);
  showToast = (m: string) => this.exibirToast(m);
  openInfoModal = () => this.abrirModalInfo();
  closeInfoModal = () => this.fecharModalInfo();
  dismissError = () => this.fecharErro();
  onGenerateQuiz = (p: GenerateQuizPayload) => this.aoGerarQuiz(p);
  onSubmitQuiz = (r: Record<string, string>) => this.aoSubmeterQuiz(r);
  onRestartConfig = () => this.aoReiniciarConfig();
  onRetrySameConfig = () => this.aoTentarNovamenteMesmaConfig();
}
