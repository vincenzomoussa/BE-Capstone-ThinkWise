package it.epicode.repository;


import it.epicode.entity.Spesa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface SpesaRepository extends JpaRepository<Spesa, Long> {
	List<Spesa> findByDataSpesaBetween(LocalDate startDate, LocalDate endDate);
}
