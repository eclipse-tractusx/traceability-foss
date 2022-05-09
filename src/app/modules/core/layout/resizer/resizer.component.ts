import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-resizer',
  templateUrl: './resizer.component.html',
  styleUrls: ['./resizer.component.scss'],
})
export class ResizerComponent {
  @Input() expanded: boolean;

  get icon(): string {
    return this.expanded ? 'arrow-left-s-fill' : 'arrow-right-s-fill';
  }
}
