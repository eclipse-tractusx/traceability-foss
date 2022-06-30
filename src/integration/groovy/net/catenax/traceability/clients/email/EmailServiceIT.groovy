package net.catenax.traceability.clients.email

import net.catenax.traceability.IntegrationSpec
import net.catenax.traceability.MailboxSupport
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
					.withContent("text/plain", message)
					.withContent("text/html", message)
    }
}
