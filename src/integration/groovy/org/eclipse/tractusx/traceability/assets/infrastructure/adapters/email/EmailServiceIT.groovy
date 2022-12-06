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

package org.eclipse.tractusx.traceability.assets.infrastructure.adapters.email


import org.eclipse.tractusx.traceability.common.support.MailboxSupport
import org.eclipse.tractusx.traceability.IntegrationSpecification
import org.springframework.beans.factory.annotation.Autowired

class EmailServiceIT extends IntegrationSpecification implements MailboxSupport {

    @Autowired
    EmailService emailService

    def "should send email"() {
        given:
            String subject = "Test Subject"
            String message = "Test Message"
            String recipient = "test@test.com"

        when:
            emailService.sendMail(recipient, subject, message)

        then:
            assertMailbox()
                .hasTotalSize(1)
                .hasRecipient(recipient)
                .hasSubject(subject)
				.hasMessage()
					.withContentType("multipart/alternative")
					.withContentType("image/png")
					.withContent("text/plain", message)
					.withContent("text/html", message)
    }
}
