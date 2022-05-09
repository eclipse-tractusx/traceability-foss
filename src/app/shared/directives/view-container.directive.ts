import { AfterViewInit, Directive, Input, TemplateRef, ViewContainerRef } from '@angular/core';
import { ViewContext } from '../model/view-context.model';
import { View } from '../model/view.model';

// https://indepth.dev/posts/1435/view-state-selector-design-pattern
@Directive({ selector: '[viewContainer]' })
export class ViewContainerDirective<T> implements AfterViewInit {
  @Input() set viewContainerLoading(templateRef: TemplateRef<ViewContext<T>>) {
    this.loaderTemplateRef = templateRef;
  }

  @Input() set viewContainerMain(templateRef: TemplateRef<ViewContext<T>>) {
    this.mainTemplateRef = templateRef;
  }

  @Input() set viewContainerError(templateRef: TemplateRef<ViewContext<T>>) {
    this.errorTemplateRef = templateRef;
  }

  @Input() set viewContainer(view: View<T>) {
    if (!view) return;

    this.context.view = view;
    this.viewContainerRef.clear();

    if (view.loader) this.viewContainerRef.createEmbeddedView(this.loaderTemplateRef, this.context);

    if (view.error && !view.loader) this.viewContainerRef.createEmbeddedView(this.errorTemplateRef, this.context);

    if (view.data && !view.error) this.viewContainerRef.createEmbeddedView(this.mainTemplateRef, this.context);
  }

  private context: ViewContext<T> = new ViewContext<T>();
  private mainTemplateRef: TemplateRef<ViewContext<T>> = null;
  private errorTemplateRef: TemplateRef<ViewContext<T>> = null;
  private loaderTemplateRef: TemplateRef<ViewContext<T>> = null;

  constructor(private viewContainerRef: ViewContainerRef) {}

  ngAfterViewInit(): void {
    if (!this.loaderTemplateRef) throw new Error('View Pattern: Missing Loader Template');
    if (!this.errorTemplateRef) throw new Error('View Pattern: Missing Error Template');
    if (!this.mainTemplateRef) throw new Error('View Pattern: Missing Main Template');
  }
}
