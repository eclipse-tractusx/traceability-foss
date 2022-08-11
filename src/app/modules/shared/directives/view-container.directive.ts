/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

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

  @Input() set viewContainerParams(params: Record<string, unknown>) {
    this.params = params;
  }

  @Input() set viewContainer(view: View<T>) {
    if (!view) return;

    this.context.view = view;

    for (const paramKey of Object.keys(this.params)) {
      this.context[paramKey] = this.params[paramKey];
    }

    let templateRef: TemplateRef<ViewContext<T>>;

    if (view.loader) templateRef = this.loaderTemplateRef;

    if (view.error && !view.loader) templateRef = this.errorTemplateRef;

    if (view.data !== undefined && view.data !== null && !view.error) templateRef = this.mainTemplateRef;

    if (!!templateRef && this.currentTemplateRef !== templateRef) {
      this.viewContainerRef.clear();
      this.viewContainerRef.createEmbeddedView(templateRef, this.context);
      this.currentTemplateRef = templateRef;
    }
  }

  private readonly context: ViewContext<T> = new ViewContext<T>();
  private mainTemplateRef: TemplateRef<ViewContext<T>> = null;
  private errorTemplateRef: TemplateRef<ViewContext<T>> = null;
  private loaderTemplateRef: TemplateRef<ViewContext<T>> = null;
  private currentTemplateRef: TemplateRef<ViewContext<T>> = null;
  private params: Record<string, unknown> = {};

  constructor(private readonly viewContainerRef: ViewContainerRef) {}

  public ngAfterViewInit(): void {
    if (!this.loaderTemplateRef) throw new Error('View Pattern: Missing Loader Template');
    if (!this.errorTemplateRef) throw new Error('View Pattern: Missing Error Template');
    if (!this.mainTemplateRef) throw new Error('View Pattern: Missing Main Template');
  }
}
