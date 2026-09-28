import { Component } from '@angular/core';
import { CourseSelectorComponent } from './courses/course-selector/course-selector.component';

@Component({ selector: 'app-root', standalone: true, imports: [CourseSelectorComponent], template: '<app-course-selector />' })
export class AppComponent {}
