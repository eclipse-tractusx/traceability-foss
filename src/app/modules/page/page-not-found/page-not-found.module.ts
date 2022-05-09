import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { SvgIconsModule } from '@ngneat/svg-icon';
import { icons, SharedModule, TemplateModule } from '@shared';
import { PageNotFoundComponent } from './presentation/page-not-found.component';

@NgModule({
  declarations: [PageNotFoundComponent],
  imports: [CommonModule, TemplateModule, SharedModule, SvgIconsModule.forChild(icons)],
})
export class PageNotFoundModule {}
