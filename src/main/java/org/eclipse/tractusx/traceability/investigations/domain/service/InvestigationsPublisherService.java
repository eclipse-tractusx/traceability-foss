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

import org.eclipse.tractusx.traceability.assets.domain.model.Asset;
import org.eclipse.tractusx.traceability.assets.domain.ports.AssetRepository;
import org.eclipse.tractusx.traceability.common.model.BPN;
import org.eclipse.tractusx.traceability.investigations.domain.model.*;
import org.eclipse.tractusx.traceability.investigations.domain.ports.InvestigationsRepository;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class InvestigationsPublisherService {

	private final NotificationsService notificationsService;
	private final InvestigationsRepository repository;
	private final InvestigationsReadService investigationsReadService;
	private final AssetRepository assetRepository;
	private final Clock clock;


	public InvestigationsPublisherService(NotificationsService notificationsService, InvestigationsRepository repository, InvestigationsReadService investigationsReadService, AssetRepository assetRepository, Clock clock) {
		this.notificationsService = notificationsService;
		this.repository = repository;
		this.investigationsReadService = investigationsReadService;
		this.assetRepository = assetRepository;
		this.clock = clock;
	}

	/**
	 * Starts a new investigation with the given BPN, asset IDs and description.
	 *
	 * @param applicationBpn the BPN to use for the investigation
	 * @param assetIds       the IDs of the assets to investigate
	 * @param description    the description of the investigation
	 * @return the ID of the newly created investigation
	 */
	public InvestigationId startInvestigation(BPN applicationBpn, List<String> assetIds, String description) {
		Investigation investigation = Investigation.startInvestigation(clock.instant(), applicationBpn, description);

		Map<String, List<Asset>> assetsByManufacturer = assetRepository.getAssetsById(assetIds).stream().collect(Collectors.groupingBy(Asset::getManufacturerId));

		assetsByManufacturer.entrySet().stream().map(it -> new Notification(
			UUID.randomUUID().toString(),
			null,
			applicationBpn.value(),
			it.getKey(),
			null,
			null,
			description,
			InvestigationStatus.RECEIVED,
			it.getValue().stream().map(Asset::getId).map(AffectedPart::new).collect(Collectors.toList())
		)).forEach(investigation::addNotification);

		return repository.save(investigation);
	}

	/**
	 * Cancels an ongoing investigation with the given BPN and ID.
	 *
	 * @param applicationBpn the BPN associated with the investigation
	 * @param id             the ID of the investigation to cancel
	 */
	public void cancelInvestigation(BPN applicationBpn, Long id) {
		InvestigationId investigationId = new InvestigationId(id);
		Investigation investigation = investigationsReadService.loadInvestigation(investigationId);
		investigation.cancel(applicationBpn);
		repository.update(investigation);
	}

	/**
	 * Sends an ongoing investigation with the given BPN and ID to the next stage.
	 *
	 * @param applicationBpn the BPN associated with the investigation
	 * @param id             the ID of the investigation to send
	 */
	public void sendInvestigation(BPN applicationBpn, Long id) {
		InvestigationId investigationId = new InvestigationId(id);
		Investigation investigation = investigationsReadService.loadInvestigation(investigationId);
		investigation.send(applicationBpn);
		repository.update(investigation);
		investigation.getNotifications().forEach(notificationsService::updateAsync);
	}

	/**
	 * Closes an ongoing investigation with the given BPN, ID and reason.
	 *
	 * @param applicationBpn the BPN associated with the investigation
	 * @param id             the ID of the investigation to close
	 * @param reason         the reason for closing the investigation
	 */
	public void closeInvestigation(BPN applicationBpn, Long id, String reason) {
		InvestigationId investigationId = new InvestigationId(id);
		Investigation investigation = investigationsReadService.loadInvestigation(investigationId);
		investigation.close(applicationBpn, reason);
		repository.update(investigation);
		investigation.getNotifications().forEach(notificationsService::updateAsync);
	}
}
