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


import {AdminModule} from '@page/admin/admin.module';
import {ImportJsonComponent} from '@page/admin/presentation/import-json/import-json.component';
import {screen, waitFor} from '@testing-library/angular';
import {renderComponent} from '@tests/test-render.utils';

describe('ImportJsonComponent', () => {
  const dummyJsonFileContent = {
    key1: 'value1',
    key2: 'value2',
    key3: {
      nestedKey: 'nestedValue',
      nestedArray: [1, 2, 3],
    },
  };

  const dummyJsonString = JSON.stringify(dummyJsonFileContent, null, 2);

  const createDummyJsonFile = (fileName: string, fileType: string) => {
    const dummyJsonBlob = new Blob([dummyJsonString], { type: fileType });
    return new File([dummyJsonBlob], fileName, { type: fileType });
  };

  const jsonFile = createDummyJsonFile('example.json', 'application/json');

  const renderComponentWithJsonFile = async (jsonFile: File, showError: boolean = false) => {
    return renderComponent(ImportJsonComponent, {
      imports: [AdminModule],
    });
  };



  it('should get the json-file', async () => {
    const jsonFile = createDummyJsonFile('example.json', 'application/json');
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
    const File = createDummyJsonFile('example.pdf', 'application/pdf');
    const { fixture } = await renderComponentWithJsonFile(File, false);

    const { componentInstance } = fixture;
    const event = { target: { files: [File] } };

    // Act
    componentInstance.getFile(event);

    // Assert
    expect(componentInstance.showError).toBe(true);
    expect(componentInstance.file).toBeTruthy();
    expect(await waitFor(() => screen.getByText('pageAdmin.importJson.error'))).toBeInTheDocument();
  });

  it('should clear the json-file', async () => {
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

  it('should show FileContainer_upload_file', async () => {

    const { fixture } = await renderComponentWithJsonFile(jsonFile, false);

    const { componentInstance } = fixture;
    const event = { target: { files: [jsonFile] } };

    // Arrange
    componentInstance.getFile(event);

    // Assert
    expect(componentInstance.showError).toBe(false);
    expect(componentInstance.file).toBeTruthy();
    expect(componentInstance.shouldShowFileContainer_upload_file(jsonFile)).toBe(true);
    expect(componentInstance.getFileExtension(jsonFile)).toEqual('json')

  })

  it('should return null if file extension is null', async () => {

    const { fixture } = await renderComponentWithJsonFile(jsonFile, false);

    const { componentInstance } = fixture;
    const extensionResult = componentInstance.getFileExtension(null);
    expect(extensionResult).toEqual(null)
  })

  it('should set error variables correctly', async () => {
    const { fixture } = await renderComponentWithJsonFile(jsonFile, false);
    const { componentInstance } = fixture;

    const errorResponse = {
      error: {
        importStateMessage: ["test message"],
        validationResult: {
          validationErrors: ["error1", "error2"]
        }
      }
    }

    componentInstance.setValidationReport(errorResponse);

    expect(componentInstance.errorImportStateMessage).toEqual(errorResponse.error.importStateMessage);
    expect(componentInstance.errorValidationResult).toEqual(errorResponse.error.validationResult);
    expect(componentInstance.errorValidationErrors).toEqual(errorResponse.error.validationResult.validationErrors);

  })

  it('should set asset variables correctly', async () => {
    const { fixture } = await renderComponentWithJsonFile(jsonFile, false);
    const { componentInstance } = fixture;

    const assetResponse = {
      importStateMessage: [
        { catenaXId: 'id1', persistedOrUpdated: true },
        { catenaXId: 'id2', persistedOrUpdated: false },
      ],
    };

    componentInstance.setAssetReport(assetResponse);

    expect(componentInstance.assetResponse).toEqual(assetResponse.importStateMessage);
    expect(componentInstance.displayUploader).toBeFalsy();
  });








});
