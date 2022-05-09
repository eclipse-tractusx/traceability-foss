import {
  Directive,
  EventEmitter,
  HostBinding,
  Input,
  OnChanges,
  Output,
  SimpleChanges,
  TemplateRef,
  ViewContainerRef,
} from '@angular/core';
import { ColumnConfig } from './column-config';

@Directive({
  selector: '[appRowDetail]',
})
export class RowDetailDirective implements OnChanges {
  @HostBinding('class.expanded')
  get expanded(): boolean {
    return this.expand;
  }

  @Input()
  set appRowDetail(value: ColumnConfig) {
    if (value !== this.row) {
      this.row = value;
    }
  }

  @Input('rowDetailTemplate')
  set template(template: TemplateRef<unknown>) {
    if (template !== this.templateReference) {
      this.templateReference = template;
    }
  }

  @Input() onExpanded: boolean;

  @Output() toggleChange = new EventEmitter<RowDetailDirective>();

  private row: ColumnConfig;
  private templateReference: TemplateRef<unknown>;
  private expand: boolean;

  constructor(public viewContainer: ViewContainerRef) {}

  ngOnChanges(changes: SimpleChanges): void {
    if (!changes.onExpanded) {
      return;
    }
    changes.onExpanded.currentValue ? this.render() : this.viewContainer.clear();
  }

  private render(): void {
    this.viewContainer.clear();
    if (this.templateReference && this.row) {
      this.viewContainer.createEmbeddedView(this.templateReference, { $implicit: this.row });
    }
  }
}
