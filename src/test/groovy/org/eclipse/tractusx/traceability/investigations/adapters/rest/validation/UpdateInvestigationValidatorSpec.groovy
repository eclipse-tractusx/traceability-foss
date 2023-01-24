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

package org.eclipse.tractusx.traceability.investigations.adapters.rest.validation

import org.eclipse.tractusx.traceability.investigations.adapters.rest.model.UpdateInvestigationRequest
import org.thymeleaf.util.StringUtils
import spock.lang.Specification
import spock.lang.Unroll

import static org.eclipse.tractusx.traceability.investigations.domain.model.InvestigationStatus.ACCEPTED
import static org.eclipse.tractusx.traceability.investigations.domain.model.InvestigationStatus.ACKNOWLEDGED
import static org.eclipse.tractusx.traceability.investigations.domain.model.InvestigationStatus.APPROVED
import static org.eclipse.tractusx.traceability.investigations.domain.model.InvestigationStatus.CANCELED
import static org.eclipse.tractusx.traceability.investigations.domain.model.InvestigationStatus.CLOSED
import static org.eclipse.tractusx.traceability.investigations.domain.model.InvestigationStatus.CREATED
import static org.eclipse.tractusx.traceability.investigations.domain.model.InvestigationStatus.DECLINED
import static org.eclipse.tractusx.traceability.investigations.domain.model.InvestigationStatus.RECEIVED
import static org.eclipse.tractusx.traceability.investigations.domain.model.InvestigationStatus.SENT

class UpdateInvestigationValidatorSpec extends Specification {

	@Unroll
	def "should not pass validation for unsupported #status status"() {
		given:
			def request = new UpdateInvestigationRequest(status, "some-reason")

		when:
			UpdateInvestigationValidator.validate(request)

		then:
			def exception = thrown(UpdateInvestigationValidationException)

		and:
			exception.message == "$status not allowed for update investigation with"

		where:
			status << [CREATED, APPROVED, SENT, RECEIVED, CLOSED, CANCELED]
	}

	@Unroll
	def "should not pass validation for invalid #reason reason with #status status"() {
		given:
			def request = new UpdateInvestigationRequest(status, reason)

		when:
			UpdateInvestigationValidator.validate(request)

		then:
			def exception = thrown(UpdateInvestigationValidationException)

		and:
			exception.message == errorMessage

		where:
			status       | reason                   | errorMessage
			ACKNOWLEDGED | "some-reason-for-update" | "Update investigation reason can't be present for ACKNOWLEDGED status"
			ACCEPTED     | null                     | "Update investigation reason must be present"
			ACCEPTED     | ""                       | "Update investigation reason must be present"
			ACCEPTED     | "     "                  | "Update investigation reason must be present"
			ACCEPTED     | generateString(14)       | "Close reason should have at least 15 characters and at most 1000 characters"
			DECLINED     | generateString(1001)     | "Close reason should have at least 15 characters and at most 1000 characters"
	}

	def "should pass validation"() {
		given:
			def request = new UpdateInvestigationRequest(status, reason)

		when:
			UpdateInvestigationValidator.validate(request)

		then:
			noExceptionThrown()

		where:
			status       | reason
			ACKNOWLEDGED | null
			ACKNOWLEDGED | ""
			ACCEPTED     | generateString(15)
			DECLINED     | generateString(1000)
			ACCEPTED     | generateString(16)
			DECLINED     | generateString(999)
	}

	private String generateString(int length) {
		return StringUtils.repeat("*", length)
	}
}
