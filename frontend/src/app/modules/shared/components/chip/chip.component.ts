import { Component, Input } from '@angular/core';

@Component({
  standalone: false,
  selector: 'app-chip',
  templateUrl: './chip.component.html',
  styleUrls: ['./chip.component.scss']
})
export class ChipComponent {
  @Input() chipTextContent: string = "NOT_PROVIDED";

}
