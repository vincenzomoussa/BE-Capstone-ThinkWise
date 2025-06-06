package it.epicode.controller;


import it.epicode.dto.InsegnanteRequestDTO;
import it.epicode.dto.InsegnanteResponseDTO;
import it.epicode.service.InsegnanteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/insegnanti")
public class InsegnanteController {

	private final InsegnanteService insegnanteService;

	public InsegnanteController(InsegnanteService insegnanteService) {
		this.insegnanteService = insegnanteService;
	}


	@GetMapping
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public ResponseEntity<List<InsegnanteResponseDTO>> getAllInsegnanti() {
		return ResponseEntity.ok(insegnanteService.getAllInsegnanti());
	}


	@GetMapping("/{id}")
	@PreAuthorize("hasRole('ROLE_ADMIN') or #id == authentication.principal.id")
	public ResponseEntity<InsegnanteResponseDTO> getInsegnanteById(@PathVariable Long id, Authentication authentication) {
		return ResponseEntity.ok(insegnanteService.getInsegnanteById(id));
	}


	@PostMapping
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@ResponseStatus(HttpStatus.CREATED)
	public InsegnanteResponseDTO createInsegnante(@RequestBody InsegnanteRequestDTO insegnanteRequestDTO) {
		return insegnanteService.createInsegnante(insegnanteRequestDTO);
	}


	@PutMapping("/{id}")
	@PreAuthorize("hasRole('ROLE_ADMIN') or #id == authentication.principal.id")
	public ResponseEntity<InsegnanteResponseDTO> updateInsegnante(
		@PathVariable Long id,
		@RequestBody InsegnanteRequestDTO insegnanteRequestDTO,
		Authentication authentication) {
		return ResponseEntity.ok(insegnanteService.updateInsegnante(id, insegnanteRequestDTO));
	}


	@DeleteMapping("/{id}")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteInsegnante(@PathVariable Long id) {
		insegnanteService.deleteInsegnante(id);
	}
}
