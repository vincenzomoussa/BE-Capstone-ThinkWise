package it.epicode.controller;


import it.epicode.dto.PagamentoRequestDTO;
import it.epicode.dto.PagamentoResponseDTO;
import it.epicode.service.PagamentoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pagamenti")
@RequiredArgsConstructor
public class PagamentoController {

	private final PagamentoService pagamentoService;


	@PostMapping
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@ResponseStatus(HttpStatus.CREATED)
	public PagamentoResponseDTO registraPagamento(@RequestBody PagamentoRequestDTO requestDTO) {
		return pagamentoService.registraPagamento(requestDTO);
	}


	@GetMapping
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@ResponseStatus(HttpStatus.OK)
	public List<PagamentoResponseDTO> getTuttiIPagamenti() {
		return pagamentoService.getTuttiIPagamenti();
	}


	@GetMapping("/{id}")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@ResponseStatus(HttpStatus.OK)
	public PagamentoResponseDTO getPagamentoById(@PathVariable Long id) {
		return pagamentoService.getPagamentoById(id);
	}


	@GetMapping("/studente/{studenteId}")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@ResponseStatus(HttpStatus.OK)
	public List<PagamentoResponseDTO> getPagamentiByStudente(@PathVariable Long studenteId) {
		return pagamentoService.getPagamentiByStudente(studenteId);
	}


	@GetMapping("/mensilita/{mensilita}")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@ResponseStatus(HttpStatus.OK)
	public List<PagamentoResponseDTO> getPagamentiByMensilita(@PathVariable String mensilita) {
		return pagamentoService.getPagamentiByMensilita(mensilita);
	}


	@DeleteMapping("/{pagamentoId}")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void eliminaPagamento(@PathVariable Long pagamentoId) {
		pagamentoService.eliminaPagamento(pagamentoId);
	}


	@PutMapping("/{pagamentoId}")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@ResponseStatus(HttpStatus.OK)
	public PagamentoResponseDTO aggiornaPagamento(
		@PathVariable Long pagamentoId,
		@RequestBody PagamentoRequestDTO requestDTO
	) {
		return pagamentoService.aggiornaPagamento(pagamentoId, requestDTO);
	}
}
