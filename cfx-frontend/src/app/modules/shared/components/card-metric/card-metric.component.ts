import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-card-metric',
  templateUrl: './card-metric.component.html',
  styleUrls: [ './card-metric.component.scss' ],
})
export class CardMetricComponent {
  @Input() headerLabelKey: string;
  @Input() value: number;
  @Input() footerLabelKey: string;

}
