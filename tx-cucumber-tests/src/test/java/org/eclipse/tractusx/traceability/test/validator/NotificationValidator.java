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

package org.eclipse.tractusx.traceability.test.validator;

import lombok.Getter;
import notification.response.NotificationResponse;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.eclipse.tractusx.traceability.test.validator.NotificationValidator.SupportedFields.ACCEPT_REASON;
import static org.eclipse.tractusx.traceability.test.validator.NotificationValidator.SupportedFields.AFFECTED_PART_ID;
import static org.eclipse.tractusx.traceability.test.validator.NotificationValidator.SupportedFields.ASSET_ID_COUNT;
import static org.eclipse.tractusx.traceability.test.validator.NotificationValidator.SupportedFields.CHANNEL;
import static org.eclipse.tractusx.traceability.test.validator.NotificationValidator.SupportedFields.CLOSE_REASON;
import static org.eclipse.tractusx.traceability.test.validator.NotificationValidator.SupportedFields.CREATED_BY;
import static org.eclipse.tractusx.traceability.test.validator.NotificationValidator.SupportedFields.DECLINE_REASON;
import static org.eclipse.tractusx.traceability.test.validator.NotificationValidator.SupportedFields.DESCRIPTION;
import static org.eclipse.tractusx.traceability.test.validator.NotificationValidator.SupportedFields.SEND_TO;
import static org.eclipse.tractusx.traceability.test.validator.NotificationValidator.SupportedFields.SEVERITY;
import static org.eclipse.tractusx.traceability.test.validator.NotificationValidator.SupportedFields.STATUS;
import static org.eclipse.tractusx.traceability.test.validator.NotificationValidator.SupportedFields.TARGET_DATE;
import static org.eclipse.tractusx.traceability.test.validator.NotificationValidator.SupportedFields.TITLE;
import static org.eclipse.tractusx.traceability.test.validator.NotificationValidator.SupportedFields.TYPE;

public class NotificationValidator {

    private final static String UNSUPPORTED_VALIDATION_MESSAGE = "Test validation for following fields %s is not supported currently supported fields are %s. Request developers to implement missing validation :)";

    public static void assertHasFields(NotificationResponse notification, Map<String, String> fieldsToCheck) {
        checkIfMapHasSupportedValidationRequest(fieldsToCheck);

        if (fieldsToCheck.containsKey(STATUS.getFieldName())) {
            assertThat(notification.getStatus().name())
                    .isEqualTo(fieldsToCheck.get(STATUS.getFieldName()));
        }

        if (fieldsToCheck.containsKey(DESCRIPTION.getFieldName())) {
            assertThat(TestUtils.unWrapStringWithTimestamp(notification.getDescription()))
                    .isEqualTo(fieldsToCheck.get(DESCRIPTION.getFieldName()));
        }

        if (fieldsToCheck.containsKey(CHANNEL.getFieldName())) {
            assertThat(notification.getChannel().name())
                    .isEqualTo(fieldsToCheck.get(CHANNEL.getFieldName()));
        }

        if (fieldsToCheck.containsKey(CLOSE_REASON.getFieldName())) {
            assertThat(notification.latestMessage().get().getMessage()).isEqualTo(fieldsToCheck.get(CLOSE_REASON.getFieldName()));
        }

        if (fieldsToCheck.containsKey(ACCEPT_REASON.getFieldName())) {
            assertThat(notification.latestMessage().get().getMessage()).isEqualTo(fieldsToCheck.get(ACCEPT_REASON.getFieldName()));

        }

        if (fieldsToCheck.containsKey(DECLINE_REASON.getFieldName())) {
            assertThat(notification.latestMessage().get().getMessage()).isEqualTo(fieldsToCheck.get(DECLINE_REASON.getFieldName()));
        }

        if (fieldsToCheck.containsKey(SEVERITY.getFieldName())) {
            assertThat(notification.getSeverity().getRealName())
                    .isEqualTo(fieldsToCheck.get(SEVERITY.getFieldName()));
        }

        if (fieldsToCheck.containsKey(TARGET_DATE.getFieldName())) {
            if (fieldsToCheck.get(TARGET_DATE.getFieldName()).isEmpty()) {
                assertThat(notification.getTargetDate())
                        .isNull();

            } else {
                assertThat(notification.getTargetDate())
                        .isEqualTo(fieldsToCheck.get(TARGET_DATE.getFieldName()));
            }
        }

        if (fieldsToCheck.containsKey(CREATED_BY.getFieldName())) {
            assertThat(notification.getCreatedBy())
                    .isEqualTo(fieldsToCheck.get(CREATED_BY.getFieldName()));
        }

        if (fieldsToCheck.containsKey(SEND_TO.getFieldName())) {
            assertThat(notification.getSendTo())
                    .isEqualTo(fieldsToCheck.get(SEND_TO.getFieldName()));
        }

        if (fieldsToCheck.containsKey(ASSET_ID_COUNT.getFieldName())) {
            assertThat(notification.getAssetIds()).hasSize(Integer.parseInt(fieldsToCheck.get(ASSET_ID_COUNT.getFieldName())));
        }

        if (fieldsToCheck.containsKey(TYPE.getFieldName())) {
            assertThat(notification.getType().name())
                    .isEqualTo(fieldsToCheck.get(TYPE.getFieldName()));
        }

        if (fieldsToCheck.containsKey(TITLE.getFieldName())) {
            assertThat(notification.getTitle())
                    .isEqualTo(fieldsToCheck.get(TITLE.getFieldName()));
        }

        if (fieldsToCheck.containsKey(AFFECTED_PART_ID.getFieldName())) {
            assertThat(notification.getAssetIds()).contains(fieldsToCheck.get(AFFECTED_PART_ID.getFieldName()));
        }
    }

    private static void checkIfMapHasSupportedValidationRequest(Map<String, String> fieldsToCheck) {
        final List<String> supportedFieldValidations = Arrays.stream(SupportedFields.values())
                .map(SupportedFields::getFieldName)
                .toList();
        final List<String> unsupportedFields = fieldsToCheck.keySet().stream()
                .filter(fieldName ->
                        !supportedFieldValidations
                                .contains(fieldName))
                .toList();
        if (!unsupportedFields.isEmpty()) {
            throw new UnsupportedOperationException(
                    UNSUPPORTED_VALIDATION_MESSAGE.formatted(unsupportedFields, supportedFieldValidations)
            );
        }
    }

    @Getter
    enum SupportedFields {
        STATUS("status"),
        DESCRIPTION("description"),
        CHANNEL("channel"),
        CLOSE_REASON("closeReason"),
        ACCEPT_REASON("acceptReason"),
        DECLINE_REASON("declineReason"),
        SEVERITY("severity"),
        CREATED_BY("createdBy"),
        SEND_TO("sendTo"),
        ASSET_ID_COUNT("assetIdCount"),
        TYPE("type"),
        TITLE("title"),
        AFFECTED_PART_ID("affectedPartId"),
        TARGET_DATE("targetDate");

        private final String fieldName;

        SupportedFields(String fieldName) {
            this.fieldName = fieldName;
        }
    }
}
