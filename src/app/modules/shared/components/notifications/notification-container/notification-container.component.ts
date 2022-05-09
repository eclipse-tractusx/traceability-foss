import { Component, OnDestroy, OnInit } from '@angular/core';
import { Subscription } from 'rxjs';
import { NotificationMessage } from '../notification-message/notification-message';
import { NotificationService } from '../notification.service';
import { notifyAnimation } from './animation';

@Component({
  selector: 'app-notification-container',
  templateUrl: './notification-container.component.html',
  styleUrls: ['./notification-container.component.scss'],
  animations: [notifyAnimation],
})
export class NotificationContainerComponent implements OnInit, OnDestroy {
  public notifications: NotificationMessage[] = [];

  private subscription: Subscription;

  constructor(private notifierService: NotificationService) {}

  ngOnInit(): void {
    this.subscription = this.notifierService
      .getNotificationObservable()
      .subscribe(notification => this.add(notification));
  }

  ngOnDestroy(): void {
    this.subscription.unsubscribe();
  }

  public remove({ id }: NotificationMessage): void {
    this.notifications = this.notifications.filter(notification => notification.id !== id);
  }

  public add(notification: NotificationMessage): void {
    this.notifications.unshift(notification);

    if (notification.timeout !== 0) {
      setTimeout(() => this.remove(notification), notification.timeout);
    }
  }
}
