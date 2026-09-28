import { Component, computed, inject, signal } from '@angular/core';
import { CourseApiService, CourseDto } from '../../core/api/course-api.service';

@Component({
  selector: 'app-course-selector',
  standalone: true,
  template: `<section><h1>Phonika</h1><p>Choose learning course</p><div class="courses">@for (course of courses(); track course.code) {<button type="button" [class.active]="activeCourse()?.code === course.code" (click)="select(course)">{{ course.language }}</button>}</div><p class="selection">Learning course: {{ activeCourse()?.code ?? 'not selected' }}</p><p class="ui-language">Interface language: unchanged</p></section>`,
  styles: [`section{max-width:40rem;margin:4rem auto;padding:1.5rem;text-align:center}.courses{display:flex;justify-content:center;gap:.75rem}button{min-width:4.5rem;padding:.75rem 1rem;border:1px solid #bbb;border-radius:.6rem;background:white;cursor:pointer}.active{border-color:#222;font-weight:700}.selection,.ui-language{margin-top:1.25rem}.ui-language{color:#666;font-size:.9rem}`]
})
export class CourseSelectorComponent {
  private readonly api = inject(CourseApiService);
  readonly courses = signal<CourseDto[]>([]);
  readonly activeCourse = signal<CourseDto | null>(null);
  constructor() { this.api.courses().subscribe(courses => this.courses.set(courses)); }
  select(course: CourseDto): void { this.activeCourse.set(course); }
}
