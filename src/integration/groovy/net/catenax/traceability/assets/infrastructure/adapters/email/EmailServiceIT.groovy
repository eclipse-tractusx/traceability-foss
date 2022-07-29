package net.catenax.traceability.assets.infrastructure.adapters.email

import net.catenax.traceability.IntegrationSpec
import net.catenax.traceability.common.support.MailboxSupport
import org.springframework.beans.factory.annotation.Autowired

class EmailServiceIT extends IntegrationSpec implements MailboxSupport {

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
