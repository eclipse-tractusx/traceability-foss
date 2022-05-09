import { Injectable } from '@angular/core';
import { NotificationText } from './notification-message/notification-text';
import { NotificationStatus } from './notification-message/notification-status';
import { NotificationMessage } from './notification-message/notification-message';
import { Observable, Subject } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class NotificationService {
  private notificationSubject = new Subject<NotificationMessage>();
  private idx = 0;

  public getNotificationObservable(): Observable<NotificationMessage> {
    return this.notificationSubject.asObservable();
  }

  public success(message: NotificationText | string, timeout = 3000): void {
    this.notificationSubject.next(new NotificationMessage(this.idx++, message, NotificationStatus.Success, timeout));
  }

  public info(message: NotificationText | string, timeout = 3000): void {
    this.notificationSubject.next(
      new NotificationMessage(this.idx++, message, NotificationStatus.Informative, timeout),
    );
  }

  public error(message: NotificationText | string, timeout = 3000): void {
    this.notificationSubject.next(new NotificationMessage(this.idx++, message, NotificationStatus.Error, timeout));
  }

  public warning(message: NotificationText | string, timeout = 3000): void {
    this.notificationSubject.next(new NotificationMessage(this.idx++, message, NotificationStatus.Warning, timeout));
  }
}
