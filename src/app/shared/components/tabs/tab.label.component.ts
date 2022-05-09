import { Component, TemplateRef, ViewChild } from '@angular/core';

@Component({
  selector: 'app-tab-label',
  template: '<ng-template><ng-content></ng-content></ng-template>',
})
export class TabLabelComponent {
  @ViewChild(TemplateRef) labelContent: TemplateRef<unknown>;
}
