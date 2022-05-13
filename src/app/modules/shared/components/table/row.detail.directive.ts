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
