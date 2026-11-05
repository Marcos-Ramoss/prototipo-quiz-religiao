import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-quiz-skeleton',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './quiz-skeleton.component.html',
  styleUrl: './quiz-skeleton.component.css'
})
export class QuizSkeletonComponent {
  @Input() message: string = 'Consultando a Inteligência Artificial...';
}

