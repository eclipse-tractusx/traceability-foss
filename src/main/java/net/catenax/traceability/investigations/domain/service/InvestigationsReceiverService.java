/********************************************************************************
 * Copyright (c) 2021,2022 Contributors to the CatenaX (ng) GitHub Organisation
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

package net.catenax.traceability.investigations.domain.service;

import net.catenax.traceability.common.model.BPN;
import net.catenax.traceability.common.properties.TraceabilityProperties;
import net.catenax.traceability.infrastructure.edc.blackbox.model.EDCNotification;
import net.catenax.traceability.infrastructure.edc.blackbox.model.NotificationType;
import net.catenax.traceability.investigations.domain.model.Investigation;
import net.catenax.traceability.investigations.domain.model.InvestigationStatus;
import net.catenax.traceability.investigations.domain.model.Notification;
import net.catenax.traceability.investigations.domain.model.exception.InvestigationIllegalUpdate;
import net.catenax.traceability.investigations.domain.model.exception.InvestigationReceiverBpnMismatchException;
import net.catenax.traceability.investigations.domain.ports.InvestigationsRepository;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.util.UUID;

@Component
public class InvestigationsReceiverService {

	private final InvestigationsRepository repository;
	private final InvestigationsReadService investigationsReadService;
	private final TraceabilityProperties traceabilityProperties;
	private final Clock clock;

	public InvestigationsReceiverService(InvestigationsRepository repository,
										 InvestigationsReadService investigationsReadService,
										 TraceabilityProperties traceabilityProperties,
										 Clock clock) {
		this.repository = repository;
		this.investigationsReadService = investigationsReadService;
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
}
