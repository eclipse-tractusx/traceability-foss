import { Directive, ElementRef, EventEmitter, HostListener, Output } from '@angular/core';

@Directive({ selector: '[clickOutside]' })
export class ClickOutsideDirective {
  @Output() clickOutside: EventEmitter<MouseEvent> = new EventEmitter<MouseEvent>();

  @HostListener('document:click', ['$event'])
  public onDocumentClick(event: MouseEvent): void {
    const targetElement = event.target as HTMLElement;

    // Check if the click was outside the element
    if (targetElement && !this.elementRef.nativeElement.contains(targetElement)) {
      this.clickOutside.emit(event);
    }
  }

  constructor(private elementRef: ElementRef) {}
}
