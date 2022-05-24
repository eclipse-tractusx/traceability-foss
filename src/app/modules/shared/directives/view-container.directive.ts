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

  @Input() set viewContainer(view: View<T>) {
    if (!view) return;

    this.context.view = view;
    this.viewContainerRef.clear();

    if (view.loader) this.viewContainerRef.createEmbeddedView(this.loaderTemplateRef, this.context);

    if (view.error && !view.loader) this.viewContainerRef.createEmbeddedView(this.errorTemplateRef, this.context);

    if (view.data !== undefined && view.data !== null && !view.error) {
      this.viewContainerRef.createEmbeddedView(this.mainTemplateRef, this.context);
    }
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
