import { SharedModule } from '@shared/shared.module';
import { renderComponent } from '@tests/test-render.utils';

import { ChipComponent } from './chip.component';

describe('ChipComponent', () => {

  const renderChipComponent = () => {
    return renderComponent(ChipComponent, {
      imports: [SharedModule],

    })
  }

  it('should create', async () => {
    const { fixture} = await renderChipComponent();
    const { componentInstance } = fixture;
    expect(componentInstance).toBeTruthy();
  });
});
