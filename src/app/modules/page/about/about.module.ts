import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { SvgIconsModule } from '@ngneat/svg-icon';
import { icons } from '../../shared/shared-icons.module';
import { SharedModule } from '../../shared/shared.module';
import { TemplateModule } from '../../shared/template.module';
import { AboutRoutingModule } from './about.routing';
import { AboutComponent } from './presentation/about.component';

@NgModule({
  declarations: [AboutComponent],
  imports: [CommonModule, TemplateModule, SharedModule, SvgIconsModule.forChild(icons), AboutRoutingModule],
})
export class AboutModule {}
