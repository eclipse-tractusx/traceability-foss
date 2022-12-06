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
package org.eclipse.tractusx.traceability.infrastructure.edc.blackbox;

import org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.model.EDCNotification;
import org.eclipse.tractusx.traceability.investigations.domain.service.InvestigationsReceiverService;
import org.eclipse.tractusx.traceability.common.config.FeatureFlags;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

@Profile(FeatureFlags.NOTIFICATIONS_ENABLED_PROFILES)
@ApiIgnore
@RestController
public class EdcController {
	private static final Logger logger = LoggerFactory.getLogger(EdcController.class);

	private final InvestigationsReceiverService investigationsReceiverService;

	public EdcController(InvestigationsReceiverService investigationsReceiverService) {
		this.investigationsReceiverService = investigationsReceiverService;
	}

	/**
	 * Receiver API call for EDC Transfer
	 */
	@PostMapping("/qualitynotifications/receive")
	public void qualityNotificationReceive(@RequestBody EDCNotification edcNotification) {
		logger.info("EdcController [qualityNotificationReceive] notificationId:{}", edcNotification);

		investigationsReceiverService.handle(edcNotification);
	}
}

