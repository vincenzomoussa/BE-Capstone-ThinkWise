package it.epicode.controller;


import it.epicode.dto.AulaRequestDTO;
import it.epicode.dto.AulaResponseDTO;
import it.epicode.service.AulaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/aule")
public class AulaController {

	private final AulaService aulaService;

	public AulaController(AulaService aulaService) {
		this.aulaService = aulaService;
	}


	@GetMapping
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public ResponseEntity<List<AulaResponseDTO>> getAllAule() {
		return ResponseEntity.ok(aulaService.getAllAule());
	}


	@GetMapping("/{id}")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public ResponseEntity<AulaResponseDTO> getAulaById(@PathVariable Long id) {
		return ResponseEntity.ok(aulaService.getAulaById(id));
	}


	@PostMapping
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@ResponseStatus(HttpStatus.CREATED)
	public AulaResponseDTO createAula(@RequestBody AulaRequestDTO aulaRequestDTO) {
		return aulaService.createAula(aulaRequestDTO);
	}


	@PutMapping("/{id}")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public ResponseEntity<AulaResponseDTO> updateAula(@PathVariable Long id, @RequestBody AulaRequestDTO aulaRequestDTO) {
		return ResponseEntity.ok(aulaService.updateAula(id, aulaRequestDTO));
	}


	@DeleteMapping("/{id}")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteAula(@PathVariable Long id) {
		aulaService.deleteAula(id);
	}


	@GetMapping("/disponibilita")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public ResponseEntity<List<AulaResponseDTO>> getAuleDisponibili(
		@RequestParam String giorno, @RequestParam String orario) {
		return ResponseEntity.ok(aulaService.getAuleDisponibiliByGiornoEOrario(giorno, orario));
	}
}
