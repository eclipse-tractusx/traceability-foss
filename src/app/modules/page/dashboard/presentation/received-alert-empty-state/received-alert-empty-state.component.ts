import { Component } from '@angular/core';

@Component({
  selector: 'app-received-alert-empty-state',
  templateUrl: './received-alert-empty-state.component.html',
  styleUrls: ['./received-alert-empty-state.component.scss'],
})
export class ReceivedAlertEmptyStateComponent {
  public listEmptyState = new Array(5).fill(null);
}
