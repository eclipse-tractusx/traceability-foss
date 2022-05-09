import { Component, OnInit, ViewEncapsulation } from '@angular/core';
import { LayoutFacade } from 'src/app/modules/shared/abstraction/layout-facade';

@Component({
  selector: 'app-private-layout',
  templateUrl: './private-layout.component.html',
  styleUrls: ['./private-layout.component.scss'],
  encapsulation: ViewEncapsulation.None,
})
export class PrivateLayoutComponent implements OnInit {
  public expanded: boolean;

  constructor(private layoutFacade: LayoutFacade) {
    this.expanded = false;
  }

  ngOnInit(): void {
    this.handleResize();
  }

  public handleResize(): void {
    const match = window.matchMedia('(min-width: 1024px)');
    match.addEventListener('change', e => {
      this.expanded = e.matches;
    });
  }

  public manualToggle(): void {
    this.expanded = !this.expanded;
    this.layoutFacade.setIsSideBarExpanded(this.expanded);
  }
}
