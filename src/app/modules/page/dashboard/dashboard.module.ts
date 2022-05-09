import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { SvgIconsModule } from '@ngneat/svg-icon';
import { NgxDaterangepickerMd } from 'ngx-daterangepicker-material';
import { NgxMapboxGLModule } from 'ngx-mapbox-gl';
import { icons } from '../../shared/shared-icons.module';
import { SharedModule } from '../../shared/shared.module';
import { TemplateModule } from '../../shared/template.module';
import { DashboardFacade } from './abstraction/dashboard.facade';
import { DashboardService } from './core/dashboard.service';
import { DashboardState } from './core/dashboard.state';
import { DashboardRoutingModule } from './dashboard.routing';
import { AlertDonutChartComponent } from './presentation/alert-donut-chart/alert-donut-chart.component';
import { CardComponent } from './presentation/card/card.component';
import { DashboardComponent } from './presentation/dashboard.component';
import { HistogramChartComponent } from './presentation/histogram-chart/histogram-chart.component';
import { MapComponent } from './presentation/map/map.component';
import { ReceivedAlertEmptyStateComponent } from './presentation/received-alert-empty-state/received-alert-empty-state.component';

@NgModule({
  declarations: [
    DashboardComponent,
    MapComponent,
    CardComponent,
    ReceivedAlertEmptyStateComponent,
    AlertDonutChartComponent,
    HistogramChartComponent,
  ],
  imports: [
    NgxMapboxGLModule.withConfig({
      accessToken: 'pk.eyJ1IjoiZmVsaXhnZXJiaWciLCJhIjoiY2sxNmh4d2dvMTJkdTNpcGZtcWhvaHpuNyJ9.2hJW4R6PoiqIgytqUn1kbg',
    }),
    CommonModule,
    TemplateModule,
    SharedModule,
    SvgIconsModule.forChild(icons),
    DashboardRoutingModule,
    NgxDaterangepickerMd.forRoot(),
  ],
  providers: [DashboardService, DashboardFacade, DashboardState],
})
export class DashboardModule {}
