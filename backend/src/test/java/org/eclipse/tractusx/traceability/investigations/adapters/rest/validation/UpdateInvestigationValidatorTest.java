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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.stream.Stream;

import static org.eclipse.tractusx.traceability.investigations.domain.model.InvestigationStatus.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UpdateInvestigationValidatorTest {

	@InjectMocks
	UpdateInvestigationValidator updateInvestigationValidator;

	@Mock
	UpdateInvestigationRequest mockRequest;

	@Test
	@DisplayName("No Validation Success for unsupported Status")
	void testUnsuccessfulValidationForUnsupportedStatus() {

		InvestigationStatus status = CREATED;
		UpdateInvestigationRequest request = new UpdateInvestigationRequest(status, "some-reason");


		UpdateInvestigationValidationException exception = org.junit.jupiter.api.Assertions.assertThrows(UpdateInvestigationValidationException.class, () -> UpdateInvestigationValidator.validate(request));
	}

	@Test
	@DisplayName("No Validation Success for invalid Reason")
	void testUnsuccessfulValidationForInvalidReason() {

		InvestigationStatus status = ACKNOWLEDGED;
		String reason = "some-reason-for-update";
		String errorMessage = "Update investigation reason can't be present for ACKNOWLEDGED status";

		UpdateInvestigationRequest request = new UpdateInvestigationRequest(status, reason);
		UpdateInvestigationValidationException exception = assertThrows(UpdateInvestigationValidationException.class, () -> UpdateInvestigationValidator.validate(request));
		assertEquals(errorMessage, exception.getMessage());

	}

	@Test
	@DisplayName("Execute Validation successfully")
	void testSuccessfulValidation() {

		UpdateInvestigationRequest request = new UpdateInvestigationRequest(ACKNOWLEDGED, null);
		UpdateInvestigationValidator.validate(request);

	}
}
