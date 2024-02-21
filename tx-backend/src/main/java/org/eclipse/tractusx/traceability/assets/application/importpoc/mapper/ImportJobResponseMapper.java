/********************************************************************************
 * Copyright (c) 2024 Contributors to the Eclipse Foundation
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
package org.eclipse.tractusx.traceability.assets.application.importpoc.mapper;

import assets.importpoc.ImportJobStatusResponse;
import assets.importpoc.ImportReportResponse;
import assets.response.base.response.ImportStateResponse;
import org.eclipse.tractusx.traceability.assets.domain.importpoc.model.ImportJob;

import java.util.List;
import java.util.stream.Stream;

public class ImportJobResponseMapper {
    public static ImportReportResponse from(ImportJob importJob) {

        ImportReportResponse.ImportJob importJobResponse =
                new ImportReportResponse.ImportJob(
                        importJob.getId().toString(),
                        importJob.getStartedOn().toString(),
                        importJob.getCompletedOn().toString(),
                        ImportJobStatusResponse.valueOf(importJob.getStatus().toString()));

        List<ImportReportResponse.ImportedAsset> importedAssets =
                Stream.concat(importJob.getAssetAsBuilt().stream(), importJob.getAssetAsPlanned().stream())
                        .map(
                                asset -> new ImportReportResponse.ImportedAsset(
                                        asset.getId(),
                                        ImportStateResponse.valueOf(asset.getImportState().toString()),
                                        importJob.getStartedOn().toString(),
                                        asset.getImportNote()
                                )
                        ).toList();
        return new ImportReportResponse(importJobResponse, importedAssets);
    }
}
