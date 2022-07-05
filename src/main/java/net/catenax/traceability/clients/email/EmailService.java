package net.catenax.traceability.clients.email;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    private final String from;

	private final SpringTemplateEngine templateEngine;

	private final Resource dividerLineImage;

	private final Resource questionMarkImage;

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

		thymeleafContext.setVariable("content", content.replaceAll("\\n", "<br/>"));

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
