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
package org.eclipse.tractusx.traceability.assets.domain.base.model;

public class ImportNote {
    public static final String TRANSIENT_CREATED = "Asset created successfully in transient state.";
    public static final String TRANSIENT_UPDATED = "Asset updated successfully in transient state.";
    public static final String PERSISTED = "Asset created/updated successfully in persistent state.";
    public static final String IN_SYNCHRONIZATION = "Twin in sync with digital twin registry. Twin will not be updated.";
    public static final String PUBLISHED_TO_CORE_SERVICES = "Assets published to core services.";
    public static final String ERROR_DTR_SHELL_CREATION_FAILED = "Failed to create shell in DTR.";
    public static final String ERROR_EDC_POLICY_CREATION_FAILED = "Failed to create policy in EDC.";
    public static final String ERROR_EDC_ASSET_CREATION_FAILED = "Failed to create asset in EDC.";
    public static final String ERROR_EDC_CONTRACT_CREATION_FAILED = "Failed to create contract in EDC.";
}
