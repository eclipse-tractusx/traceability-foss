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
package org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import org.eclipse.tractusx.traceability.investigations.domain.model.AffectedPart;
import org.eclipse.tractusx.traceability.investigations.domain.model.InvestigationStatus;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record EDCNotification(@Valid
							  @NotNull
							  @Schema(description = "Header of the EDC notification",
								  implementation = EDCNotificationHeader.class) EDCNotificationHeader header,
							  @NotNull EDCNotificationContent content) {

	@JsonIgnore
	public String getRecipientBPN() {
		return header.recipientBPN();
	}

	@JsonIgnore
	public String getNotificationId() {
		return header.notificationId();
	}

	@JsonIgnore
	public String getSenderBPN() {
		return header.senderBPN();
	}

	@JsonIgnore
	public String getSenderAddress() {
		return header.senderAddress();
	}

	@JsonIgnore
	public String getInformation() {
		return content.information();
	}

	@JsonIgnore
	public List<AffectedPart> getListOfAffectedItems() {
		return content.listOfAffectedItems().stream()
			.map(AffectedPart::new)
			.collect(Collectors.toList());
	}

	public NotificationType convertNotificationType() {
		String classification = header().classification();

		return NotificationType.fromValue(classification)
			.orElseThrow(() -> new IllegalArgumentException("%s not supported notification type".formatted(classification)));
	}

	public InvestigationStatus convertInvestigationStatus() {
		String investigationStatus = header().status();

		return InvestigationStatus.fromValue(investigationStatus)
			.orElseThrow(() -> new IllegalArgumentException("%s not supported investigation status".formatted(investigationStatus)));
	}
}

