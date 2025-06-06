package it.epicode.controller;


import it.epicode.dto.SpesaRequestDTO;
import it.epicode.dto.SpesaResponseDTO;
import it.epicode.entity.Spesa;
import it.epicode.service.SpesaService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/spese")
public class SpesaController {

	private final SpesaService spesaService;

	public SpesaController(SpesaService spesaService) {
		this.spesaService = spesaService;
	}


	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@GetMapping
	public List<SpesaResponseDTO> getAllSpese() {
		return spesaService.getAllSpese();
	}


	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@GetMapping("/{id}")
	public SpesaResponseDTO getSpesaById(@PathVariable Long id) {
		return spesaService.getSpesaById(id);
	}


	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public SpesaResponseDTO createSpesa(@RequestBody SpesaRequestDTO dto) {
		return spesaService.createSpesa(dto);
	}


	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@PutMapping("/{id}")
	public SpesaResponseDTO updateSpesa(@PathVariable Long id, @RequestBody SpesaRequestDTO dto) {
		return spesaService.updateSpesa(id, dto);
	}


	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteSpesa(@PathVariable Long id) {
		spesaService.deleteSpesa(id);
	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@GetMapping("/filtrate")
	public List<SpesaResponseDTO> getSpeseFiltrate(
		@RequestParam(required = false) Integer anno,
		@RequestParam(required = false) Integer mese,
		@RequestParam(required = false) Spesa.CategoriaSpesa categoria
	) {
		return spesaService.getSpeseFiltrate(anno, mese, categoria);
	}
}
