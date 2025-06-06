package it.epicode.repository;


import it.epicode.entity.Pagamento;
import it.epicode.entity.Studente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PagamentoRepository extends JpaRepository<Pagamento, Long> {

	List<Pagamento> findByStudenteId(Long studenteId);
	List<Pagamento> findByMensilitaSaldata(String mensilita);
	Optional<Pagamento> findByStudenteAndMensilitaSaldata(Studente studente, String mensilitaSaldata);

	@Query("SELECT COALESCE(SUM(p.importo), 0) FROM Pagamento p")
	double getTotalePagamenti();

}
