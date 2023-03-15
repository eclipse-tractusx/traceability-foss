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

import org.eclipse.tractusx.traceability.assets.infrastructure.config.async.AssetsAsyncConfig;
import org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.InvestigationsEDCFacade;
import org.eclipse.tractusx.traceability.investigations.domain.model.Notification;
import org.eclipse.tractusx.traceability.investigations.domain.ports.EDCUrlProvider;
import org.eclipse.tractusx.traceability.investigations.domain.ports.InvestigationsRepository;
import static org.slf4j.LoggerFactory.getLogger;
import org.slf4j.Logger;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.util.List;

@Service
public class NotificationsService {

	private final InvestigationsEDCFacade edcFacade;
	private final InvestigationsRepository repository;
	private final EDCUrlProvider edcUrlProvider;
	private static final Logger logger = getLogger(MethodHandles.lookup().lookupClass());

	public NotificationsService(InvestigationsEDCFacade edcFacade, InvestigationsRepository repository, EDCUrlProvider edcUrlProvider) {
		this.edcFacade = edcFacade;
		this.repository = repository;
		this.edcUrlProvider = edcUrlProvider;
	}

	@Async(value = AssetsAsyncConfig.UPDATE_NOTIFICATION_EXECUTOR)
	public void updateAsync(Notification notification, boolean isReceiver) {
		String senderEdcUrl = edcUrlProvider.getSenderUrl();
		String receiverBpn;
		String senderBpn;
		if (isReceiver) {
			receiverBpn = notification.getSenderBpnNumber();
			senderBpn = notification.getReceiverBpnNumber();

		} else {
			receiverBpn = notification.getReceiverBpnNumber();
			senderBpn = notification.getSenderBpnNumber();
		}

		List<String> receiverEdcUrls = edcUrlProvider.getEdcUrls(receiverBpn);

		for (String receiverEdcUrl : receiverEdcUrls) {
			Notification notificationToSend = notification.copy(senderBpn, receiverBpn);
			logger.info("NotificationUpdate (NotificationService) id: {}, refId: {}", notificationToSend.getId(), notificationToSend.getNotificationReferenceId());
			edcFacade.startEDCTransfer(notificationToSend, receiverEdcUrl, senderEdcUrl);
			repository.update(notificationToSend);
		}
	}

	public void updateAsync(Notification notification) {
		updateAsync(notification, false);
	}
}
