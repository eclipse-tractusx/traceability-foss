import { Component } from '@angular/core';
import { environment } from '@env';
import * as mockService from '../../../mocks/mock';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss'],
})
export class AppComponent {
  constructor() {
    if (!environment.production) {
      const { worker } = mockService;
      void worker.start();
    }
  }
}
