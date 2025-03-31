import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-breadcrumb',
  templateUrl: './breadcrumb.component.html',
  styleUrls: ['./breadcrumb.component.scss']
})
export class BreadcrumbComponent {
  @Input() variant: 'As built' | 'As planned' = 'As built';
  @Input() partName: string = '';
  @Input() uuid: string = '';
}

