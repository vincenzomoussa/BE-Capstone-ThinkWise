package it.epicode.repository;


import it.epicode.entity.Corso;
import it.epicode.entity.Livello;
import it.epicode.entity.TipoCorso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CorsoRepository extends JpaRepository<Corso, Long> {

    List<Corso> findByAttivoTrue();
    List<Corso> findByAttivoFalse();
    List<Corso> findByCorsoTipoAndLivelloAndAttivoTrue(String corsoTipo, Livello livello);
    List<Corso> findByInsegnanteIdAndAttivoTrue(Long insegnanteId);
    List<Corso> findByGiornoAndOrarioAndAttivoTrue(String giorno, String orario);
    List<Corso> findByAulaIdAndGiornoAndOrarioAndAttivoTrue(Long aulaId, String giorno, String orario);
    List<Corso> findByTipoCorso(TipoCorso tipoCorso);
    List<Corso> findByTipoCorsoAndAttivoTrue(TipoCorso tipoCorso);
    List<Corso> findByTipoCorsoAndAttivo(TipoCorso tipoCorso, boolean attivo);
    List<Corso> findByInsegnanteId(Long insegnanteId);
}