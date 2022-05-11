import { Component, OnInit, ViewEncapsulation } from '@angular/core';
import { Observable } from 'rxjs';
import { View } from '@shared/model/view.model';
import { DashboardFacade } from '../abstraction/dashboard.facade';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss'],
  encapsulation: ViewEncapsulation.None,
})
export class DashboardComponent implements OnInit {
  public title = 'Dashboard';
  public numberOfParts$: Observable<View<number>>;

  constructor(private dashboardFacade: DashboardFacade) {
    this.numberOfParts$ = this.dashboardFacade.numberOfParts$;
  }

  ngOnInit(): void {
    this.dashboardFacade.setNumberOfParts();
  }
}
