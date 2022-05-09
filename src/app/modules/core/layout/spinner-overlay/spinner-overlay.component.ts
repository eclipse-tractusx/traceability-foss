import { Component } from '@angular/core';
import { Observable } from 'rxjs';
import { SpinnerOverlayService } from './spinner-overlay.service';

@Component({
  selector: 'app-spinner-overlay',
  templateUrl: './spinner-overlay.component.html',
  styleUrls: ['./spinner-overlay.component.scss'],
})
export class SpinnerOverlayComponent {
  public isOverlayLoading$: Observable<boolean>;

  constructor(private spinnerOverlyService: SpinnerOverlayService) {
    this.isOverlayLoading$ = this.spinnerOverlyService.isOverlayShowing$;
  }
}
