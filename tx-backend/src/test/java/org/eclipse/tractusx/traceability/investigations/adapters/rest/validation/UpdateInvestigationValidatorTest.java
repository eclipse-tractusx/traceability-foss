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

package org.eclipse.tractusx.traceability.investigations.adapters.rest.validation;

import org.eclipse.tractusx.traceability.investigations.adapters.rest.model.UpdateInvestigationRequest;
import org.eclipse.tractusx.traceability.investigations.adapters.rest.model.UpdateInvestigationStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class UpdateInvestigationValidatorTest {

    @InjectMocks
    UpdateInvestigationValidator updateInvestigationValidator;

    @Mock
    UpdateInvestigationRequest mockRequest;


    @Test
    @DisplayName("No Validation Success for invalid Reason")
    void testUnsuccessfulValidationForInvalidReason() {

        UpdateInvestigationStatus acknowledged = UpdateInvestigationStatus.ACKNOWLEDGED;
        String reason = "some-reason-for-update";
        String errorMessage = "Update investigation reason can't be present for ACKNOWLEDGED status";

        UpdateInvestigationRequest request = new UpdateInvestigationRequest(acknowledged, reason);
        UpdateInvestigationValidationException exception = assertThrows(UpdateInvestigationValidationException.class, () -> UpdateInvestigationValidator.validate(request));
        assertEquals(errorMessage, exception.getMessage());

    }

    @Test
    @DisplayName("Execute Validation successfully")
    void testSuccessfulValidation() {
        UpdateInvestigationStatus accepted = UpdateInvestigationStatus.ACCEPTED;
        UpdateInvestigationRequest request = new UpdateInvestigationRequest(accepted, "abcdefg12313212321123");
        UpdateInvestigationValidator.validate(request);

    }
}
