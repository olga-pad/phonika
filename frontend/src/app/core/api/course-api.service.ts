import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface CourseDto { code: string; language: 'RU' | 'EN' | 'FR'; }

@Injectable({ providedIn: 'root' })
export class CourseApiService {
  private readonly http = inject(HttpClient);
  courses(): Observable<CourseDto[]> { return this.http.get<CourseDto[]>('http://localhost:8080/api/courses'); }
}
