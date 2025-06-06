package it.epicode.controller;


import it.epicode.dto.CalendarioDTO;
import it.epicode.service.CalendarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/calendario")
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class CalendarioController {

	@Autowired
	private CalendarioService calendarioService;


	@GetMapping("/aule-disponibili")
	public List<String> getAuleDisponibili(@RequestParam String giorno, @RequestParam String orario) {
		return calendarioService.getAuleDisponibili(giorno, orario);
	}


	@GetMapping("/corsi-programmati")
	public List<CalendarioDTO> getCorsiProgrammati(
		@RequestParam String giorno,
		@RequestParam(required = false) Long insegnante,
		@RequestParam(required = false) String livello) {

		return calendarioService.getCorsiSettimanaFiltrati(giorno, insegnante, livello);
	}



	@PostMapping("/interrompi-corso/{corsoId}")
	public void interrompiCorso(@PathVariable Long corsoId) {
		calendarioService.interrompiCorso(corsoId);
	}
}
