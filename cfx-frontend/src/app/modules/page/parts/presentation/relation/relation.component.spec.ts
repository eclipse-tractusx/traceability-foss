import { MAT_DIALOG_DATA } from '@angular/material/dialog';
import { RelationComponent } from '@page/parts/presentation/relation/relation.component';
import { renderComponent } from '@tests/test-render.utils';

describe('RelationComponent', () => {
  const renderRelationComponent = async () => {
    return renderComponent(RelationComponent, {
      providers: [{ provide: MAT_DIALOG_DATA, useValue: { partId: 'ID1', partName: 'TestNameOfPart', context: 'parts' } }]
    });
  };

  it('should assign the values correct to the used variables.', async () => {
    const { fixture } = await renderRelationComponent();
    const { componentInstance } = fixture;

    expect(componentInstance.overwriteContext).toEqual('parts');
    expect(componentInstance.partId).toEqual('ID1');
    expect(componentInstance.partName).toEqual('TestNameOfPart');
  });
});
