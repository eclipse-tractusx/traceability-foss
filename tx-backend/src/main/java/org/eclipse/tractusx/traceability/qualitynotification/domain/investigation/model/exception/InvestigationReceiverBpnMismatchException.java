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

package org.eclipse.tractusx.traceability.qualitynotification.domain.investigation.model.exception;

import org.eclipse.tractusx.traceability.common.model.BPN;

public class InvestigationReceiverBpnMismatchException extends IllegalArgumentException {

    public InvestigationReceiverBpnMismatchException(String message) {
        super(message);
    }

    public InvestigationReceiverBpnMismatchException(BPN actual, BPN receiver, String investigationId) {
        super("%s BPN is not eligible to handle %s BPN %s investigation".formatted(actual, receiver, investigationId));
    }
}
