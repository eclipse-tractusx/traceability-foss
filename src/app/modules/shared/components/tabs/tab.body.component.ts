import { Component, TemplateRef, ViewChild } from '@angular/core';

@Component({
  selector: 'app-tab-body',
  template: '<ng-template><ng-content></ng-content></ng-template>',
})
export class TabBodyComponent {
  @ViewChild(TemplateRef) bodyContent: TemplateRef<unknown>;
}
