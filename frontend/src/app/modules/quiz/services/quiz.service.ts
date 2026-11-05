import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import {
  GenerateQuizPayload,
  QuizSession,
  SubmitQuizPayload,
  QuizResult
} from '../models/quiz.models';

@Injectable({
  providedIn: 'root'
})
export class QuizService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = environment.apiUrl;

  generateQuiz(payload: GenerateQuizPayload): Observable<QuizSession> {
    return this.http.post<QuizSession>(`${this.apiUrl}/generate`, payload);
  }

  submitQuiz(payload: SubmitQuizPayload): Observable<QuizResult> {
    return this.http.post<QuizResult>(`${this.apiUrl}/submit`, payload);
  }
}

