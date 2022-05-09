import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { SvgIconsModule } from '@ngneat/svg-icon';
import { SharedModule } from '../../shared/shared.module';
import { TemplateModule } from '../../shared/template.module';
import { PageNotFoundComponent } from './presentation/page-not-found.component';
import { icons } from '../../shared/shared-icons.module';

@NgModule({
  declarations: [PageNotFoundComponent],
  imports: [CommonModule, TemplateModule, SharedModule, SvgIconsModule.forChild(icons)],
})
export class PageNotFoundModule {}
