package it.epicode.repository;


import it.epicode.entity.Studente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


import java.util.List;

public interface StudenteRepository extends JpaRepository<Studente, Long> {

	@Query("SELECT s FROM Studente s WHERE s.corsi IS EMPTY")
	List<Studente> findStudentiSenzaCorso();

}

