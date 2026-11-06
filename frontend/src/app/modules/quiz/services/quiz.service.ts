import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, catchError, of, tap } from 'rxjs';
import { environment } from '../../../../environments/environment';
import {
  GenerateQuizPayload,
  QuizSession,
  SubmitQuizPayload,
  QuizResult,
  QuizStats,
  ActivityItem,
  QuizSummary,
  PublishQuizPayload
} from '../models/quiz.models';

const STATS_STORAGE_KEY = 'convivere_quiz_stats';
const ACTIVITIES_STORAGE_KEY = 'convivere_activities';

@Injectable({
  providedIn: 'root'
})
export class QuizService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = environment.apiUrl;

  private readonly estatisticasIniciais: QuizStats = {
    quizzesCriados: 12,
    perguntasDisponiveis: 3842,
    tradicoesCadastradas: 8,
    participacoesRealizadas: 5280
  };

  private readonly estatisticasSubject = new BehaviorSubject<QuizStats>(this.carregarEstatisticasArmazenadas());
  readonly stats$ = this.estatisticasSubject.asObservable();

  private readonly atividadesSubject = new BehaviorSubject<ActivityItem[]>(this.carregarAtividadesArmazenadas());
  readonly activities$ = this.atividadesSubject.asObservable();

  constructor() {
    this.atualizarEstatisticas();
  }

  obterEstatisticas(): Observable<QuizStats> {
    return this.http.get<QuizStats>(`${this.apiUrl}/stats`).pipe(
      tap(stats => {
        this.salvarEstatisticas(stats);
      }),
      catchError(() => of(this.estatisticasSubject.value))
    );
  }

  atualizarEstatisticas(): void {
    this.obterEstatisticas().subscribe();
  }

  gerarQuiz(payload: GenerateQuizPayload): Observable<QuizSession> {
    return this.http.post<QuizSession>(`${this.apiUrl}/generate`, payload).pipe(
      tap(() => {
        this.incrementarQuizzesCriados(payload);
      })
    );
  }

  submeterQuiz(payload: SubmitQuizPayload): Observable<QuizResult> {
    return this.http.post<QuizResult>(`${this.apiUrl}/submit`, payload).pipe(
      tap(resultado => {
        this.incrementarParticipacoes(resultado);
      })
    );
  }

  listarPublicados(): Observable<QuizSummary[]> {
    return this.http.get<QuizSummary[]>(`${this.apiUrl}/publicados`).pipe(
      catchError(() => of([]))
    );
  }

  listarTodosAdmin(): Observable<QuizSummary[]> {
    return this.http.get<QuizSummary[]>(`${this.apiUrl}/admin/todos`).pipe(
      catchError(() => of([]))
    );
  }

  obterQuizPorId(id: string): Observable<QuizSession> {
    return this.http.get<QuizSession>(`${this.apiUrl}/${id}`);
  }

  publicarQuiz(id: string, payload?: PublishQuizPayload): Observable<QuizSummary> {
    return this.http.put<QuizSummary>(`${this.apiUrl}/${id}/publicar`, payload || {}).pipe(
      tap(() => this.atualizarEstatisticas())
    );
  }

  excluirQuiz(id: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`).pipe(
      tap(() => this.atualizarEstatisticas())
    );
  }

  // Aliases de conveniência
  getPublicQuizzes(): Observable<QuizSummary[]> {
    return this.listarPublicados();
  }

  getAdminQuizzes(): Observable<QuizSummary[]> {
    return this.listarTodosAdmin();
  }

  getQuizById(id: string): Observable<QuizSession> {
    return this.obterQuizPorId(id);
  }

  publishQuiz(id: string, payload?: PublishQuizPayload): Observable<QuizSummary> {
    return this.publicarQuiz(id, payload);
  }

  deleteQuiz(id: string): Observable<void> {
    return this.excluirQuiz(id);
  }

  // Aliases de conveniência
  generateQuiz(payload: GenerateQuizPayload): Observable<QuizSession> {
    return this.gerarQuiz(payload);
  }

  submitQuiz(payload: SubmitQuizPayload): Observable<QuizResult> {
    return this.submeterQuiz(payload);
  }

  getStats(): Observable<QuizStats> {
    return this.obterEstatisticas();
  }

  private incrementarQuizzesCriados(payload: GenerateQuizPayload): void {
    const atual = this.estatisticasSubject.value;
    const atualizado: QuizStats = {
      ...atual,
      quizzesCriados: atual.quizzesCriados + 1,
      perguntasDisponiveis: atual.perguntasDisponiveis + (payload.quantidade || 5)
    };
    this.salvarEstatisticas(atualizado);

    this.adicionarAtividade({
      id: Date.now().toString(),
      type: 'quiz_created',
      title: `Quiz "${payload.religiao} - Nível ${payload.dificuldade}" criado`,
      subtitle: payload.tema ? `Tema: ${payload.tema}` : 'Conteúdo gerado com IA',
      timeAgo: 'Agora mesmo',
      icon: '🏆',
      iconBgClass: 'badge-amber'
    });
  }

  private incrementarParticipacoes(resultado: QuizResult): void {
    const atual = this.estatisticasSubject.value;
    const atualizado: QuizStats = {
      ...atual,
      participacoesRealizadas: atual.participacoesRealizadas + 1
    };
    this.salvarEstatisticas(atualizado);

    this.adicionarAtividade({
      id: Date.now().toString(),
      type: 'quiz_completed',
      title: `Quiz concluído com ${resultado.aproveitamento}% de acerto`,
      subtitle: `${resultado.acertos} de ${resultado.totalQuestoes} questões corretas`,
      timeAgo: 'Agora mesmo',
      icon: '🎓',
      iconBgClass: 'badge-mint'
    });
  }

  private adicionarAtividade(item: ActivityItem): void {
    const atual = this.atividadesSubject.value;
    const atualizado = [item, ...atual.slice(0, 9)];
    this.atividadesSubject.next(atualizado);
    this.salvarAtividadesArmazenadas(atualizado);
  }

  private carregarEstatisticasArmazenadas(): QuizStats {
    if (typeof localStorage === 'undefined') return this.estatisticasIniciais;
    try {
      const bruto = localStorage.getItem(STATS_STORAGE_KEY);
      return bruto ? JSON.parse(bruto) : this.estatisticasIniciais;
    } catch {
      return this.estatisticasIniciais;
    }
  }

  private salvarEstatisticas(stats: QuizStats): void {
    this.estatisticasSubject.next(stats);
    if (typeof localStorage !== 'undefined') {
      try {
        localStorage.setItem(STATS_STORAGE_KEY, JSON.stringify(stats));
      } catch {}
    }
  }

  private carregarAtividadesArmazenadas(): ActivityItem[] {
    const atividadesPadrao: ActivityItem[] = [
      {
        id: 'act-1',
        type: 'quiz_created',
        title: 'Quiz "Judaísmo - Nível Fácil" criado',
        subtitle: 'Festas e costumes bíblicos',
        timeAgo: 'Há 2 horas',
        icon: '🏆',
        iconBgClass: 'badge-amber'
      },
      {
        id: 'act-2',
        type: 'questions_added',
        title: '18 novas perguntas adicionadas',
        subtitle: 'Curadoria de diálogo inter-religioso',
        timeAgo: 'Há 5 horas',
        icon: '👥',
        iconBgClass: 'badge-teal'
      },
      {
        id: 'act-3',
        type: 'tradition_updated',
        title: 'Tradição "Matriz Africana" atualizada',
        subtitle: 'História, filosofia e ancestralidade',
        timeAgo: 'Ontem às 16:40',
        icon: '📖',
        iconBgClass: 'badge-green'
      }
    ];

    if (typeof localStorage === 'undefined') return atividadesPadrao;
    try {
      const bruto = localStorage.getItem(ACTIVITIES_STORAGE_KEY);
      return bruto ? JSON.parse(bruto) : atividadesPadrao;
    } catch {
      return atividadesPadrao;
    }
  }

  private salvarAtividadesArmazenadas(atividades: ActivityItem[]): void {
    if (typeof localStorage !== 'undefined') {
      try {
        localStorage.setItem(ACTIVITIES_STORAGE_KEY, JSON.stringify(atividades));
      } catch {}
    }
  }
}
