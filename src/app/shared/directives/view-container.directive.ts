/*
 * Copyright 2021 The PartChain Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import { AfterViewInit, Directive, Input, TemplateRef, ViewContainerRef } from '@angular/core';
import { ViewContext } from '../model/view-context.model';
import { View } from '../model/view.model';

/**
 * https://indepth.dev/posts/1435/view-state-selector-design-pattern
 *
 * @export
 * @class ViewContainerDirective
 * @implements {AfterViewInit}
 * @template T
 */
@Directive({ selector: '[viewContainer]' })
export class ViewContainerDirective<T> implements AfterViewInit {
  /**
   * loading template
   *
   * @param {TemplateRef<ViewContext<T>>} templateRef
   * @memberof ViewContainerDirective
   */
  @Input() set viewContainerLoading(templateRef: TemplateRef<ViewContext<T>>) {
    this.loaderTemplateRef = templateRef;
  }

  /**
   * Main template
   *
   * @param {TemplateRef<ViewContext<T>>} templateRef
   * @memberof ViewContainerDirective
   */
  @Input() set viewContainerMain(templateRef: TemplateRef<ViewContext<T>>) {
    this.mainTemplateRef = templateRef;
  }

  /**
   * Error template
   *
   * @param {TemplateRef<ViewContext<T>>} templateRef
   * @memberof ViewContainerDirective
   */
  @Input() set viewContainerError(templateRef: TemplateRef<ViewContext<T>>) {
    this.errorTemplateRef = templateRef;
  }

  /**
   * View setter
   *
   * @param {View<T>} view
   * @memberof ViewContainerDirective
   */
  @Input() set viewContainer(view: View<T>) {
    if (!view) return;

    this.context.view = view;
    this.viewContainerRef.clear();

    if (view.loader) this.viewContainerRef.createEmbeddedView(this.loaderTemplateRef, this.context);

    if (view.error && !view.loader) this.viewContainerRef.createEmbeddedView(this.errorTemplateRef, this.context);

    if (view.data && !view.error) this.viewContainerRef.createEmbeddedView(this.mainTemplateRef, this.context);
  }

  /**
   * View context
   *
   * @private
   * @type {ViewContext<T>}
   * @memberof ViewContainerDirective
   */
  private context: ViewContext<T> = new ViewContext<T>();

  /**
   * Main template reference
   *
   * @private
   * @type {TemplateRef<ViewContext<T>>}
   * @memberof ViewContainerDirective
   */
  private mainTemplateRef: TemplateRef<ViewContext<T>> = null;

  /**
   * Error template reference
   *
   * @private
   * @type {TemplateRef<ViewContext<T>>}
   * @memberof ViewContainerDirective
   */
  private errorTemplateRef: TemplateRef<ViewContext<T>> = null;

  /**
   * Loading template reference
   *
   * @private
   * @type {TemplateRef<ViewContext<T>>}
   * @memberof ViewContainerDirective
   */
  private loaderTemplateRef: TemplateRef<ViewContext<T>> = null;

  /**
   * Creates an instance of ViewContainerDirective.
   * @param {ViewContainerRef} viewContainerRef
   * @memberof ViewContainerDirective
   */
  constructor(private viewContainerRef: ViewContainerRef) {}

  /**
   * Angular lifecycle method - Ng After View Init
   *
   * @return {void}
   * @memberof ViewContainerDirective
   */
  ngAfterViewInit(): void {
    if (!this.loaderTemplateRef) throw new Error('View Pattern: Missing Loader Template');
    if (!this.errorTemplateRef) throw new Error('View Pattern: Missing Error Template');
    if (!this.mainTemplateRef) throw new Error('View Pattern: Missing Main Template');
  }
}
