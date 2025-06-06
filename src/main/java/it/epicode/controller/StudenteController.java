package it.epicode.controller;

import it.epicode.dto.PagamentoRequestDTO;
import it.epicode.dto.PagamentoResponseDTO;
import it.epicode.dto.StudenteRequestDTO;
import it.epicode.dto.StudenteResponseDTO;
import it.epicode.entity.Corso;
import it.epicode.entity.Pagamento;
import it.epicode.entity.Studente;
import it.epicode.repository.CorsoRepository;
import it.epicode.repository.PagamentoRepository;
import it.epicode.repository.StudenteRepository;
import it.epicode.service.StudenteService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/studenti")
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class StudenteController {

	private final StudenteService studenteService;
	private final StudenteRepository studenteRepository;
	private final CorsoRepository corsoRepository;
	private final PagamentoRepository pagamentoRepository;


	public StudenteController(
		StudenteService studenteService,
		StudenteRepository studenteRepository,
		CorsoRepository corsoRepository,
		PagamentoRepository pagamentoRepository
	) {
		this.studenteService = studenteService;
		this.studenteRepository = studenteRepository;
		this.corsoRepository = corsoRepository;
		this.pagamentoRepository = pagamentoRepository;
	}


	@GetMapping
	public ResponseEntity<List<StudenteResponseDTO>> getAllStudenti() {
		return ResponseEntity.ok(studenteService.getAllStudenti());
	}


	@GetMapping("/{id}")
	public ResponseEntity<StudenteResponseDTO> getStudenteById(@PathVariable Long id) {
		return ResponseEntity.ok(studenteService.getStudenteById(id));
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public StudenteResponseDTO createStudente(@RequestBody StudenteRequestDTO dto) {
		return studenteService.createStudente(dto);
	}


	@PutMapping("/{id}")
	public ResponseEntity<StudenteResponseDTO> updateStudente(@PathVariable Long id, @RequestBody StudenteRequestDTO dto) {
		return ResponseEntity.ok(studenteService.updateStudente(id, dto));
	}


	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteStudente(@PathVariable Long id) {
		studenteService.deleteStudente(id);
	}


	@GetMapping("/senza-corso")
	public ResponseEntity<List<StudenteResponseDTO>> getStudentiSenzaCorso() {
		List<StudenteResponseDTO> studentiSenzaCorso = studenteService.getStudentiSenzaCorsoDTO();
		return ResponseEntity.ok(studentiSenzaCorso);
	}


	@GetMapping("/{id}/corsi")
	public ResponseEntity<List<Corso>> getCorsiStudente(@PathVariable Long id) {
		Studente studente = studenteRepository.findById(id)
			.orElseThrow(() -> new EntityNotFoundException("Studente non trovato"));

		return ResponseEntity.ok(studente.getCorsi());
	}


	@GetMapping("/{id}/pagamenti")
	public ResponseEntity<List<Pagamento>> getPagamentiStudente(@PathVariable Long id) {
		List<Pagamento> pagamenti = pagamentoRepository.findByStudenteId(id);
		return ResponseEntity.ok(pagamenti);
	}


	@DeleteMapping("/{studenteId}/rimuovi-da-corso/{corsoId}")
	public ResponseEntity<?> rimuoviStudenteDaCorso(@PathVariable Long studenteId, @PathVariable Long corsoId) {
		Studente studente = studenteRepository.findById(studenteId)
			.orElseThrow(() -> new EntityNotFoundException("Studente non trovato"));
		Corso corso = corsoRepository.findById(corsoId)
			.orElseThrow(() -> new EntityNotFoundException("Corso non trovato"));

		corso.getStudenti().remove(studente);
		studente.getCorsi().remove(corso);

		corsoRepository.save(corso);
		studenteRepository.save(studente);

		return ResponseEntity.ok("Studente rimosso dal corso");
	}


	@PostMapping("/{id}/pagamenti")
	public ResponseEntity<?> aggiungiPagamento(
			@PathVariable Long id,
			@RequestBody PagamentoRequestDTO pagamentoRequestDTO) {

		// Recuperiamo lo studente dall'ID
		Studente studente = studenteRepository.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Studente non trovato"));

		// Creiamo il nuovo pagamento
		Pagamento nuovoPagamento = new Pagamento();
		nuovoPagamento.setStudente(studente);
		nuovoPagamento.setImporto(pagamentoRequestDTO.getImporto());
		nuovoPagamento.setDataPagamento(pagamentoRequestDTO.getDataPagamento());
		nuovoPagamento.setMensilitaSaldata(pagamentoRequestDTO.getMensilitaSaldata());
		nuovoPagamento.setNumeroRicevuta(pagamentoRequestDTO.getNumeroRicevuta());
		nuovoPagamento.setMetodoPagamento(pagamentoRequestDTO.getMetodoPagamento());
		nuovoPagamento.setNote(pagamentoRequestDTO.getNote());

		pagamentoRepository.save(nuovoPagamento);
		return ResponseEntity.ok(nuovoPagamento);
	}
}
