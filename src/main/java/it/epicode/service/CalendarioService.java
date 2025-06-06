package it.epicode.service;


import it.epicode.dto.CalendarioDTO;
import it.epicode.entity.Aula;
import it.epicode.entity.Corso;
import it.epicode.repository.AulaRepository;
import it.epicode.repository.CorsoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CalendarioService {

	@Autowired private CorsoRepository corsoRepository;

	@Autowired private AulaRepository aulaRepository;

	public List<String> getAuleDisponibili(String giorno, String orario) {
		List<Long> auleOccupate = corsoRepository.findByGiornoAndOrarioAndAttivoTrue(giorno, orario)
			.stream()
			.map(c -> c.getAula().getId())
			.collect(Collectors.toList());

		return aulaRepository.findAll().stream()
			.filter(aula -> !auleOccupate.contains(aula.getId()))
			.map(Aula::getNome)
			.collect(Collectors.toList());
	}


	public void interrompiCorso(Long corsoId) {
		Corso corso = corsoRepository.findById(corsoId)
			.orElseThrow(() -> new EntityNotFoundException("Corso non trovato"));

		corso.setAttivo(false);
		corsoRepository.save(corso);
	}

	private String normalizza(String input) {
		return input == null ? "" : input.toLowerCase().replace("ì", "i").replace("é", "e");
	}


	public List<CalendarioDTO> getCorsiSettimanaFiltrati(String giornoBase, Long insegnanteId, String livello) {
		List<String> giorniSettimana = List.of("Lunedì", "Martedì", "Mercoledì", "Giovedì", "Venerdì", "Sabato");
		List<String> giorniNormalizzati = giorniSettimana.stream().map(this::normalizza).toList();

		List<CalendarioDTO> lezioni = new ArrayList<>();

		List<Corso> corsi = corsoRepository.findByAttivoTrue().stream()
			.filter(c -> insegnanteId == null || (c.getInsegnante() != null && c.getInsegnante().getId().equals(insegnanteId)))
			.filter(c -> livello == null || livello.isEmpty() || c.getLivello().toString().equalsIgnoreCase(livello))
			.toList();

		for (Corso c : corsi) {
			String aulaNome = c.getAula() != null ? c.getAula().getNome() : "N/A";
			String insegnanteNome = c.getInsegnante() != null
				? c.getInsegnante().getNome() + " " + c.getInsegnante().getCognome()
				: "N/A";

			if (giornoBase == null || giornoBase.isBlank() || giornoBase.equalsIgnoreCase("settimana") ||
				giornoBase.equalsIgnoreCase("tutti") || giorniNormalizzati.contains(normalizza(c.getGiorno()))) {
				lezioni.add(new CalendarioDTO(
					c.getId(),
					c.getCorsoTipo(),
					c.getFrequenza(),
					c.getGiorno(),
					c.getOrario(),
					aulaNome,
					insegnanteNome,
					c.getLivello().name()
				));
			}

			if ("2 volte a settimana".equalsIgnoreCase(c.getFrequenza())
				&& c.getSecondoGiorno() != null
				&& c.getSecondoOrario() != null
				&& giorniNormalizzati.contains(normalizza(c.getSecondoGiorno()))) {

				lezioni.add(new CalendarioDTO(
					c.getId(),
					c.getCorsoTipo(),
					c.getFrequenza(),
					c.getSecondoGiorno(),
					c.getSecondoOrario(),
					aulaNome,
					insegnanteNome,
					c.getLivello().name()
				));
			}
		}

		return lezioni;
	}
}
