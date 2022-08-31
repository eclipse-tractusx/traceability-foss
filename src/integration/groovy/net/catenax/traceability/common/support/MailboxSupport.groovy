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

package net.catenax.traceability.common.support

import com.icegreen.greenmail.spring.GreenMailBean
import org.springframework.beans.factory.annotation.Autowired

import javax.mail.BodyPart
import javax.mail.internet.ContentType
import javax.mail.internet.MimeMessage
import javax.mail.internet.MimeMultipart

trait MailboxSupport {

	@Autowired
	GreenMailBean greenMailBean

	MailboxAssertion assertMailbox() {
		new MailboxAssertion(greenMailBean)
	}

	static class MultipartMessageAssertion {
		private final Set<String> contentTypes = new HashSet<>()

		private final Map<String, String> content = new HashMap<>()

		private final MailboxAssertion owner

		private final MimeMessage message

		MultipartMessageAssertion(MailboxAssertion owner, MimeMessage message) {
			this.owner = owner
			this.message = message
			if (message.getContent() instanceof MimeMultipart) {
				processMultipartMessage(message.getContent() as MimeMultipart)
			}
		}

		MultipartMessageAssertion withContentType(String... contentTypes) {
			assert this.contentTypes.containsAll(contentTypes)
			this
		}

		MultipartMessageAssertion withContent(String contentType, String expectedContent) {
			String content = this.content.get(contentType)
			assert content != null
			assert content.contains(expectedContent)
			this
		}

		MailboxAssertion and() {
			owner
		}

		private void processMultipartMessage(MimeMultipart mimeMultipart) {
			int count = mimeMultipart.getCount()

			for (int i = 0; i < count; i++) {
				BodyPart bodyPart = mimeMultipart.getBodyPart(i)
				processBodyPart(bodyPart)
			}
		}

		private void processBodyPart(BodyPart bodyPart) {
			ContentType contentType = new ContentType(bodyPart.getContentType())

			contentTypes.add(contentType.getBaseType())

			if (bodyPart.getContent() instanceof MimeMultipart) {
				processMultipartMessage((MimeMultipart) bodyPart.getContent())
			} else {
				content.put(contentType.getBaseType(), (String) bodyPart.getContent())
			}
		}
	}

	static class MailboxAssertion {

		private final GreenMailBean greenMailBean

		MailboxAssertion(GreenMailBean greenMailBean) {
			this.greenMailBean = greenMailBean
		}

		MailboxAssertion hasTotalSize(int size) {
			assert greenMailBean.getReceivedMessages().size() == size
			this
		}

		MailboxAssertion hasRecipient(String recipient) {
			MimeMessage[] messages = greenMailBean.getReceivedMessages()
			assert messages[0].getHeader("To").contains(recipient)
			this
		}


		MultipartMessageAssertion hasMessage() {
			MimeMessage[] messages = greenMailBean.getReceivedMessages()
			new MultipartMessageAssertion(this, messages[0])
		}

		MailboxAssertion hasSubject(String subject) {
			MimeMessage[] messages = greenMailBean.getReceivedMessages()
			assert messages[0].getHeader("Subject").contains(subject)
			this
		}
	}
}
