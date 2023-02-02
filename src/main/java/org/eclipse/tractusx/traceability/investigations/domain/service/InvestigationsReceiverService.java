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

package org.eclipse.tractusx.traceability.investigations.domain.service;

import org.eclipse.tractusx.traceability.common.model.BPN;
import org.eclipse.tractusx.traceability.common.properties.TraceabilityProperties;
import org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.model.EDCNotification;
import org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.model.NotificationType;
import org.eclipse.tractusx.traceability.investigations.domain.model.Investigation;
import org.eclipse.tractusx.traceability.investigations.domain.model.InvestigationId;
import org.eclipse.tractusx.traceability.investigations.domain.model.InvestigationStatus;
import org.eclipse.tractusx.traceability.investigations.domain.model.Notification;
import org.eclipse.tractusx.traceability.investigations.domain.model.exception.InvestigationIllegalUpdate;
import org.eclipse.tractusx.traceability.investigations.domain.model.exception.InvestigationReceiverBpnMismatchException;
import org.eclipse.tractusx.traceability.investigations.domain.ports.InvestigationsRepository;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.util.UUID;

@Component
public class InvestigationsReceiverService {

	private final InvestigationsRepository repository;
	private final InvestigationsReadService investigationsReadService;
	private final NotificationsService notificationsService;
	private final TraceabilityProperties traceabilityProperties;
	private final Clock clock;

	public InvestigationsReceiverService(InvestigationsRepository repository,
										 InvestigationsReadService investigationsReadService,
										 NotificationsService notificationsService,
										 TraceabilityProperties traceabilityProperties,
										 Clock clock) {
		this.repository = repository;
		this.investigationsReadService = investigationsReadService;
		this.notificationsService = notificationsService;
		this.traceabilityProperties = traceabilityProperties;
		this.clock = clock;
	}

	public void handle(EDCNotification edcNotification) {
		BPN recipientBPN = BPN.of(edcNotification.getRecipientBPN());
		BPN applicationBPN = traceabilityProperties.getBpn();

		if (!applicationBPN.equals(recipientBPN)) {
			throw new InvestigationReceiverBpnMismatchException(applicationBPN, recipientBPN, edcNotification.getNotificationId());
		}

		if (!edcNotification.getClassification().equals(NotificationType.QMINVESTIGATION)) {
			throw new InvestigationIllegalUpdate("Received %s classified edc notification which is not an investigation".formatted(edcNotification.getClassification()));
		}

		switch (edcNotification.getStatus()) {
			case APPROVED -> receiveInvestigation(edcNotification, recipientBPN);
			case CLOSED -> closeInvestigation(edcNotification);
			default -> throw new InvestigationIllegalUpdate("Failed to handle notification due to unhandled %s status".formatted(edcNotification.getStatus()));
		}
	}

	private void receiveInvestigation(EDCNotification edcNotification, BPN bpn) {
		Investigation investigation = Investigation.receiveInvestigation(clock.instant(), bpn, edcNotification.getInformation());

		Notification notification = new Notification(
			UUID.randomUUID().toString(),
			edcNotification.getNotificationId(),
			edcNotification.getSenderBPN(),
			edcNotification.getRecipientBPN(),
			edcNotification.getSenderAddress(),
			null,
			edcNotification.getInformation(),
			InvestigationStatus.RECEIVED,
			edcNotification.getListOfAffectedItems()
		);

		investigation.addNotification(notification);

		repository.save(investigation);
	}

	private void closeInvestigation(EDCNotification edcNotification) {
		Investigation investigation = investigationsReadService.loadInvestigationByNotificationReferenceId(edcNotification.getNotificationId());

		investigation.close(traceabilityProperties.getBpn(), edcNotification.getInformation());

		repository.update(investigation);
	}

	public void updateInvestigation(BPN bpn, Long investigationIdRaw, InvestigationStatus status, String reason) {
		Investigation investigation = investigationsReadService.loadInvestigation(new InvestigationId(investigationIdRaw));

		switch (status) {
			case ACKNOWLEDGED -> investigation.acknowledge(bpn);
			case ACCEPTED -> investigation.accept(bpn, reason);
			case DECLINED -> investigation.decline(bpn, reason);
			default -> throw new InvestigationIllegalUpdate("Can't update %s investigation with %s status".formatted(investigationIdRaw, status));
		}

		repository.update(investigation);

		// TODO EDC communication
	}
}
