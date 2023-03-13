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

import org.eclipse.tractusx.traceability.common.mapper.InvestigationMapper;
import org.eclipse.tractusx.traceability.common.mapper.NotificationMapper;
import org.eclipse.tractusx.traceability.common.model.BPN;
import org.eclipse.tractusx.traceability.common.properties.TraceabilityProperties;
import org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.model.EDCNotification;
import org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.model.NotificationType;
import org.eclipse.tractusx.traceability.infrastructure.jpa.investigation.InvestigationEntity;
import org.eclipse.tractusx.traceability.investigations.adapters.rest.model.InvestigationData;
import org.eclipse.tractusx.traceability.investigations.domain.model.*;
import org.eclipse.tractusx.traceability.investigations.domain.model.exception.InvestigationIllegalUpdate;
import org.eclipse.tractusx.traceability.investigations.domain.model.exception.InvestigationReceiverBpnMismatchException;
import org.eclipse.tractusx.traceability.investigations.domain.ports.InvestigationsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.util.List;

@Component
public class InvestigationsReceiverService {

	private final InvestigationsRepository repository;
	private final InvestigationsReadService investigationsReadService;
	private final NotificationMapper notificationMapper;
	private final InvestigationMapper investigationMapper;
	private final TraceabilityProperties traceabilityProperties;
	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	private final NotificationsService notificationsService;


	public InvestigationsReceiverService(InvestigationsRepository repository,
										 InvestigationsReadService investigationsReadService,
										 NotificationMapper notificationMapper, NotificationsService notificationsService, InvestigationMapper investigationMapper, TraceabilityProperties traceabilityProperties) {

		this.repository = repository;
		this.investigationsReadService = investigationsReadService;
		this.notificationMapper = notificationMapper;
		this.investigationMapper = investigationMapper;
		this.traceabilityProperties = traceabilityProperties;
		this.notificationsService = notificationsService;
	}

	public void handleNotificationReceiverCallback(EDCNotification edcNotification) {
		logger.info("Received notification response with id {}", edcNotification.getNotificationId());

		BPN recipientBPN = BPN.of(edcNotification.getRecipientBPN());

		validateNotificationReceiverCallback(edcNotification);

		InvestigationStatus investigationStatus = edcNotification.convertInvestigationStatus();

		switch (investigationStatus) {
			case SENT -> receiveInvestigation(edcNotification, recipientBPN);
			case ACKNOWLEDGED -> receiveUpdateInvestigation(edcNotification, InvestigationStatus.ACKNOWLEDGED);
			case ACCEPTED -> receiveUpdateInvestigation(edcNotification, InvestigationStatus.ACCEPTED);
			case CLOSED -> closeInvestigation(edcNotification);
			default -> throw new InvestigationIllegalUpdate("Failed to handle notification due to unhandled %s status".formatted(investigationStatus));
		}
	}

	private void validateNotificationReceiverCallback(EDCNotification edcNotification) {
		NotificationType notificationType = edcNotification.convertNotificationType();

		if (!notificationType.equals(NotificationType.QMINVESTIGATION)) {
			throw new InvestigationIllegalUpdate("Received %s classified edc notification which is not an investigation".formatted(notificationType));
		}
	}

	private void receiveInvestigation(EDCNotification edcNotification, BPN bpn) {
		logger.info("receiveInvestigation");
		Notification notification = notificationMapper.toReceiverNotification(edcNotification, InvestigationStatus.RECEIVED);
		Investigation investigation = investigationMapper.toReceiverInvestigation(bpn, edcNotification.getInformation(), notification);
		repository.save(investigation);
	}

	private void receiveUpdateInvestigation(EDCNotification edcNotification, InvestigationStatus investigationStatus) {
		logger.info("receiveUpdateInvestigation with status {}", investigationStatus);
		Notification notification = notificationMapper.toReceiverNotification(edcNotification, investigationStatus);
		logger.info("receiveUpdateInvestigation notification with status {}", notification);

		Investigation investigation = investigationsReadService.loadInvestigationByNotificationId(edcNotification.getRelatedNotificationId());
		logger.info("receiveUpdateInvestigation investigation with status {}", investigation);
		investigation.addNotification(notification);

		InvestigationId savedInvestigation = repository.update(investigation);

		logger.info("Stored received notification in investigation {}", savedInvestigation);
	}

	private void closeInvestigation(EDCNotification edcNotification) {
		logger.info("closeInvestigation");
		Investigation investigation = investigationsReadService.loadInvestigationByNotificationId(edcNotification.getNotificationId());
		investigation.close(traceabilityProperties.getBpn(), edcNotification.getInformation());
		repository.update(investigation);
	}

	public void updateInvestigationPublisher(BPN applicationBpn, Long investigationIdRaw, InvestigationStatus status, String reason) {
		Investigation investigation = investigationsReadService.loadInvestigation(new InvestigationId(investigationIdRaw));
		List<Notification> invalidNotifications = invalidNotifications(investigation, applicationBpn);

		if (!invalidNotifications.isEmpty()) {
			StringBuilder builder = new StringBuilder("Investigation receiverBpnNumber mismatch for notifications with IDs: ");
			for (Notification notification : invalidNotifications) {
				builder.append(notification.getId()).append(", ");
			}
			builder.delete(builder.length() - 2, builder.length()); // Remove the last ", " from the string
			throw new InvestigationReceiverBpnMismatchException(builder.toString());
		}


		switch (status) {
			case ACKNOWLEDGED -> investigation.acknowledge();
			case ACCEPTED -> investigation.accept(reason);
			case DECLINED -> investigation.decline(reason);
			default -> throw new InvestigationIllegalUpdate("Can't update %s investigation with %s status".formatted(investigationIdRaw, status));
		}

		repository.update(investigation);

		final boolean isReceiver = investigation.getInvestigationSide().equals(InvestigationSide.RECEIVER);
		String side = "";
		if (investigation.getInvestigationSide() != null) {
			side = investigation.getInvestigationSide().name();
		} else {
			side = "not set";
		}
		logger.info("updateInvestigationPublisher with investigation {}", investigation);

		investigation.getNotifications().forEach(notification -> notificationsService.updateAsync(notification, isReceiver));
	}

	private List<Notification> invalidNotifications(final Investigation investigation, final BPN applicationBpn) {
		final String applicationBpnValue = applicationBpn.value();
		return investigation.getNotifications().stream()
			.filter(notification -> !notification.getReceiverBpnNumber().equals(applicationBpnValue)).toList();
	}


}
