package it.epicode.auth;


import it.epicode.entity.AppUser;
import it.epicode.entity.Role;
import it.epicode.service.AppUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AuthRunner implements ApplicationRunner {

    @Autowired private AppUserService appUserService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        createAdminUser();
    }

    private void createAdminUser() {
        Optional<AppUser> adminUser = appUserService.findByUsername("admin");
        if (adminUser.isEmpty()) {
            appUserService.registerUser("admin", "vincenzomoussa@gmail.com", "adminpwd", Role.ROLE_ADMIN);
            System.out.println("✅ Utente Admin creato con successo!");
        }
    }

}


