package it.epicode.controller;


import it.epicode.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/email")
public class EmailTestController {

	@Autowired
	private EmailService emailService;

	@PostMapping("/send")
	public String sendTestEmail(@RequestParam String to) {
		String subject = "Test di invio email";
		String text = "<h1>Email di test</h1><p>Questa è un'email di prova dal sistema.</p>";

		emailService.sendEmail(to, subject, text);
		return "Email inviata con successo a " + to;
	}
}
