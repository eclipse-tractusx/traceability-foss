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

package org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.validators;

import jakarta.validation.ConstraintValidatorContext;
import org.eclipse.tractusx.traceability.common.model.BPN;
import org.eclipse.tractusx.traceability.common.properties.TraceabilityProperties;
import org.eclipse.tractusx.traceability.notification.domain.notification.exception.NotificationSenderAndReceiverBPNEqualException;
import org.eclipse.tractusx.traceability.notification.infrastructure.edc.model.EDCNotification;
import org.eclipse.tractusx.traceability.notification.domain.notification.exception.InvestigationReceiverBpnMismatchException;
import org.eclipse.tractusx.traceability.notification.infrastructure.edc.EDCNotificationValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EDCNotificationValidatorTest {


    @Mock
    TraceabilityProperties traceabilityProperties;

    @Mock
    ConstraintValidatorContext context;

    @Mock
    EDCNotification edcNotification;

    @InjectMocks
    EDCNotificationValidator validator;

    @Test
    void testIsValidWithNullEDCNotification() {
        // Given
        EDCNotification edcNotification = null;

        // When
        boolean result = validator.isValid(edcNotification, context);

        // Then
        assertTrue(result);

    }

    @Test
    void testReceiverIsApplicationOwner() {
        // Given
        when(traceabilityProperties.getBpn()).thenReturn(BPN.of("BPN_OF_APPLICATION"));
        when(edcNotification.getSenderBPN()).thenReturn("OTHER");
        when(edcNotification.getRecipientBPN()).thenReturn("BPN_OF_APPLICATION");

        // When
        boolean result = validator.isValid(edcNotification, context);

        // Then
        assertTrue(result);
    }

    @Test
    void testSenderAndReceiverSameBPNException() {
        String bpn = "APPLICATION_BON";
        // Given
        when(traceabilityProperties.getBpn()).thenReturn(BPN.of(bpn));
        when(edcNotification.getSenderBPN()).thenReturn(bpn);
        when(edcNotification.getRecipientBPN()).thenReturn(bpn);
        when(edcNotification.getNotificationId()).thenReturn("123");
        assertThrows(NotificationSenderAndReceiverBPNEqualException.class, () -> validator.isValid(edcNotification, context));
    }

    @Test
    void testThrowsExceptionNoBpnMatch() {
        // Given
        when(traceabilityProperties.getBpn()).thenReturn(BPN.of("BPN_OF_APPLICATION"));
        when(edcNotification.getSenderBPN()).thenReturn("BPN_OF_SENDER");
        when(edcNotification.getRecipientBPN()).thenReturn("OTHER");
        // When
        // Then
        assertThrows(InvestigationReceiverBpnMismatchException.class, () -> validator.isValid(edcNotification, context));
    }

}


