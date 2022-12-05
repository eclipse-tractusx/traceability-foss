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

import org.eclipse.tractusx.traceability.investigations.domain.model.AffectedPart;
import org.eclipse.tractusx.traceability.investigations.domain.model.InvestigationStatus;
import org.eclipse.tractusx.traceability.investigations.domain.model.Notification;

import java.util.List;


public class EDCNotification {

	private String notificationId;
	private String senderBPN;
	private String senderAddress;
	private String recipientBPN;
	private String information;
	private List<AffectedPart> listOfAffectedItems;
	private InvestigationStatus status;

	private NotificationType classification;


	public EDCNotification() {
	}

	public EDCNotification(String senderEDC, Notification notification) {
		this.notificationId = notification.getId();
		this.senderBPN = notification.getSenderBpnNumber();
		this.senderAddress = senderEDC;
		this.recipientBPN = notification.getReceiverBpnNumber();
		this.information = notification.getDescription();
		this.listOfAffectedItems = notification.getAffectedParts();
		this.status = notification.getInvestigationStatus();
		this.classification = NotificationType.QMINVESTIGATION;
	}

	public String getNotificationId() {
		return notificationId;
	}

	public void setNotificationId(String notificationId) {
		this.notificationId = notificationId;
	}

	public String getSenderBPN() {
		return senderBPN;
	}

	public void setSenderBPN(String senderBPN) {
		this.senderBPN = senderBPN;
	}

	public String getSenderAddress() {
		return senderAddress;
	}

	public void setSenderAddress(String senderAddress) {
		this.senderAddress = senderAddress;
	}

	public String getRecipientBPN() {
		return recipientBPN;
	}

	public void setRecipientBPN(String recipientBPN) {
		this.recipientBPN = recipientBPN;
	}

	public String getInformation() {
		return information;
	}

	public void setInformation(String information) {
		this.information = information;
	}

	public InvestigationStatus getStatus() {
		return status;
	}

	public void setStatus(InvestigationStatus status) {
		this.status = status;
	}

	public List<AffectedPart> getListOfAffectedItems() {
		return listOfAffectedItems;
	}

	public void setListOfAffectedItems(List<AffectedPart> listOfAffectedItems) {
		this.listOfAffectedItems = listOfAffectedItems;
	}

	public NotificationType getClassification() {
		return classification;
	}

	public void setClassification(NotificationType classification) {
		this.classification = classification;
	}

}
