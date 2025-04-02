import { NotificationChannel } from '@shared/components/multi-select-autocomplete/table-type.model';
import { FilterAttribute, FilterOperator } from '@shared/model/filter.model';
import { NotificationTabComponent } from './notification-tab.component';

describe('NotificationTabComponent', () => {
  let component: NotificationTabComponent;

  beforeEach(() => {
    component = new NotificationTabComponent({} as any); // Mock NotificationProcessingService
  });

  describe('createNotificationFilter', () => {
    it('should return basic filter with SENDER channel when empty object provided', () => {
      const result = component.createNotificationFilter({});
      expect(result).toEqual({
        channel: {
          value: [{ value: NotificationChannel.SENDER, strategy: FilterOperator.EQUAL }],
          operator: 'OR'
        }
      });
    });


    it('should convert string array to FilterAttribute with OR operator', () => {
      const input = {
        status: ['READ', 'UNREAD']
      };

      const result = component.createNotificationFilter(input);
      const expected: FilterAttribute = {
        value: [
          { value: 'READ', strategy: FilterOperator.EQUAL },
          { value: 'UNREAD', strategy: FilterOperator.EQUAL }
        ],
        operator: 'OR'
      };

      expect(result.status).toEqual(expected);
    });

    it('should preserve existing FilterAttribute objects', () => {
      const existingFilter: FilterAttribute = {
        value: [{ value: 'CUSTOM', strategy: FilterOperator.EQUAL }],
        operator: 'AND'
      };

      const input = {
        type: [existingFilter]
      };

      const result = component.createNotificationFilter(input);
      expect(result.type).toEqual(existingFilter);
    });

    it('should set RECEIVER channel when createdBy property exists', () => {
      const input = {
        createdBy: ['user123']
      };

      const result = component.createNotificationFilter(input);
      expect(result.channel).toEqual({
        value: [{ value: NotificationChannel.RECEIVER, strategy: FilterOperator.EQUAL }],
        operator: 'OR'
      });
    });

    it('should set SENDER channel when createdBy property does not exist', () => {
      const input = {
        status: ['READ']
      };

      const result = component.createNotificationFilter(input);
      expect(result.channel).toEqual({
        value: [{ value: NotificationChannel.SENDER, strategy: FilterOperator.EQUAL }],
        operator: 'OR'
      });
    });

    it('should handle complex filter object correctly', () => {
      const input = {
        status: ['READ', 'UNREAD'],
        type: [{
          value: [{ value: 'ALERT', strategy: FilterOperator.EQUAL }],
          operator: 'AND'
        }],
        createdBy: ['user123'],
        emptyArray: [],
        undefinedProp: undefined
      };

      const result = component.createNotificationFilter(input);

      expect(result).toEqual({
        status: {
          value: [
            { value: 'READ', strategy: FilterOperator.EQUAL },
            { value: 'UNREAD', strategy: FilterOperator.EQUAL }
          ],
          operator: 'OR'
        },
        type: {
          value: [{ value: 'ALERT', strategy: FilterOperator.EQUAL }],
          operator: 'AND'
        },
        createdBy: {
          value: [{ value: 'user123', strategy: FilterOperator.EQUAL }],
          operator: 'OR'
        },
        channel: {
          value: [{ value: NotificationChannel.RECEIVER, strategy: FilterOperator.EQUAL }],
          operator: 'OR'
        }
      });
    });
  });

  // You might want to add tests for other methods as well
  describe('filterActivated', () => {
    it('should not emit when no filter values are provided', () => {
      spyOn(component.notificationsFilterChanged, 'emit');
      component.filterActivated({});
      expect(component.notificationsFilterChanged.emit).not.toHaveBeenCalled();
    });

    it('should emit when valid filter values are provided', () => {
      spyOn(component.notificationsFilterChanged, 'emit');
      const filter = { status: ['READ'] };
      component.filterActivated(filter);
      expect(component.notificationsFilterChanged.emit).toHaveBeenCalled();
    });
  });
});
