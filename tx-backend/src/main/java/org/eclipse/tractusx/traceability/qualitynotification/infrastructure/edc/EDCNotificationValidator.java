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

package org.eclipse.tractusx.traceability.qualitynotification.infrastructure.edc;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.eclipse.tractusx.traceability.common.model.BPN;
import org.eclipse.tractusx.traceability.common.properties.TraceabilityProperties;
import org.eclipse.tractusx.traceability.qualitynotification.infrastructure.edc.model.EDCNotification;
import org.eclipse.tractusx.traceability.qualitynotification.domain.investigation.model.exception.InvestigationReceiverBpnMismatchException;
import org.springframework.stereotype.Component;

/**
 * A validator for {@link EDCNotification} objects annotated with {@link ValidEDCNotification}.
 * This validator ensures that the sender BPN of the notification is eligible to handle the application BPN.
 * If the sender BPN is null or does not match the application BPN, an {@link InvestigationReceiverBpnMismatchException}
 * will be thrown.
 */
@Component
public class EDCNotificationValidator implements ConstraintValidator<ValidEDCNotification, EDCNotification> {
    private final TraceabilityProperties traceabilityProperties;


    public EDCNotificationValidator(TraceabilityProperties traceabilityProperties) {
        this.traceabilityProperties = traceabilityProperties;
    }

    /**
     * Initializes the validator.
     *
     * @param constraintAnnotation the annotation that this validator is validating
     */
    @Override
    public void initialize(ValidEDCNotification constraintAnnotation) {
        // No initialization needed
    }

    /**
     * Validates the specified {@link EDCNotification} object.
     *
     * @param edcNotification the {@link EDCNotification} object to validate
     * @param context         the {@link ConstraintValidatorContext} for creating validation messages
     * @return true if the validation succeeds
     * @throws InvestigationReceiverBpnMismatchException if the sender BPN is null or does not match the application BPN
     */
    @Override
    public boolean isValid(EDCNotification edcNotification, ConstraintValidatorContext context) {
        // Will not be handled in this validation
        if (edcNotification == null) {
            return true;
        }
        BPN applicationBPN = traceabilityProperties.getBpn();
        String senderBPN = edcNotification.getSenderBPN();
        String recipientBPN = edcNotification.getRecipientBPN();
        if (senderBPN == null) {
            throw new InvestigationReceiverBpnMismatchException("BPN of sender cannot be null.");
        }
        if (!senderBPN.equals(applicationBPN.value()) && !recipientBPN.equals(applicationBPN.value())) {
            final String senderBPNIsNotSameAsReceiverError = String.format("BPN {%s} is not eligible to handle BPN: {%s}", applicationBPN.value(), senderBPN);
            throw new InvestigationReceiverBpnMismatchException(senderBPNIsNotSameAsReceiverError);
        }

        return true;
    }


}
