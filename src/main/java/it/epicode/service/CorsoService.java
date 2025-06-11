package it.epicode.service;


import it.epicode.dto.*;
import it.epicode.entity.*;
import it.epicode.repository.AulaRepository;
import it.epicode.repository.CorsoRepository;
import it.epicode.repository.InsegnanteRepository;
import it.epicode.repository.StudenteRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CorsoService {

	@Autowired private StudenteService studenteService;

	@Autowired private CorsoRepository corsoRepository;

	@Autowired private StudenteRepository studenteRepository;

	@Autowired private AulaRepository aulaRepository;

	@Autowired private InsegnanteRepository insegnanteRepository;

	private Map<Long, List<Studente>> listaDiAttesa = new HashMap<>();


	public List<CorsoResponseDTO> getTuttiICorsi() {
		return corsoRepository.findByAttivoTrue().stream()
			.map(this::convertToResponseDTO)
			.collect(Collectors.toList());
	}


	public List<CorsoResponseDTO> getCorsiByInsegnante(Long insegnanteId) {
		return corsoRepository.findByInsegnanteIdAndAttivoTrue(insegnanteId).stream()
			.map(this::convertToResponseDTO)
			.collect(Collectors.toList());
	}

	public CorsoResponseDTO getCorsoById(Long id) {
		Corso corso = corsoRepository.findById(id)
			.orElseThrow(() -> new EntityNotFoundException("Corso non trovato con ID: " + id));
		return convertToResponseDTO(corso);
	}



	public List<CorsoResponseDTO> getCorsiByGiornoEOrario(String giorno, String orario) {
		return corsoRepository.findByGiornoAndOrarioAndAttivoTrue(giorno, orario).stream()
			.map(this::convertToResponseDTO)
			.collect(Collectors.toList());
	}


	public List<CorsoResponseDTO> getCorsiByTipologiaELivello(String corsoTipo, Livello livello) {
		return corsoRepository.findByCorsoTipoAndLivelloAndAttivoTrue(corsoTipo, livello).stream()
			.map(this::convertToResponseDTO)
			.collect(Collectors.toList());
	}

	public CorsoResponseDTO creaCorso(CorsoRequestDTO request) {
		Optional<Aula> aulaOpt = aulaRepository.findById(request.getAulaId());
		if (aulaOpt.isEmpty()) {
			throw new EntityNotFoundException("Aula non trovata con ID: " + request.getAulaId());
		}
		Aula aula = aulaOpt.get();

		Optional<Insegnante> insegnanteOpt = insegnanteRepository.findById(request.getInsegnanteId());
		if (insegnanteOpt.isEmpty()) {
			throw new EntityNotFoundException("Insegnante non trovato con ID: " + request.getInsegnanteId());
		}
		Insegnante insegnante = insegnanteOpt.get();


		List<Corso> corsiEsistenti = corsoRepository.findByAulaIdAndGiornoAndOrarioAndAttivoTrue(
			aula.getId(), request.getGiorno(), request.getOrario());
		if (!corsiEsistenti.isEmpty()) {
			throw new IllegalStateException("L'aula è già occupata per il giorno " + request.getGiorno() +
				" alle " + request.getOrario());
		}

		Corso corso = new Corso();
		BeanUtils.copyProperties(request, corso);
		corso.setNome(request.getNome());
		corso.setTipoCorso(request.getTipoCorso());
		corso.setSecondoGiorno(request.getSecondoGiorno());
		corso.setSecondoOrario(request.getSecondoOrario());
		corso.setAula(aula);
		corso.setInsegnante(insegnante);
		corso.setStudenti(studenteRepository.findAllById(request.getStudentiIds()));
		corso.setAttivo(true);

		corsoRepository.save(corso);
		return convertToResponseDTO(corso);
	}



	public CorsoResponseDTO modificaCorso(Long id, CorsoRequestDTO request) {
		Corso corso = corsoRepository.findById(id)
			.orElseThrow(() -> new EntityNotFoundException("Corso non trovato"));

		Optional<Aula> aulaOpt = aulaRepository.findById(request.getAulaId());
		if (aulaOpt.isEmpty()) {
			throw new EntityNotFoundException("Aula non trovata con ID: " + request.getAulaId());
		}
		Aula aula = aulaOpt.get();

		Optional<Insegnante> insegnanteOpt = insegnanteRepository.findById(request.getInsegnanteId());
		if (insegnanteOpt.isEmpty()) {
			throw new EntityNotFoundException("Insegnante non trovato con ID: " + request.getInsegnanteId());
		}
		Insegnante insegnante = insegnanteOpt.get();


		List<Corso> corsiEsistenti = corsoRepository.findByAulaIdAndGiornoAndOrarioAndAttivoTrue(
			aula.getId(), request.getGiorno(), request.getOrario());
		boolean sovrapposto = corsiEsistenti.stream()
			.anyMatch(c -> !c.getId().equals(corso.getId()));
		if (sovrapposto) {
			throw new IllegalStateException("Impossibile modificare: l'aula è già occupata per quel giorno/orario.");
		}

		BeanUtils.copyProperties(request, corso, "id");
		corso.setNome(request.getNome());
		corso.setTipoCorso(request.getTipoCorso());
		corso.setSecondoGiorno(request.getSecondoGiorno());
		corso.setSecondoOrario(request.getSecondoOrario());
		corso.setAula(aula);
		corso.setInsegnante(insegnante);
		corso.setStudenti(studenteRepository.findAllById(request.getStudentiIds()));

		corsoRepository.save(corso);
		return convertToResponseDTO(corso);
	}




	public void interrompiCorso(Long id) {
		Corso corso = corsoRepository.findById(id)
			.orElseThrow(() -> new EntityNotFoundException("Corso non trovato"));

		corso.setAttivo(false);
		corsoRepository.save(corso);
	}


	public void eliminaCorso(Long id) {
		Corso corso = corsoRepository.findById(id)
			.orElseThrow(() -> new EntityNotFoundException("Corso non trovato"));

		corsoRepository.deleteById(id);
	}



	public List<CorsoResponseDTO> getCorsiDisattivati() {
		List<Corso> corsi = corsoRepository.findByAttivoFalse();
		return corsi.stream().map(this::convertToResponseDTO).collect(Collectors.toList());
	}


	public void riattivaCorso(Long id) {
		Corso corso = corsoRepository.findById(id)
			.orElseThrow(() -> new EntityNotFoundException("Corso non trovato con ID: " + id));

		if (corso.isAttivo()) {
			throw new IllegalStateException("Il corso è già attivo.");
		}


		List<Corso> corsiEsistenti = corsoRepository.findByAulaIdAndGiornoAndOrarioAndAttivoTrue(
			corso.getAula().getId(), corso.getGiorno(), corso.getOrario());

		if (!corsiEsistenti.isEmpty()) {
			throw new IllegalStateException("Impossibile riattivare il corso: l'aula è già occupata in quel giorno/orario.");
		}

		corso.setAttivo(true);
		corsoRepository.save(corso);
	}

	public List<CorsoResponseDTO> getCorsiByTipoCorso(TipoCorso tipoCorso, boolean attivo) {
		return corsoRepository.findByTipoCorsoAndAttivo(tipoCorso, attivo).stream()
			.map(this::convertToResponseDTO)
			.collect(Collectors.toList());
	}

	public List<StudenteResponseDTO> getListaDiAttesa() {
		return studenteRepository.findStudentiSenzaCorso()
			.stream()
			.map(studenteService::convertToResponseDTO)
			.collect(Collectors.toList());
	}
	
	public void aggiungiStudente(Long corsoId, Long studenteId) {
		Corso corso = corsoRepository.findById(corsoId)
			.orElseThrow(() -> new EntityNotFoundException("Corso non trovato con ID: " + corsoId));
		Studente studente = studenteRepository.findById(studenteId)
			.orElseThrow(() -> new EntityNotFoundException("Studente non trovato con ID: " + studenteId));


		List<Studente> studenti = corso.getStudenti();
		if (!studenti.contains(studente)) {
			studenti.add(studente);
			corsoRepository.save(corso);
		} else {
			throw new IllegalArgumentException("Lo studente è già assegnato a questo corso.");
		}
	}



	public CorsoResponseDTO convertToResponseDTO(Corso corso) {
		CorsoResponseDTO dto = new CorsoResponseDTO();
		BeanUtils.copyProperties(corso, dto);
		dto.setNome(corso.getNome());
		dto.setTipoCorso(corso.getTipoCorso());
		if (corso.getInsegnante() != null) {
			dto.setInsegnante(new InsegnanteResponseDTO(corso.getInsegnante().getId(),
				corso.getInsegnante().getNome(),
				corso.getInsegnante().getCognome()));
		}
		if (corso.getAula() != null) {
			dto.setAula(new AulaResponseDTO(corso.getAula().getId(),
				corso.getAula().getNome(),
				corso.getAula().getCapienzaMax()));
		}
		if (corso.getStudenti() != null) {
			dto.setStudenti(corso.getStudenti().stream()
				.map(studente -> new StudenteResponseDTO(studente.getId(),
					studente.getNome(),
					studente.getCognome()))
				.collect(Collectors.toList()));
		}
		return dto;
	}

	public List<CorsoResponseDTO> getAllCorsi() {
		return corsoRepository.findAll().stream()
			.map(this::convertToResponseDTO)
			.collect(Collectors.toList());
	}

	public List<CorsoResponseDTO> getAllCorsiByInsegnante(Long insegnanteId) {
		return corsoRepository.findByInsegnanteId(insegnanteId).stream()
			.map(this::convertToResponseDTO)
			.collect(Collectors.toList());
	}

}