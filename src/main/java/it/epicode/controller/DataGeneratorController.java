package it.epicode.controller;

import it.epicode.service.DataGeneratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/generate-data")
public class DataGeneratorController {

	@Autowired
	private DataGeneratorService dataGeneratorService;

	@PostMapping
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public ResponseEntity<String> generateData() {
		dataGeneratorService.generateAllData(5, 5, 30, 10, 20, 100);
		return ResponseEntity.ok("Dati generati con successo!");
	}
} 