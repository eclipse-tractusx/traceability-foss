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

package net.catenax.traceability.investigations.domain.model;

import net.catenax.traceability.investigations.domain.model.exception.NotificationStatusTransitionNotAllowed;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.requireNonNullElseGet;

public class Notification {
	private final String id;
	private final String notificationReferenceId;
	private final String senderBpnNumber;
	private final String receiverBpnNumber;
	private String edcUrl;
	private String contractAgreementId;
	private final List<AffectedPart> affectedParts;
	private final String description;
	private InvestigationStatus investigationStatus;

	public Notification(String id,
						String notificationReferenceId,
						String senderBpnNumber,
						String receiverBpnNumber,
						String edcUrl,
						String contractAgreementId,
						String description,
						InvestigationStatus investigationStatus,
						List<AffectedPart> affectedParts) {
		this.id = id;
		this.notificationReferenceId = notificationReferenceId;
		this.senderBpnNumber = senderBpnNumber;
		this.receiverBpnNumber = receiverBpnNumber;
		this.edcUrl = edcUrl;
		this.contractAgreementId = contractAgreementId;
		this.description = description;
		this.investigationStatus = investigationStatus;

		this.affectedParts = requireNonNullElseGet(affectedParts, ArrayList::new);
	}

	void changeStatusTo(InvestigationStatus to) {
		boolean transitionAllowed = investigationStatus.transitionAllowed(to);

		if (!transitionAllowed) {
			throw new NotificationStatusTransitionNotAllowed(id, investigationStatus, to);
		}

		this.investigationStatus = to;
	}

	public String getId() {
		return id;
	}

	public String getNotificationReferenceId() {
		return notificationReferenceId;
	}

	public String getContractAgreementId() {
		return contractAgreementId;
	}

	public void setContractAgreementId(String contractAgreementId) {
		this.contractAgreementId = contractAgreementId;
	}

	public List<AffectedPart> getAffectedParts() {
		return affectedParts;
	}

	public String getSenderBpnNumber() {
		return senderBpnNumber;
	}

	public String getReceiverBpnNumber() {
		return receiverBpnNumber;
	}

	public void setEdcUrl(String edcUrl) {
		this.edcUrl = edcUrl;
	}

	public String getEdcUrl() {
		return edcUrl;
	}

	public String getDescription() {
		return description;
	}

	public InvestigationStatus getInvestigationStatus() {
		return investigationStatus;
	}
}
