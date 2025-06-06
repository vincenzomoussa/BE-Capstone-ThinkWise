package it.epicode.controller;


import it.epicode.dto.CorsoRequestDTO;
import it.epicode.dto.CorsoResponseDTO;
import it.epicode.dto.StudenteRequestDTO;
import it.epicode.dto.StudenteResponseDTO;
import it.epicode.entity.Livello;
import it.epicode.entity.TipoCorso;
import it.epicode.service.CorsoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/corsi")
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class CorsoController {

	@Autowired private CorsoService corsoService;


	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@GetMapping
	public ResponseEntity<List<CorsoResponseDTO>> getTuttiICorsi(@RequestParam(required = false) TipoCorso tipo) {
		if (tipo != null) {
			return ResponseEntity.ok(corsoService.getCorsiByTipoCorso(tipo, true));
		} else {
		return ResponseEntity.ok(corsoService.getTuttiICorsi());
		}
	}


	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@GetMapping("/{id}")
	public ResponseEntity<CorsoResponseDTO> getCorsoById(@PathVariable Long id) {
		return ResponseEntity.ok(corsoService.getCorsoById(id));
	}



	@GetMapping("/insegnante/{id}")
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_INSEGNANTE')")
	public ResponseEntity<List<CorsoResponseDTO>> getCorsiByInsegnante(@PathVariable Long id) {
		return ResponseEntity.ok(corsoService.getCorsiByInsegnante(id));
	}


	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@GetMapping("/giorno-orario")
	public ResponseEntity<List<CorsoResponseDTO>> getCorsiByGiornoEOrario(
		@RequestParam String giorno,
		@RequestParam String orario) {
		return ResponseEntity.ok(corsoService.getCorsiByGiornoEOrario(giorno, orario));
	}


	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@GetMapping("/lingua-livello")
	public ResponseEntity<List<CorsoResponseDTO>> getCorsiByLinguaELivello(
		@RequestParam String corsoTipo,
		@RequestParam String livello
	) {
		try {
			Livello livelloEnum = Livello.valueOf(livello.toUpperCase());
			return ResponseEntity.ok(corsoService.getCorsiByTipologiaELivello(corsoTipo, livelloEnum));
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(Collections.emptyList());
		}
	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@GetMapping("/tipo/{tipoCorso}")
	public ResponseEntity<List<CorsoResponseDTO>> getCorsiByTipoCorso(@PathVariable TipoCorso tipoCorso, @RequestParam(required = false) boolean attivo) {
		return ResponseEntity.ok(corsoService.getCorsiByTipoCorso(tipoCorso, attivo));
	}


	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@PostMapping
	public ResponseEntity<CorsoResponseDTO> creaCorso(@RequestBody CorsoRequestDTO request) {
		return ResponseEntity.ok(corsoService.creaCorso(request));
	}


	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@PutMapping("/{id}")
	public ResponseEntity<CorsoResponseDTO> modificaCorso(@PathVariable Long id, @RequestBody CorsoRequestDTO request) {
		return ResponseEntity.ok(corsoService.modificaCorso(id, request));
	}


	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@PutMapping("/{id}/interrompi")
	public ResponseEntity<String> interrompiCorso(@PathVariable Long id) {
		corsoService.interrompiCorso(id);
		return ResponseEntity.ok("Corso interrotto con successo.");
	}


	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@DeleteMapping("/{id}")
	public ResponseEntity<String> eliminaCorso(@PathVariable Long id) {
		corsoService.eliminaCorso(id);
		return ResponseEntity.ok("Corso eliminato con successo.");
	}


	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@GetMapping("/disattivati")
	public ResponseEntity<List<CorsoResponseDTO>> getCorsiDisattivati(@RequestParam(required = false) TipoCorso tipo) {
		if (tipo != null) {
			return ResponseEntity.ok(corsoService.getCorsiByTipoCorso(tipo, false));
		} else {
		return ResponseEntity.ok(corsoService.getCorsiDisattivati());
		}
	}


	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@PutMapping("/{id}/riattiva")
	public ResponseEntity<String> riattivaCorso(@PathVariable Long id) {
		corsoService.riattivaCorso(id);
		return ResponseEntity.ok("✅ Corso riattivato con successo.");
	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@GetMapping("/lista-attesa/studenti")
	public ResponseEntity<List<StudenteResponseDTO>> getStudentiInListaDiAttesa() {
		return ResponseEntity.ok(corsoService.getListaDiAttesa());
	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@PostMapping("/{id}/aggiungi-studente")
	public ResponseEntity<String> aggiungiStudenteAlCorso(
		@PathVariable Long id,
		@RequestBody StudenteRequestDTO studenteRequestDTO) {
		corsoService.aggiungiStudente(id, studenteRequestDTO.getStudenteId());
		return ResponseEntity.ok("Studente assegnato al corso con successo.");
	}
}