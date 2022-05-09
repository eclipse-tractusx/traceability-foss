import { Component } from '@angular/core';
import { environment } from '../environments/environment';
import * as mockService from '../mocks/browser';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss'],
})
export class AppComponent {
  constructor() {
    if (!environment.production) {
      const { worker } = mockService;
      worker.start();
    }
  }
}
