import { NotificationStatus } from './notification-status';
import { NotificationText } from './notification-text';

export class NotificationMessage {
  public id: number;
  public isSliderON: boolean;
  public message: NotificationText | string;
  public status: NotificationStatus;
  public timeout: number;

  constructor(id: number, message: NotificationText | string, status: NotificationStatus | null, timeout: number) {
    this.id = id;
    this.message = message;
    this.status = status;
    this.isSliderON = true;
    this.timeout = timeout;
  }
}
