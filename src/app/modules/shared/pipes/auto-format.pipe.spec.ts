import { screen } from '@testing-library/angular';
import { CalendarDateModel } from '@core/model/calendar-date.model';
import { renderComponent } from '@tests/test-render.utils';

import { SharedModule } from '..';

describe('AutoFormatPipe', () => {
  it('should format CalendarDateModel as short date', async () => {
    await renderComponent(`{{ rawValue | autoFormat }}`, {
      imports: [SharedModule],
      componentProperties: {
        rawValue: new CalendarDateModel('2022-07-15T10:00:00.000Z'),
      },
    });

    expect(screen.getByText('7/15/2022')).toBeInTheDocument();
  });

  it('should format string as it is', async () => {
    await renderComponent(`{{ rawValue | autoFormat }}`, {
      imports: [SharedModule],
      componentProperties: {
        rawValue: 'some text',
      },
    });

    expect(screen.getByText('some text')).toBeInTheDocument();
  });

  it('should format number as a string', async () => {
    await renderComponent(`{{ rawValue | autoFormat }}`, {
      imports: [SharedModule],
      componentProperties: {
        rawValue: 100000.11234,
      },
    });

    expect(screen.getByText('100000.11234')).toBeInTheDocument();
  });

  it('should format object as a string', async () => {
    await renderComponent(`{{ rawValue | autoFormat }}`, {
      imports: [SharedModule],
      componentProperties: {
        rawValue: {},
      },
    });

    expect(screen.getByText('[object Object]')).toBeInTheDocument();
  });
});
