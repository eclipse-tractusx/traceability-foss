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

import net.catenax.traceability.assets.domain.model.Asset;
import net.catenax.traceability.assets.domain.ports.AssetRepository;
import net.catenax.traceability.common.model.BPN;
import net.catenax.traceability.investigations.domain.model.AffectedPart;
import net.catenax.traceability.investigations.domain.model.Investigation;
import net.catenax.traceability.investigations.domain.model.InvestigationId;
import net.catenax.traceability.investigations.domain.model.InvestigationStatus;
import net.catenax.traceability.investigations.domain.model.Notification;
import net.catenax.traceability.investigations.domain.ports.InvestigationsRepository;
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

	public InvestigationsPublisherService(NotificationsService notificationsService,
										  InvestigationsRepository repository,
										  InvestigationsReadService investigationsReadService,
										  AssetRepository assetRepository, Clock clock) {
		this.notificationsService = notificationsService;
		this.repository = repository;
		this.investigationsReadService = investigationsReadService;
		this.assetRepository = assetRepository;
		this.clock = clock;
	}

	public InvestigationId startInvestigation(BPN bpn, List<String> assetIds, String description) {
		Investigation investigation = Investigation.startInvestigation(clock.instant(), bpn, description);

		Map<String, List<Asset>> assetsByManufacturer = assetRepository.getAssetsById(assetIds).stream().collect(Collectors.groupingBy(Asset::getManufacturerId));

		assetsByManufacturer.entrySet().stream()
			.map(it -> new Notification(
				UUID.randomUUID().toString(),
				null,
				bpn.value(),
				it.getKey(),
				null,
				null,
				description,
				InvestigationStatus.RECEIVED,
				it.getValue().stream().map(Asset::getId).map(AffectedPart::new).collect(Collectors.toList())
			)).forEach(investigation::addNotification);

		return repository.save(investigation);
	}

	public void cancelInvestigation(BPN bpn, Long id) {
		InvestigationId investigationId = new InvestigationId(id);

		Investigation investigation = investigationsReadService.loadInvestigation(investigationId);

		investigation.cancel(bpn);

		repository.update(investigation);
	}

	public void approveInvestigation(BPN bpn, Long id) {
		InvestigationId investigationId = new InvestigationId(id);

		Investigation investigation = investigationsReadService.loadInvestigation(investigationId);

		investigation.approve(bpn);

		repository.update(investigation);

		investigation.getNotifications().forEach(notificationsService::updateAsync);
	}

	public void closeInvestigation(BPN bpn, Long id, String reason) {
		InvestigationId investigationId = new InvestigationId(id);

		Investigation investigation = investigationsReadService.loadInvestigation(investigationId);

		investigation.close(bpn, reason);

		repository.update(investigation);

		investigation.getNotifications().forEach(notificationsService::updateAsync);
	}
}
