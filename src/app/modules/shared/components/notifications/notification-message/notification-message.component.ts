import { Component, EventEmitter, Input, OnChanges, Output } from '@angular/core';
import { NotificationMessage } from './notification-message';
import { NotificationStatus } from './notification-status';

@Component({
  selector: 'app-notification-message',
  templateUrl: './notification-message.component.html',
  styleUrls: ['./notification-message.component.scss'],
})
export class NotificationMessageComponent implements OnChanges {
  @Input() notifierMessage: NotificationMessage;
  @Output() removeNotification: EventEmitter<NotificationMessage> = new EventEmitter<NotificationMessage>();

  public statusBarCss: string;

  ngOnChanges(): void {
    this.changeStatusBarCss();
  }

  public remove(): void {
    this.removeNotification.emit(this.notifierMessage);
  }

  private changeStatusBarCss(): void {
    const cssMapping = new Map([
      [NotificationStatus.Success, 'status-bar-success'],
      [NotificationStatus.Warning, 'status-bar-warning'],
      [NotificationStatus.Error, 'status-bar-error'],
      [NotificationStatus.Informative, 'status-bar-informative'],
    ]);

    this.statusBarCss = cssMapping.get(this.notifierMessage.status);
  }
}
