import { Directive, HostListener, Input } from '@angular/core';
import { MatTooltip } from '@angular/material/tooltip';

@Directive({
  selector: '[appTooltip]',
  providers: [MatTooltip],
})
export class TooltipDirective {
  @Input() tooltip: string;

  public appTooltip: MatTooltip;

  @HostListener('mouseover') mouseover(): void {
    this.appTooltip.message = this.tooltip;
    this.appTooltip.show();
  }

  @HostListener('mouseleave') mouseleave(): void {
    this.appTooltip.hide();
  }

  constructor(appTooltip: MatTooltip) {
    this.appTooltip = appTooltip;
  }
}
