import { NgModule } from '@angular/core';
import { DashboardComponent } from './presentation/dashboard.component';
import { MapComponent } from './presentation/map/map.component';
import { DashboardService } from './core/dashboard.service';
import { TemplateModule } from '../../shared/template.module';
import { CommonModule } from '@angular/common';
import { NgxMapboxGLModule } from 'ngx-mapbox-gl';
import { SharedModule } from '../../shared/shared.module';
import { SvgIconsModule } from '@ngneat/svg-icon';
import { icons } from '../../shared/shared-icons.module';
import { DashboardFacade } from './abstraction/dashboard.facade';
import { DashboardState } from './core/dashboard.state';
import { DashboardRoutingModule } from './dashboard.routing';
import { CardComponent } from './presentation/card/card.component';
import { ReceivedAlertEmptyStateComponent } from './presentation/received-alert-empty-state/received-alert-empty-state.component';
import { AlertDonutChartComponent } from './presentation/alert-donut-chart/alert-donut-chart.component';
import { HistogramChartComponent } from './presentation/histogram-chart/histogram-chart.component';
import { NgxDaterangepickerMd } from 'ngx-daterangepicker-material';

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
