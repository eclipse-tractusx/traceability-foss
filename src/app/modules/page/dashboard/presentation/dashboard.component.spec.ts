import { screen } from '@testing-library/angular';
import { renderComponent } from '@tests/test-render.utils';

import { DashboardComponent } from './dashboard.component';
import { DashboardModule } from '../dashboard.module';

describe('Dashboard', () => {
  it('dummy test', async () => {
    await renderComponent(DashboardComponent, {
      imports: [DashboardModule],
    });

    expect(screen.getByText('Dashboard')).toBeInTheDocument()
  });
});