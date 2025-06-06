package it.epicode.controller;


import it.epicode.entity.Livello;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/livelli")
public class LivelloController {

	@GetMapping
	public ResponseEntity<Livello[]> getLivelli() {
		return ResponseEntity.ok(Livello.values());
	}
}
