import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-quality-alert-empty-state',
  templateUrl: './quality-alert-empty-state.component.html',
  styleUrls: ['./quality-alert-empty-state.component.scss'],
})
export class QualityAlertEmptyStateComponent {
  @Input() title: string;
  @Input() message: string;
  @Input() imageUrl: string;
}
