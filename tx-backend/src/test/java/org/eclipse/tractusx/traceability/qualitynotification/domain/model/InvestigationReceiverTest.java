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

package org.eclipse.tractusx.traceability.qualitynotification.domain.model;

import org.eclipse.tractusx.traceability.common.model.BPN;
import org.eclipse.tractusx.traceability.qualitynotification.domain.investigation.model.Investigation;
import org.eclipse.tractusx.traceability.qualitynotification.domain.investigation.model.InvestigationId;
import org.eclipse.tractusx.traceability.qualitynotification.domain.investigation.model.InvestigationSide;
import org.eclipse.tractusx.traceability.qualitynotification.domain.investigation.model.InvestigationStatus;
import org.eclipse.tractusx.traceability.qualitynotification.domain.investigation.model.Notification;
import org.eclipse.tractusx.traceability.qualitynotification.domain.investigation.model.exception.InvestigationStatusTransitionNotAllowed;
import org.eclipse.tractusx.traceability.testdata.NotificationTestDataFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.ArrayList;
import java.util.stream.Stream;

import static org.eclipse.tractusx.traceability.qualitynotification.domain.investigation.model.InvestigationStatus.ACCEPTED;
import static org.eclipse.tractusx.traceability.qualitynotification.domain.investigation.model.InvestigationStatus.ACKNOWLEDGED;
import static org.eclipse.tractusx.traceability.qualitynotification.domain.investigation.model.InvestigationStatus.CANCELED;
import static org.eclipse.tractusx.traceability.qualitynotification.domain.investigation.model.InvestigationStatus.CLOSED;
import static org.eclipse.tractusx.traceability.qualitynotification.domain.investigation.model.InvestigationStatus.CREATED;
import static org.eclipse.tractusx.traceability.qualitynotification.domain.investigation.model.InvestigationStatus.DECLINED;
import static org.eclipse.tractusx.traceability.qualitynotification.domain.investigation.model.InvestigationStatus.RECEIVED;
import static org.eclipse.tractusx.traceability.qualitynotification.domain.investigation.model.InvestigationStatus.SENT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class InvestigationReceiverTest {

    Investigation investigation;

    private static Stream<Arguments> provideInvalidStatusForAcknowledgeInvestigation() {
        return Stream.of(
                Arguments.of(CLOSED),
                Arguments.of(DECLINED),
                Arguments.of(ACCEPTED),
                Arguments.of(CANCELED),
                Arguments.of(CREATED)

        );
    }

    private static Stream<Arguments> provideInvalidStatusForAcceptInvestigation() {
        return Stream.of(
                Arguments.of(CREATED),
                Arguments.of(CANCELED),
                Arguments.of(DECLINED),
                Arguments.of(RECEIVED),
                Arguments.of(SENT),
                Arguments.of(CLOSED)
        );
    }

    private static Stream<Arguments> provideInvalidStatusForDeclineInvestigation() {
        return Stream.of(
                Arguments.of(CLOSED),
                Arguments.of(CANCELED),
                Arguments.of(DECLINED),
                Arguments.of(ACCEPTED),
                Arguments.of(RECEIVED),
                Arguments.of(SENT),
                Arguments.of(CREATED)
        );
    }

    private static Stream<Arguments> provideInvalidStatusForCloseInvestigation() {
        return Stream.of(
                Arguments.of(CLOSED),
                Arguments.of(CANCELED)
        );
    }

    private static Stream<Arguments> provideInvalidStatusForSendInvestigation() {
        return Stream.of(
                Arguments.of(CLOSED),
                Arguments.of(CANCELED),
                Arguments.of(DECLINED),
                Arguments.of(ACCEPTED),
                Arguments.of(ACKNOWLEDGED),
                Arguments.of(RECEIVED),
                Arguments.of(SENT)
        );
    }

    private static Stream<Arguments> provideInvalidStatusForCancelInvestigation() {
        return Stream.of(
                Arguments.of(CANCELED),
                Arguments.of(CLOSED),
                Arguments.of(DECLINED),
                Arguments.of(ACCEPTED),
                Arguments.of(ACKNOWLEDGED),
                Arguments.of(RECEIVED),
                Arguments.of(SENT)
        );
    }

    @ParameterizedTest
    @DisplayName("Forbid Acknowledge Investigation with invalid status")
    @MethodSource("provideInvalidStatusForAcknowledgeInvestigation")
    void forbidAcknowledgeInvestigationWithStatusClosed(InvestigationStatus status) {
        // Given
        investigation = receiverInvestigationWithStatus(status);
        Notification notification = testNotification();
        assertThrows(InvestigationStatusTransitionNotAllowed.class, () -> investigation.acknowledge(notification));

        assertEquals(status, investigation.getInvestigationStatus());

    }

    @ParameterizedTest
    @DisplayName("Forbid Accept Investigation with invalid status")
    @MethodSource("provideInvalidStatusForAcceptInvestigation")
    void forbidAcceptInvestigationWithDisallowedStatus(InvestigationStatus status) {
        Notification notification = testNotification();

        investigation = receiverInvestigationWithStatus(status);

        assertThrows(InvestigationStatusTransitionNotAllowed.class, () -> investigation.accept("some reason", notification));

        assertEquals(status, investigation.getInvestigationStatus());

    }

    @ParameterizedTest
    @DisplayName("Forbid Decline Investigation with invalid status")
    @MethodSource("provideInvalidStatusForDeclineInvestigation")
    void forbidDeclineInvestigationWithInvalidStatus(InvestigationStatus status) {
        investigation = receiverInvestigationWithStatus(status);
        Notification notification = testNotification();
        assertThrows(InvestigationStatusTransitionNotAllowed.class, () -> investigation.decline("some-reason", notification));

        assertEquals(status, investigation.getInvestigationStatus());

    }

    @ParameterizedTest
    @DisplayName("Forbid Close Investigation with invalid status")
    @MethodSource("provideInvalidStatusForCloseInvestigation")
    void forbidCloseInvestigationWithInvalidStatus(InvestigationStatus status) {
        investigation = receiverInvestigationWithStatus(status);
        BPN bpn = new BPN("BPNL000000000001");
        assertThrows(InvestigationStatusTransitionNotAllowed.class, () -> investigation.close(bpn, "some-reason"));

        assertEquals(status, investigation.getInvestigationStatus());

    }

    @ParameterizedTest
    @DisplayName("Forbid Send Investigation with invalid status")
    @MethodSource("provideInvalidStatusForSendInvestigation")
    void forbidSendInvestigationWithInvalidStatus(InvestigationStatus status) {

        investigation = receiverInvestigationWithStatus(status);
        BPN bpn = new BPN("BPNL000000000001");

        assertThrows(InvestigationStatusTransitionNotAllowed.class, () -> investigation.send(bpn));
        assertEquals(status, investigation.getInvestigationStatus());

    }

    @ParameterizedTest
    @DisplayName("Forbid Cancel Investigation with invalid statuses")
    @MethodSource("provideInvalidStatusForCancelInvestigation")
    void forbidCancelInvestigationWithInvalidStatus(InvestigationStatus status) {
        Investigation investigation = receiverInvestigationWithStatus(status);
        BPN bpn = new BPN("BPNL000000000001");
        assertThrows(InvestigationStatusTransitionNotAllowed.class, () -> investigation.cancel(bpn));

        assertEquals(status, investigation.getInvestigationStatus());
    }


    @Test
    @DisplayName("Acknowledge Investigation successfully")
    void acknowledgeInvestigationSuccessfully() {
        Notification notification = testNotification();
        investigation = receiverInvestigationWithStatus(RECEIVED);
        investigation.acknowledge(notification);
        assertEquals(ACKNOWLEDGED, investigation.getInvestigationStatus());

    }

    @Test
    @DisplayName("Accept Investigation successfully")
    void acceptInvestigationSuccessfully() {
        Notification notification = testNotification();
        investigation = receiverInvestigationWithStatus(ACKNOWLEDGED);
        investigation.accept("some reason", notification);
        assertEquals(ACCEPTED, investigation.getInvestigationStatus());
        assertEquals(ACCEPTED, notification.getInvestigationStatus());
    }

    @Test
    @DisplayName("Decline Investigation successfully")
    void declineInvestigationSuccessfully() {
        Notification notification = testNotification();
        investigation = receiverInvestigationWithStatus(ACKNOWLEDGED);
        investigation.decline("some reason", notification);
        assertEquals(DECLINED, investigation.getInvestigationStatus());
        assertEquals(DECLINED, notification.getInvestigationStatus());
    }

    //util functions
    private Investigation receiverInvestigationWithStatus(InvestigationStatus status) {
        return investigationWithStatus(status, InvestigationSide.RECEIVER);
    }

    private Investigation investigationWithStatus(InvestigationStatus status, InvestigationSide side) {
        BPN bpn = new BPN("BPNL000000000001");
        return new Investigation(new InvestigationId(1L), bpn, status, side, "", "", "", "", Instant.now(), new ArrayList<>(), new ArrayList<>());
    }

    private Notification testNotification() {
        return NotificationTestDataFactory.createNotificationTestData();
    }
}
