import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { SvgIconsModule } from '@ngneat/svg-icon';
import { icons } from '../../shared/shared-icons.module';
import { SharedModule } from '../../shared/shared.module';
import { TemplateModule } from '../../shared/template.module';
import { FooterComponent } from './footer/footer.component';
import { LayoutRoutingModule } from './layout.routing';
import { NavBarComponent } from './nav-bar/nav-bar.component';
import { PrivateLayoutComponent } from './private-layout/private-layout.component';
import { ResizerComponent } from './resizer/resizer.component';
import { SidebarComponent } from './sidebar/sidebar.component';
import { SpinnerOverlayComponent } from './spinner-overlay/spinner-overlay.component';
import { SidebarSectionComponent } from './sidebar/sidebar-section/sidebar-section.component';

@NgModule({
  declarations: [
    PrivateLayoutComponent,
    NavBarComponent,
    ResizerComponent,
    SidebarComponent,
    FooterComponent,
    SpinnerOverlayComponent,
    SidebarSectionComponent,
  ],
  imports: [CommonModule, LayoutRoutingModule, TemplateModule, SharedModule, SvgIconsModule.forChild(icons)],
})
export class LayoutModule {}
