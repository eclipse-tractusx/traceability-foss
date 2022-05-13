import { Component, ElementRef, EventEmitter, Input, Output, ViewChild } from '@angular/core';

type ButtonVariant = 'button' | 'raised' | 'flat' | 'stroked' | 'icon' | 'fab' | 'mini-fab';

@Component({
  selector: 'app-button',
  templateUrl: './button.component.html',
})
export class ButtonComponent {
  @ViewChild('ButtonElement') buttonElement: ElementRef;
  @Input() color: 'primary' | 'accent' | 'warn' = 'primary';
  @Input() variant: ButtonVariant = 'button';

  @Input() label: string;
  @Input() iconName: string;
  @Input() isDisabled: boolean = false;

  @Output() click: EventEmitter<MouseEvent> = new EventEmitter<MouseEvent>();

  public getClasses(): Record<string, boolean> {
    return { ['mat-' + this.color]: true };
  }
}
