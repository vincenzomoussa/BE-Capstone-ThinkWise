package it.epicode.service;


import it.epicode.auth.JwtTokenUtil;
import it.epicode.auth.LoginRequest;
import it.epicode.auth.LoginResponse;
import it.epicode.entity.AppUser;
import it.epicode.entity.Role;
import it.epicode.repository.AppUserRepository;
import jakarta.persistence.EntityExistsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Service
public class AppUserService {

    private static final Logger logger = LoggerFactory.getLogger(AppUserService.class);

    @Autowired private AppUserRepository appUserRepository;

    @Autowired private PasswordEncoder passwordEncoder;

    @Autowired private AuthenticationManager authenticationManager;

    @Autowired private JwtTokenUtil jwtTokenUtil;

    @Autowired private EmailService emailService;

    @Value("${spring.mail.username}")
    private String adminEmail;

    public AppUser registerUser(String username, String email, String password, Role role) {
        if (appUserRepository.existsByEmail(email)) {
            throw new EntityExistsException("Email già in uso");
        }

        AppUser appUser = new AppUser();
        appUser.setUsername(username);
        appUser.setEmail(email);
        appUser.setPassword(passwordEncoder.encode(password));
        appUser.setRoles(Set.of(role));


        appUserRepository.save(appUser);
        logger.info("Nuovo utente registrato: {}", username);


        try {
            String subject = "Benvenuto in Gestione Scuola!";
            String text = "<h1>Benvenuto, " + username + "!</h1><p>Il tuo account è stato creato con successo.</p>";
            emailService.sendEmail(email, subject, text);
            logger.info("Email di conferma inviata a {}", email);
        } catch (Exception e) {
            logger.error("Errore nell'invio dell'email a {}: {}", email, e.getMessage());
        }


        try {
            String adminNotification = "<h1>Nuovo utente registrato!</h1><p>Username: " + username + "</p><p>Email: " + email + "</p>";
            emailService.sendEmail(adminEmail, "Nuovo utente registrato", adminNotification);
            logger.info("Notifica admin inviata a {}", adminEmail);
        } catch (Exception e) {
            logger.error("Errore nell'invio della notifica all'admin {}: {}", adminEmail, e.getMessage());
        }

        return appUser;
    }

    public LoginResponse authenticate(LoginRequest loginRequest) {
        System.out.println("➡️ Tentativo di login con email: " + loginRequest.getEmail());


        AppUser user = appUserRepository.findByEmail(loginRequest.getEmail())
            .orElseThrow(() -> {
                System.out.println("❌ Utente non trovato per email: " + loginRequest.getEmail());
                return new UsernameNotFoundException("Utente non trovato");
            });

        System.out.println("🔑 Trovato utente: " + user.getEmail());


        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(user.getEmail(), loginRequest.getPassword())
        );

        System.out.println("✅ Autenticazione riuscita per: " + user.getEmail());

        String token = jwtTokenUtil.generateToken(user);
        System.out.println("🛡️ Token generato: " + token);


        return new LoginResponse(token, user.getId());
    }

    public Optional<AppUser> findByUsername(String username) {
        return appUserRepository.findByUsername(username);
    }
}
