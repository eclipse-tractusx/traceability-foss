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

package org.eclipse.tractusx.traceability.investigations.adapters.rest.validation;

import org.eclipse.tractusx.traceability.investigations.adapters.rest.model.UpdateInvestigationRequest;
import org.eclipse.tractusx.traceability.investigations.domain.model.InvestigationStatus;

import java.util.Set;

import static org.eclipse.tractusx.traceability.investigations.domain.model.InvestigationStatus.ACCEPTED;
import static org.eclipse.tractusx.traceability.investigations.domain.model.InvestigationStatus.ACKNOWLEDGED;
import static org.eclipse.tractusx.traceability.investigations.domain.model.InvestigationStatus.DECLINED;

public class UpdateInvestigationValidator {

	private static final Set<InvestigationStatus> ALLOWED_STATUSES = Set.of(ACKNOWLEDGED, ACCEPTED, DECLINED);

	private static final int MINIMUM_REASON_CHARACTERS_SIZE = 15;
	private static final int MAXIMUM_REASON_CHARACTERS_SIZE = 1000;

	private UpdateInvestigationValidator() {
	}

	public static void validate(UpdateInvestigationRequest updateInvestigationRequest) {
		InvestigationStatus status = updateInvestigationRequest.status();

		if (!ALLOWED_STATUSES.contains(status)) {
			throw new UpdateInvestigationValidationException("%s not allowed for update investigation with".formatted(status));
		}

		String reason = updateInvestigationRequest.reason();

		if (status == ACKNOWLEDGED) {
			if (reason != null && !reason.isBlank()) {
				throw new UpdateInvestigationValidationException("Update investigation reason can't be present for %s status".formatted(ACKNOWLEDGED));
			}
		}

		if (status != ACKNOWLEDGED) {
			if (reason == null || reason.isBlank()) {
				throw new UpdateInvestigationValidationException("Update investigation reason must be present");
			}

			if (reason.length() < MINIMUM_REASON_CHARACTERS_SIZE || reason.length() > MAXIMUM_REASON_CHARACTERS_SIZE) {
				throw new UpdateInvestigationValidationException("Close reason should have at least %d characters and at most %d characters".formatted(MINIMUM_REASON_CHARACTERS_SIZE, MAXIMUM_REASON_CHARACTERS_SIZE));
			}
		}
	}
}
