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
package assets.importpoc;

import assets.response.base.response.ImportStateResponse;

import java.time.Instant;
import java.util.List;

public record ImportReportResponse(ImportJobResponse importJobResponse,
                                   List<ImportedAssetResponse> importedAssetResponse) {

    public record ImportJobResponse(String importId, Instant startedOn, Instant completedOn,
                                    ImportJobStatusResponse importJobStatusResponse) {

    }

    public record ImportedAssetResponse(String catenaxId, ImportStateResponse importStateResponse, Instant importedOn,
                                        String importMessage) {

    }
}
