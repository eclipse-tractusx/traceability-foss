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

package org.eclipse.tractusx.traceability.qualitynotification.investigation.rest.validation;

import org.eclipse.tractusx.traceability.qualitynotification.application.validation.UpdateQualityNotificationValidationException;
import org.eclipse.tractusx.traceability.qualitynotification.application.validation.UpdateQualityNotificationValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import qualitynotification.base.request.UpdateQualityNotificationRequest;
import qualitynotification.base.request.UpdateQualityNotificationStatusRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class UpdateQualityNotificationValidatorTest {

    @InjectMocks
    UpdateQualityNotificationValidator updateInvestigationValidator;


    @Test
    @DisplayName("No Validation Success for invalid Reason")
    void testUnsuccessfulValidationForInvalidReason() {

        UpdateQualityNotificationStatusRequest acknowledged = UpdateQualityNotificationStatusRequest.ACKNOWLEDGED;
        String reason = "some-reason-for-update";
        String errorMessage = "Update investigation reason can't be present for ACKNOWLEDGED status";

        UpdateQualityNotificationRequest request = new UpdateQualityNotificationRequest();
        request.setReason(reason);
        request.setStatus(acknowledged);
        UpdateQualityNotificationValidationException exception = assertThrows(UpdateQualityNotificationValidationException.class, () -> UpdateQualityNotificationValidator.validate(request));
        assertEquals(errorMessage, exception.getMessage());

    }

    @Test
    @DisplayName("Execute Validation successfully")
    void testSuccessfulValidation() {
        UpdateQualityNotificationStatusRequest accepted = UpdateQualityNotificationStatusRequest.ACCEPTED;
        UpdateQualityNotificationRequest request = new UpdateQualityNotificationRequest();
        request.setReason("abcdefg12313212321123");
        request.setStatus(accepted);
        UpdateQualityNotificationValidator.validate(request);

    }
}
