package it.epicode.repository;

import it.epicode.entity.Insegnante;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InsegnanteRepository extends JpaRepository<Insegnante, Long> {
}
