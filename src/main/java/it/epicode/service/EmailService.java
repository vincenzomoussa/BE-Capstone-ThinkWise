package it.epicode.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

	@Autowired private JavaMailSender mailSender;

	public void sendEmail(String to, String subject, String text) {
		try {
			MimeMessage message = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, true);

			helper.setTo(to);
			helper.setSubject(subject);
			helper.setText(text, true);

			mailSender.send(message);
			System.out.println("Email inviata con successo a " + to);
		} catch (MessagingException e) {
			System.err.println("Errore nell'invio dell'email: " + e.getMessage());
		}
	}


	public void sendEmailWithAttachment(String to, String subject, String body, byte[] attachment, String filename) {
		try {
			MimeMessage message = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, true);

			helper.setTo(to);
			helper.setSubject(subject);
			helper.setText(body);
			helper.addAttachment(filename, new ByteArrayResource(attachment));

			mailSender.send(message);
			System.out.println("Email inviata con successo a " + to);

		} catch (MessagingException e) {
			throw new RuntimeException("Errore nell'invio dell'email", e);
		}
	}
}
