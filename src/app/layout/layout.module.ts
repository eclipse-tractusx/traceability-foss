import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { SvgIconsModule } from '@ngneat/svg-icon';
import { SharedModule } from '../shared/shared.module';
import { TemplateModule } from '../shared/template.module';
import { icons } from '../shared/shared-icons.module';
import { LayoutRoutingModule } from './layout.routing';
import { NavBarComponent } from './presentation/nav-bar/nav-bar.component';
import { PrivateLayoutComponent } from './presentation/private-layout/private-layout.component';
import { ResizerComponent } from './presentation/resizer/resizer.component';
import { SidebarComponent } from './presentation/sidebar/sidebar.component';
import { FooterComponent } from './presentation/footer/footer.component';
import { SpinnerOverlayComponent } from './presentation/spinner-overlay/presentation/spinner-overlay.component';

@NgModule({
  declarations: [
    PrivateLayoutComponent,
    NavBarComponent,
    ResizerComponent,
    SidebarComponent,
    FooterComponent,
    SpinnerOverlayComponent,
  ],
  imports: [CommonModule, LayoutRoutingModule, TemplateModule, SharedModule, SvgIconsModule.forChild(icons)],
})
export class LayoutModule {}
