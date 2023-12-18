/********************************************************************************
 * Copyright (c) 2023 Contributors to the Eclipse Foundation
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


import { AdminModule } from '@page/admin/admin.module';
import { ImportJsonComponent } from '@page/admin/presentation/import-json/import-json.component';
import { screen, waitFor } from '@testing-library/angular';
import { renderComponent } from '@tests/test-render.utils';
import {AdminFacade} from "@page/admin/core/admin.facade";
import {TestBed} from "@angular/core/testing";

describe('ImportJsonComponent', () => {
  const jsonFileContent = {
    key1: 'value1',
    key2: 'value2',
    key3: {
      nestedKey: 'nestedValue',
      nestedArray: [1, 2, 3],
    },
  };

  const jsonString = JSON.stringify(jsonFileContent, null, 2);

  const createJsonFile = (fileName: string, fileType: string) => {
    const jsonBlob = new Blob([jsonString], { type: fileType });
    return new File([jsonBlob], fileName, { type: fileType });
  };

  const renderComponentWithJsonFile = async (jsonFile: File, showError: boolean = false) => {
    return renderComponent(ImportJsonComponent, {
      imports: [AdminModule],
    });
  };



  it('should get the json-file', async () => {
    const jsonFile = createJsonFile('example.json', 'application/json');
    const { fixture } = await renderComponentWithJsonFile(jsonFile, false);

    const { componentInstance } = fixture;
    const event = { target: { files: [jsonFile] } };

    // Act
    componentInstance.getFile(event);

    // Assert
    expect(componentInstance.showError).toBe(false);
    expect(componentInstance.file).toBeTruthy();
  });

  it('should show error Message', async () => {
    const jsonFile = createJsonFile('example.pdf', 'application/pdf');
    const { fixture } = await renderComponentWithJsonFile(jsonFile, false);

    const { componentInstance } = fixture;
    const event = { target: { files: [jsonFile] } };

    // Act
    componentInstance.getFile(event);

    // Assert
    expect(componentInstance.showError).toBe(true);
    expect(componentInstance.file).toBeTruthy();
    expect(await waitFor(() => screen.getByText('pageAdmin.importJson.error'))).toBeInTheDocument();
  });

  it('should clear the json-file', async () => {
    const jsonFile = createJsonFile('example.json', 'application/json');
    const { fixture } = await renderComponentWithJsonFile(jsonFile, false);

    const { componentInstance } = fixture;
    const event = { target: { files: [jsonFile] } };

    // Arrange
    componentInstance.getFile(event);

    // Act
    componentInstance.clearFile();

    // Assert
    expect(componentInstance.showError).toBe(false);
    expect(componentInstance.file).toBeFalsy();
  });

});
