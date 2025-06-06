package it.epicode.controller;


import it.epicode.service.ThinkBoardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
public class ThinkBoard {

	private final ThinkBoardService thinkBoardService;

	public ThinkBoard(ThinkBoardService thinkBoardService) {
		this.thinkBoardService = thinkBoardService;
	}

	@GetMapping("/stats")
	public ResponseEntity<Map<String, Object>> getStats() {
		return ResponseEntity.ok(thinkBoardService.getStats());
	}

	@GetMapping("/avvisi")
	public ResponseEntity<List<Map<String, String>>> getAvvisi() {
		return ResponseEntity.ok(thinkBoardService.getAvvisi());
	}

	@GetMapping("/pagamenti-mensili")
	public ResponseEntity<Map<String, Object>> getPagamentiMensili() {
		return ResponseEntity.ok(thinkBoardService.getPagamentiMensili());
	}

	@GetMapping("/entrate-uscite")
	public ResponseEntity<Map<String, Object>> getEntrateUscite() {
		return ResponseEntity.ok(thinkBoardService.getEntrateUscite());
	}

	@GetMapping("/spese-generali")
	public ResponseEntity<Map<String, Object>> getSpeseGenerali() {
		return ResponseEntity.ok(thinkBoardService.getSpeseGenerali());
	}
}
