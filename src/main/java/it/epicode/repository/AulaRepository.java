package it.epicode.repository;


import it.epicode.entity.Aula;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AulaRepository extends JpaRepository<Aula, Long> {

	@Query("SELECT a FROM Aula a WHERE KEY(a.disponibilita) = :giorno AND a.disponibilita LIKE %:orario%")
	List<Aula> findAuleDisponibiliByGiornoEOrario(@Param("giorno") String giorno, @Param("orario") String orario);
}


