import { CommonModule } from '@angular/common';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { getI18nPageProvider } from '@core/i18n';
import { AlertsModule } from '@page/alerts/alerts.module';
import { AlertsComponent } from '@page/alerts/presentation/alerts.component';
import { ImprintModule } from '@page/imprint/imprint.module';
import { screen } from '@testing-library/angular';
import { renderComponent } from '@tests/test-render.utils';

import { ImprintComponent } from './imprint.component';

describe('ImprintComponent', () => {
  let component: ImprintComponent;

  const renderImprint = async () => {
    return await renderComponent(ImprintComponent, {
      imports: [ImprintModule],
      translations: ['pageImprint']
    });
  };

  it('should render the component', async () => {
    await renderImprint();
    const imprintTitle = screen.getByText('pageImprint.title');
    expect(imprintTitle).toBeInTheDocument();
  });
});


