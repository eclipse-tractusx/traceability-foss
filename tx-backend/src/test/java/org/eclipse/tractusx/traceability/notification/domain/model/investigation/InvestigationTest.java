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

package org.eclipse.tractusx.traceability.notification.domain.model.investigation;

import org.eclipse.tractusx.traceability.common.model.BPN;
import org.eclipse.tractusx.traceability.notification.domain.base.model.Notification;
import org.eclipse.tractusx.traceability.notification.domain.base.model.NotificationId;
import org.eclipse.tractusx.traceability.notification.domain.base.model.NotificationMessage;
import org.eclipse.tractusx.traceability.notification.domain.base.model.NotificationSide;
import org.eclipse.tractusx.traceability.notification.domain.base.model.NotificationStatus;
import org.eclipse.tractusx.traceability.notification.domain.notification.exception.InvestigationIllegalUpdate;
import org.eclipse.tractusx.traceability.notification.domain.notification.exception.InvestigationStatusTransitionNotAllowed;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.stream.Stream;

import static org.eclipse.tractusx.traceability.notification.domain.base.model.NotificationStatus.ACCEPTED;
import static org.eclipse.tractusx.traceability.notification.domain.base.model.NotificationStatus.ACKNOWLEDGED;
import static org.eclipse.tractusx.traceability.notification.domain.base.model.NotificationStatus.CANCELED;
import static org.eclipse.tractusx.traceability.notification.domain.base.model.NotificationStatus.CLOSED;
import static org.eclipse.tractusx.traceability.notification.domain.base.model.NotificationStatus.CREATED;
import static org.eclipse.tractusx.traceability.notification.domain.base.model.NotificationStatus.DECLINED;
import static org.eclipse.tractusx.traceability.notification.domain.base.model.NotificationStatus.RECEIVED;
import static org.eclipse.tractusx.traceability.notification.domain.base.model.NotificationStatus.SENT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class InvestigationTest {

    Notification investigation;
    @Mock
    NotificationMessage notificationMessage;

    @Test
    @DisplayName("Forbid Cancel Investigation with disallowed status")
    void forbidCancellingInvestigationWithDisallowedStatus() {
        NotificationStatus status = RECEIVED;
        BPN bpn = new BPN("BPNL000000000001");
        investigation = senderInvestigationWithStatus(bpn, status);
        assertThrows(InvestigationStatusTransitionNotAllowed.class, () -> investigation.cancel(bpn));
        assertEquals(status, investigation.getNotificationStatus());
    }

    @Test
    @DisplayName("Forbid Send Investigation with disallowed status")
    void forbidSendingInvestigationWithDisallowedStatus() {
        NotificationStatus status = SENT;
        BPN bpn = new BPN("BPNL000000000001");
        investigation = senderInvestigationWithStatus(bpn, status);
        assertThrows(InvestigationStatusTransitionNotAllowed.class, () -> investigation.send(bpn));
        assertEquals(status, investigation.getNotificationStatus());
    }

    @Test
    @DisplayName("Forbid Close Investigation for different BPN")
    void forbidCloseInvestigationWithDisallowedStatus() {
        NotificationStatus status = CREATED;
        BPN bpn = new BPN("BPNL000000000001");
        BPN bpnOther = new BPN("BPNL12321321321");
        investigation = senderInvestigationWithStatus(bpnOther, status);
        assertThrows(InvestigationIllegalUpdate.class, () -> investigation.close(bpn, "some-reason", notificationMessage));
        assertEquals(status, investigation.getNotificationStatus());
    }

    @Test
    @DisplayName("Forbid Cancel Investigation for different BPN")
    void forbidCancelInvestigationForDifferentBpn() {
        NotificationStatus status = CREATED;
        BPN bpn = new BPN("BPNL000000000001");
        investigation = senderInvestigationWithStatus(bpn, status);
        BPN bpn2 = new BPN("BPNL000000000002");
        assertThrows(InvestigationIllegalUpdate.class, () -> investigation.cancel(bpn2));
        assertEquals(status, investigation.getNotificationStatus());
    }

    @Test
    @DisplayName("Forbid Send Investigation for different BPN")
    void forbidSendInvestigationForDifferentBpn() {
        NotificationStatus status = CREATED;
        BPN bpn = new BPN("BPNL000000000001");
        investigation = senderInvestigationWithStatus(bpn, status);
        BPN bpn2 = new BPN("BPNL000000000002");
        assertThrows(InvestigationIllegalUpdate.class, () -> investigation.send(bpn2));
        assertEquals(status, investigation.getNotificationStatus());
    }

    @Test
    @DisplayName("Forbid Close Investigation for different BPN")
    void forbidCloseInvestigationForDifferentBpn() {
        NotificationStatus status = CREATED;
        BPN bpn = new BPN("BPNL000000000001");
        investigation = senderInvestigationWithStatus(bpn, status);
        BPN bpn2 = new BPN("BPNL000000000002");
        assertThrows(InvestigationIllegalUpdate.class, () -> investigation.close(bpn2, "some reason", notificationMessage));
        assertEquals(status, investigation.getNotificationStatus());
    }

    @Test
    @DisplayName("Send Investigation status")
    void sendInvestigationSuccessfully() {
        BPN bpn = new BPN("BPNL000000000001");
        investigation = senderInvestigationWithStatus(bpn, CREATED);
        investigation.send(bpn);
        assertEquals(SENT, investigation.getNotificationStatus());
    }

    @Test
    @DisplayName("Cancel Investigation status")
    void cancelInvestigationSuccessfully() {
        BPN bpn = new BPN("BPNL000000000001");
        investigation = senderInvestigationWithStatus(bpn, CREATED);
        investigation.cancel(bpn);
        assertEquals(CANCELED, investigation.getNotificationStatus());
    }

    @Test
    @DisplayName("Close Investigation with allowed status")
    void closeInvestigationWithAllowedStatusSuccessfully() {
        BPN bpn = new BPN("BPNL000000000001");
        investigation = senderInvestigationWithStatus(bpn, SENT);
        investigation.close(bpn, "some-reason", notificationMessage);
        assertEquals(CLOSED, investigation.getNotificationStatus());
    }


    // util functions
    private Notification senderInvestigationWithStatus(BPN bpn, NotificationStatus status) {
        return investigationWithStatus(bpn, status, NotificationSide.SENDER);
    }

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

    private Notification investigationWithStatus(BPN bpn, NotificationStatus status, NotificationSide side) {

        return Notification.builder()
                .notificationId(new NotificationId(1L))
                .bpn(bpn)
                .notificationStatus(status)
                .notificationSide(side)
                .createdAt(Instant.now())
                .build();

        // return new Investigation(new InvestigationId(1L), bpn, status, side, "", "", "", "", Instant.now(), new ArrayList<>(), new ArrayList<>());
    }

    @ParameterizedTest
    @DisplayName("Forbid Acknowledge Investigation with invalid status")
    @MethodSource("provideInvalidStatusForAcknowledgeInvestigation")
    void forbidAcknowledgeInvestigationWithStatusClosed(NotificationStatus status) {
        // Given
        investigation = receiverInvestigationWithStatus(status);

        assertThrows(InvestigationStatusTransitionNotAllowed.class, () -> investigation.acknowledge());

        assertEquals(status, investigation.getNotificationStatus());

    }

    @ParameterizedTest
    @DisplayName("Forbid Accept Investigation with invalid status")
    @MethodSource("provideInvalidStatusForAcceptInvestigation")
    void forbidAcceptInvestigationWithDisallowedStatus(NotificationStatus status) {
        investigation = receiverInvestigationWithStatus(status);
        assertThrows(InvestigationStatusTransitionNotAllowed.class, () -> investigation.accept("some reason", notificationMessage));
        assertEquals(status, investigation.getNotificationStatus());

    }

    @ParameterizedTest
    @DisplayName("Forbid Decline Investigation with invalid status")
    @MethodSource("provideInvalidStatusForDeclineInvestigation")
    void forbidDeclineInvestigationWithInvalidStatus(NotificationStatus status) {
        investigation = receiverInvestigationWithStatus(status);
        assertThrows(InvestigationStatusTransitionNotAllowed.class, () -> investigation.decline("some-reason", notificationMessage));
        assertEquals(status, investigation.getNotificationStatus());
    }

    @ParameterizedTest
    @DisplayName("Forbid Close Investigation with invalid status")
    @MethodSource("provideInvalidStatusForCloseInvestigation")
    void forbidCloseInvestigationWithInvalidStatus(NotificationStatus status) {
        investigation = receiverInvestigationWithStatus(status);
        BPN bpn = new BPN("BPNL000000000001");
        assertThrows(InvestigationStatusTransitionNotAllowed.class, () -> investigation.close(bpn, "some-reason", notificationMessage));

        assertEquals(status, investigation.getNotificationStatus());

    }

    @ParameterizedTest
    @DisplayName("Forbid Send Investigation with invalid status")
    @MethodSource("provideInvalidStatusForSendInvestigation")
    void forbidSendInvestigationWithInvalidStatus(NotificationStatus status) {

        investigation = receiverInvestigationWithStatus(status);
        BPN bpn = new BPN("BPNL000000000001");

        assertThrows(InvestigationStatusTransitionNotAllowed.class, () -> investigation.send(bpn));
        assertEquals(status, investigation.getNotificationStatus());

    }

    @ParameterizedTest
    @DisplayName("Forbid Cancel Investigation with invalid statuses")
    @MethodSource("provideInvalidStatusForCancelInvestigation")
    void forbidCancelInvestigationWithInvalidStatus(NotificationStatus status) {
        Notification investigation = receiverInvestigationWithStatus(status);
        BPN bpn = new BPN("BPNL000000000001");
        assertThrows(InvestigationStatusTransitionNotAllowed.class, () -> investigation.cancel(bpn));

        assertEquals(status, investigation.getNotificationStatus());
    }


    @Test
    @DisplayName("Acknowledge Investigation successfully")
    void acknowledgeInvestigationSuccessfully() {
        investigation = receiverInvestigationWithStatus(RECEIVED);
        investigation.acknowledge();
        assertEquals(ACKNOWLEDGED, investigation.getNotificationStatus());

    }

    @Test
    @DisplayName("Accept Investigation successfully")
    void acceptInvestigationSuccessfully() {
        investigation = receiverInvestigationWithStatus(ACKNOWLEDGED);
        investigation.accept("some reason", notificationMessage);
        assertEquals(ACCEPTED, investigation.getNotificationStatus());
    }

    @Test
    @DisplayName("Decline Investigation successfully")
    void declineInvestigationSuccessfully() {
        investigation = receiverInvestigationWithStatus(ACKNOWLEDGED);
        investigation.decline("some reason", notificationMessage);
        assertEquals(DECLINED, investigation.getNotificationStatus());
    }

    //util functions
    private Notification receiverInvestigationWithStatus(NotificationStatus status) {
        return investigationWithStatus(status, NotificationSide.RECEIVER);
    }

    private Notification investigationWithStatus(NotificationStatus status, NotificationSide side) {
        BPN bpn = new BPN("BPNL000000000001");
        return Notification.builder()
                .notificationId(new NotificationId(1L))
                .bpn(bpn)
                .notificationStatus(status)
                .notificationSide(side)
                .build();
    }
}

