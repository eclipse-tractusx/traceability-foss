package net.catenax.traceability

import com.icegreen.greenmail.spring.GreenMailBean
import org.springframework.beans.factory.annotation.Autowired

import javax.mail.internet.MimeMessage

trait MailboxSupport {

    @Autowired
    GreenMailBean greenMailBean

    MailboxAssertion assertMailbox() {
        new MailboxAssertion(greenMailBean)
    }

    static class MailboxAssertion {

        private final GreenMailBean greenMailBean

        private MailboxAssertion(GreenMailBean greenMailBean) {
            this.greenMailBean = greenMailBean
        }

        MailboxAssertion hasTotalSize(int size) {
            assert greenMailBean.getReceivedMessages().size() == size
            this
        }

        MailboxAssertion hasRecipient(String recipient, int index = 0) {
            MimeMessage[] messages = greenMailBean.getReceivedMessages()
            assert index < messages.size()
            assert messages[index].getHeader("To").contains(recipient)
            this
        }

        MailboxAssertion hasMessage(String message, int index = 0) {
            MimeMessage[] messages = greenMailBean.getReceivedMessages()
            assert index < messages.size()
            assert messages[index].getContent() == "${message}\r\n"
            this
        }

        MailboxAssertion hasSubject(String subject, int index = 0) {
            MimeMessage[] messages = greenMailBean.getReceivedMessages()
            assert index < messages.size()
            assert messages[index].getHeader("Subject").contains(subject)
            this
        }
    }
}