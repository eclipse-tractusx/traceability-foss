/********************************************************************************
 * Copyright (c) 2022, 2023 Bayerische Motoren Werke Aktiengesellschaft (BMW AG)
 * Copyright (c) 2022, 2023 ZF Friedrichshafen AG
 * Copyright (c) 2022, 2023 Contributors to the Eclipse Foundation
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Apache License, Version 2.0 which is available at
 * https://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 ********************************************************************************/

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

  @Input() set viewContainerCustomContext(customContext: Record<string, unknown>) {
    this.customContext = customContext;
  }

  @Input() set viewContainer(view: View<T>) {
    if (!view) return;

    this.context.view = view;
    this.context.customContext = this.customContext;

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
  private customContext: Record<string, unknown> = {};

  constructor(private readonly viewContainerRef: ViewContainerRef) {
  }

  public ngAfterViewInit(): void {
    if (!this.loaderTemplateRef) throw new Error('View Pattern: Missing Loader Template');
    if (!this.errorTemplateRef) throw new Error('View Pattern: Missing Error Template');
    if (!this.mainTemplateRef) throw new Error('View Pattern: Missing Main Template');
  }
}
