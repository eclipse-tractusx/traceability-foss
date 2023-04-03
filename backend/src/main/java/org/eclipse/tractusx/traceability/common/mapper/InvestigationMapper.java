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
package org.eclipse.tractusx.traceability.common.mapper;

import org.eclipse.tractusx.traceability.common.model.BPN;
import org.eclipse.tractusx.traceability.investigations.domain.model.*;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.util.ArrayList;
import java.util.List;

@Component
public class InvestigationMapper {
	private final Clock clock;

	public InvestigationMapper(Clock clock) {
		this.clock = clock;
	}

	/**
	 * Creates an Investigation object representing the investigation received by the receiver for a given notification.
	 *
	 * @param bpn          the BPN of the investigation
	 * @param description  the description of the investigation
	 * @param notification the notification associated with the investigation
	 * @return an Investigation object representing the investigation received by the receiver
	 */
	public Investigation toInvestigation(BPN bpn, String description, Notification notification) {

		List<String> assetIds = new ArrayList<>();
		notification.getAffectedParts().stream()
			.map(AffectedPart::assetId)
			.forEach(assetIds::add);
		return new Investigation(
			null,
			bpn,
			InvestigationStatus.RECEIVED,
			InvestigationSide.RECEIVER,
			null,
			null,
			null,
			description,
			clock.instant(),
			assetIds,
			List.of(notification)
		);
	}
}
