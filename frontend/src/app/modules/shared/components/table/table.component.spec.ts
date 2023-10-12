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

import { Pagination } from '@core/model/pagination.model';
import { TableComponent } from '@shared/components/table/table.component';
import { TableConfig } from '@shared/components/table/table.model';
import { SharedModule } from '@shared/shared.module';
import { fireEvent, screen, waitFor } from '@testing-library/angular';
import { getInputFromChildNodes, renderComponent } from '@tests/test-render.utils';

describe('TableComponent', () => {
  const generateTableContent = (size: number) => {
    return Array.apply(null, Array(size)).map((_, i) => ({ name: 'name_' + i, test: 'test' }));
  };
  const renderTable = (
    size: number,
    displayedColumns = ['name'],
    header = { name: 'Name' },
    selected = jasmine.createSpy(),
  ) => {
    const content = generateTableContent(size);
    const data = { page: 0, pageSize: 10, totalItems: 100, content } as Pagination<unknown>;

    const tableConfig: TableConfig = { displayedColumns, header };
    return renderComponent(
      `<app-table [paginationData]='data' [tableConfig]='tableConfig' (selected)='selected($event)'></app-table>`,
      {
        declarations: [TableComponent],
        imports: [SharedModule],
        componentProperties: {
          data,
          tableConfig,
          selected,
        },
      },
    );
  };

  it('should render table', async () => {
    const tableSize = 7;
    await renderTable(tableSize);

    const tableElement = screen.getByTestId('table-component--test-id');
    expect(tableElement).toBeInTheDocument();

    const tableHeadElement = screen.getByTestId('table-component--head-row');
    expect(tableHeadElement.children[0].children.length).toBe(1);

    const tableBodyElement = screen.getAllByTestId('table-component--body-row');
    expect(tableBodyElement.length).toBe(tableSize);
  });

  it('should render table with correctData', async () => {
    const tableSize = 3;
    await renderTable(tableSize);

    expect(screen.getByText('name_0')).toBeInTheDocument();
    expect(screen.getByText('name_1')).toBeInTheDocument();
    expect(screen.getByText('name_2')).toBeInTheDocument();
  });

  it('should render correct amount of table headers', async () => {
    const tableSize = 3;
    await renderTable(tableSize, ['name'], { name: 'Name for test' });

    expect(screen.getByText('Name for test')).toBeInTheDocument();
  });

  it('should render select column', async () => {
    const tableSize = 3;
    await renderTable(tableSize, ['select', 'name']);

    const selectAllElement = screen.getByTestId('select-all--test-id');
    expect(selectAllElement).toBeInTheDocument();

    const selectItemElements = screen.getAllByTestId('select-one--test-id');
    expect(selectItemElements.length).toBe(tableSize);
  });

  it('should select all items and deselect all', async () => {
    const tableSize = 3;
    await renderTable(tableSize, ['select', 'name']);

    const checkboxSelectAll = getInputFromChildNodes(
      screen.getByTestId('select-all--test-id').firstChild.firstChild.childNodes,
    );

    fireEvent.click(checkboxSelectAll);
    const selectItemElements = screen.getAllByTestId('select-one--test-id');
    for (const element of selectItemElements) {
      await waitFor(() => expect(element).toHaveClass('mat-mdc-checkbox-checked'));
    }

    fireEvent.click(checkboxSelectAll);
    for (const element of selectItemElements) {
      await waitFor(() => expect(element).not.toHaveClass('mat-mdc-checkbox-checked'));
    }
  });

  it('should sort by name', async () => {
    const tableSize = 3;
    const content = generateTableContent(tableSize);
    const data = { page: 0, pageSize: 10, totalItems: 100, content } as Pagination<unknown>;

    const tableConfig: TableConfig = {
      displayedColumns: ['name'],
      header: { name: 'Name Sort' },
      sortableColumns: { name: true },
    };

    const configChange = jasmine.createSpy();
    const component = await renderComponent(
      `<app-table  [paginationData]='data' [tableConfig]='tableConfig' (configChanged)='configChange($event)'></app-table>`,
      {
        declarations: [TableComponent],
        imports: [SharedModule],
        componentProperties: {
          data,
          tableConfig,
          configChange,
        },
      },
    );

    const nameElement = screen.getByText('Name Sort');
    nameElement.click();

    expect(configChange).toHaveBeenCalledWith({ page: 0, pageSize: 10, sorting: ['name', 'asc'] });
    nameElement.click();
    expect(configChange).toHaveBeenCalledWith({ page: 0, pageSize: 10, sorting: ['name', 'desc'] });
    nameElement.click();
    expect(configChange).toHaveBeenCalledWith({ page: 0, pageSize: 10, sorting: ['name', 'desc'] });
  });

  it('should select one item', async () => {
    const tableSize = 3;
    const selected = jasmine.createSpy();
    await renderTable(tableSize, ['name'], { name: 'Name for test' }, selected);

    const tableElement = screen.getByText('name_0');
    expect(tableElement).toBeInTheDocument();

    tableElement.click();
    expect(selected).toHaveBeenCalledWith({ name: 'name_0', test: 'test' });
  });
});
