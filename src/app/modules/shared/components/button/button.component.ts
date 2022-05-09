import { Component, EventEmitter, Input, Output } from '@angular/core';

@Component({
  selector: 'app-button',
  templateUrl: './button.component.html',
  styleUrls: ['./button.component.scss'],
})
export class ButtonComponent {
  @Input() button: string;
  @Input() icon: string;
  @Input() type: 'button' | 'submit' | 'reset';
  @Input() disable: boolean;

  @Output() clickEvent: EventEmitter<MouseEvent> = new EventEmitter<MouseEvent>();
}
