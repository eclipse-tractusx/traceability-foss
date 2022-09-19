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

package net.catenax.traceability.assets.infrastructure.adapters.email;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.regex.Pattern;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    private final String from;

	private final SpringTemplateEngine templateEngine;

	private final Resource dividerLineImage;

	private final Resource questionMarkImage;

	private static final Pattern NEW_LINE_PATTERN = Pattern.compile("\\n");

    public EmailService(
		JavaMailSender mailSender,
		SpringTemplateEngine templateEngine,
		@Value("${spring.mail.username}") String from,
		@Value("classpath:/mail-templates/divider_lines.png") Resource dividerLineImage,
		@Value("classpath:/mail-templates/question_mark_icon.png") Resource questionMarkImage
	) {
        this.mailSender = mailSender;
        this.from = from;
		this.templateEngine = templateEngine;
		this.dividerLineImage = dividerLineImage;
		this.questionMarkImage = questionMarkImage;
	}

    public void sendMail(String to, String subject, String content) throws MessagingException {
		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
		Context thymeleafContext = new Context();

		String updatedContent = NEW_LINE_PATTERN.matcher(content).replaceAll("<br/>");
		thymeleafContext.setVariable("content", updatedContent);

		String htmlBody = templateEngine.process("basic.html", thymeleafContext);
		String textBody = templateEngine.process("basic.txt", thymeleafContext);

        helper.setFrom(from);
        helper.setTo(to);
        helper.setSubject(subject);
		helper.setText(textBody, htmlBody);

		helper.addInline("divider_lines.png", dividerLineImage);
		helper.addInline("question_mark_icon.png", questionMarkImage);

        mailSender.send(message);
    }
}
