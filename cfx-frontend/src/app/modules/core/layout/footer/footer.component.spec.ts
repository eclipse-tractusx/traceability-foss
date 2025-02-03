import { LayoutModule } from '@layout/layout.module';
import { FooterComponent } from '@layout/footer/footer.component';
import { SharedModule } from '@shared/shared.module';
import { screen, waitFor } from '@testing-library/angular';
import { renderComponent } from '@tests/test-render.utils';

describe('Footer', () => {
  const renderFooter = () => {
    return renderComponent(FooterComponent, {
      imports: [LayoutModule, SharedModule],
    });
  };
  it('should render footer', async () => {
    await renderFooter();

    expect(await waitFor(() => screen.getByText('layout.footer.imprint'))).toBeInTheDocument();
    expect(await waitFor(() => screen.getByText('© Cofinity-X GmbH - All rights reserved.'))).toBeInTheDocument();
  });
});
