import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-avatar',
  templateUrl: './avatar.component.html',
  styleUrls: ['./avatar.component.scss'],
})
export class AvatarComponent {
  @Input() size: string;

  @Input() initialsTopSize: string;

  @Input() name = '';

  @Input() color: string;

  @Input() imageUrl: string;

  public circleStyle(): {
    'background-color': string;
    height: string;
    width: string;
  } {
    return {
      'background-color': this.color,
      height: this.size,
      width: this.size,
    };
  }

  public spanStyle(): {
    'font-size': string;
    'line-height': string;
    top: string;
  } {
    return {
      'font-size': `calc(${this.size} / 2)`,
      'line-height': `calc(${this.size} / 2)`,
      top: this.initialsTopSize,
    };
  }
}
