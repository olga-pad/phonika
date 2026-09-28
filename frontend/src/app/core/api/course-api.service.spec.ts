import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting, HttpTestingController } from '@angular/common/http/testing';
import { CourseApiService } from './course-api.service';

describe('CourseApiService', () => {
  it('loads RU EN FR courses from backend API', () => {
    TestBed.configureTestingModule({ providers: [provideHttpClient(), provideHttpClientTesting()] });
    const service = TestBed.inject(CourseApiService); const http = TestBed.inject(HttpTestingController);
    service.courses().subscribe(courses => expect(courses.map(c => c.language)).toEqual(['RU','EN','FR']));
    http.expectOne('http://localhost:8080/api/courses').flush([{code:'READING_RU',language:'RU'},{code:'READING_EN',language:'EN'},{code:'READING_FR',language:'FR'}]);
    http.verify();
  });
});
