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

import { TemplateRef } from '@angular/core';
import { Role } from '@core/user/role.model';
import { FilterOperator } from '@page/parts/model/parts.model';

export type TableHeaderSort = [string, 'asc' | 'desc'];

export interface Option {
  display: string;
  value: any;
  checked: boolean;
}

export interface FilterConfig {
  filterKey: string;
  isTextSearch: boolean;
  singleSearch?: boolean;
  isDate?: boolean;
  option: Option[];
  maxDate?: Date | null;
  column?: string;
}

export type DisplayColumns<T> = 'select' | 'menu' | 'settings' | T;

export const CreateHeaderFromColumns = (columns: string[], headerKey: string): Record<string, string> => {
  return columns?.reduce((header, column) => ({ ...header, [column]: `${headerKey}.${column}` }), {});
};
export interface FilterInfo {
  filterValue: string;
  filterOperator: FilterOperator;
}

export enum FilterMethod {
  AND = 'AND',
  OR = 'OR',
}

export interface TableFilter {
  filterMethod: FilterMethod;
  createdDate?: FilterInfo;
  description?: FilterInfo;
  status?: FilterInfo[];
  severity?: FilterInfo[];
  createdBy?: FilterInfo;
  sendTo?: FilterInfo;
}

export interface TablePaginationEventConfig {
  page: number;
  pageSize: number;
}

export interface TableEventConfig extends TablePaginationEventConfig {
  sorting: TableHeaderSort;
  filtering?: TableFilter;
}

export interface MenuActionConfig<T> {
  label: string;
  icon: string;
  action: (data: T) => void;
  condition?: (data: T) => boolean;
  isAuthorized?: boolean;
}


export enum SortingOptions {
  NONE = 'NONE',
  ASC = 'ASC',
  DSC = 'DSC',
}

export interface TableConfig<Columns extends string = string> {
  displayedColumns: DisplayColumns<Columns>[];
  columnRoles?: Record<Columns, Role>;
  sortableColumns?: Record<Columns, boolean>;
  header?: Record<Columns, string>;
  hasPagination?: boolean;
  cellRenderers?: Partial<Record<Columns, TemplateRef<unknown>>>;
  menuActionsConfig?: MenuActionConfig<unknown>[];
}
