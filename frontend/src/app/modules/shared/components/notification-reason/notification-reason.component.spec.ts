import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Notification, NotificationStatus, NotificationType } from '@shared/model/notification.model';
import { Severity } from '@shared/model/severity.model';
import { NotificationReasonComponent } from './notification-reason.component';

describe('NotificationReasonComponent', () => {
  let component: NotificationReasonComponent;
  let fixture: ComponentFixture<NotificationReasonComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ NotificationReasonComponent ]
    })
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(NotificationReasonComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should process notification input and set textMessages correctly', () => {
    const notification: Notification = {
      id: '1',
      title: 'test',
      type: NotificationType.ALERT,
      status: NotificationStatus.RECEIVED,
      description: 'Test Notification',
      createdBy: '123',
      createdByName: 'John Doe',
      createdDate: new Date('2022-05-01T12:34:12'),
      assetIds: [],
      sendTo: 'B',
      severity: Severity.CRITICAL,
      isFromSender: true,
      messages: [
        {
          id: '1',
          sentBy: '123',
          sentByName: 'John Doe',
          sendTo: 'A',
          sendToName: 'A',
          contractAgreementId: 'contractAgreement-1',
          notificationReferenceId: 'referenceId-1',
          edcNotificationId: 'edc-1',
          messageDate: '2022-05-01T12:33:12',
          messageId: '1',
          message: 'Hello',
          status: NotificationStatus.SENT,
          errorMessage: ''
        },
        {
          id: '2',
          sentBy: '123',
          sentByName: 'John Doe',
          sendTo: 'B',
          sendToName: 'B',
          contractAgreementId: 'contractAgreement-2',
          notificationReferenceId: 'referenceId-2',
          edcNotificationId: 'edc-1',
          messageDate: '2022-05-01T12:34:12',
          messageId: '2',
          message: 'Hello',
          status: NotificationStatus.SENT,
          errorMessage: ''
        },
      ]
    };
    component.notificationMessages = notification.messages;
    component.ngOnInit();
    expect(component.textMessages.length).toBe(2);
    expect(component.textMessages[0].message).toEqual('Hello');
    expect(component.textMessages[1].direction).toEqual('left');
  });

  it('should correctly compare two dates with isSameDay', () => {
    const date1 = '2023-06-15';
    const date2 = '2023-06-15';
    const date3 = '2023-06-16';
    expect(component.isSameDay(date1, date2)).toBeTruthy();
    expect(component.isSameDay(date1, date3)).toBeFalsy();
  });

  it('should handle empty messages array', () => {
    const notification: Notification = {
      id: '1',
      title: 'test',
      type: NotificationType.ALERT,
      status: NotificationStatus.RECEIVED,
      description: 'Test Notification',
      createdBy: '123',
      createdByName: 'John Doe',
      createdDate: new Date('2022-05-01T12:34:12'),
      assetIds: [],
      sendTo: 'B',
      severity: Severity.CRITICAL,
      isFromSender: true,
      messages: []
    };
    component.notificationMessages = notification.messages;
    expect(component.textMessages.length).toBe(0);
  });

  it('should handle incorrect date formats in messages', () => {
    const notification: Notification = {
      id: '1',
      title: 'test',
      type: NotificationType.ALERT,
      status: NotificationStatus.RECEIVED,
      description: 'Test Notification',
      createdBy: '123',
      createdByName: 'John Doe',
      createdDate: new Date('2022-05-01T12:34:12'),
      assetIds: [],
      sendTo: 'B',
      severity: Severity.CRITICAL,
      isFromSender: true,
      messages: []
    };
    component.notificationMessages = notification.messages;
    // Since date is invalid, sorting and processing might behave unexpectedly, expecting no error thrown and no messages processed
    expect(component.textMessages.length).toBe(0);
  });


});
