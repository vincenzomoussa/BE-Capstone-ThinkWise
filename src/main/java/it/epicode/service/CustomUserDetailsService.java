package it.epicode.service;


import it.epicode.entity.AppUser;
import it.epicode.repository.AppUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private AppUserRepository appUserRepository;

    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        //Cerchiamo l'utente sia per email che per username
        AppUser appUser = appUserRepository.findByEmail(usernameOrEmail)
                .or(() -> appUserRepository.findByUsername(usernameOrEmail))
                .orElseThrow(() -> new UsernameNotFoundException("Utente non trovato: " + usernameOrEmail));

        //Convertiamo l'utente in un oggetto UserDetails
        return User.builder()
                .username(appUser.getEmail())  // 👈 Usiamo l'email come "username"
                .password(appUser.getPassword())  // 🔐 Password già hashata nel database
                .roles(appUser.getRoles().stream()
                        .map(role -> role.name().replace("ROLE_", "")) //Rimuove "ROLE_" dal nome
                        .toArray(String[]::new))
                .build();
    }
}

