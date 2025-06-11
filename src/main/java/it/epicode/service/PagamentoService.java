package it.epicode.service;

import it.epicode.dto.PagamentoRequestDTO;
import it.epicode.dto.PagamentoResponseDTO;
import it.epicode.entity.Pagamento;
import it.epicode.entity.Studente;
import it.epicode.repository.PagamentoRepository;
import it.epicode.repository.StudenteRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PagamentoService {

	private final PagamentoRepository pagamentoRepository;
	private final StudenteRepository studenteRepository;

	@Transactional
	public PagamentoResponseDTO registraPagamento(PagamentoRequestDTO requestDTO) {
		Studente studente = studenteRepository.findById(requestDTO.getStudenteId())
			.orElseThrow(() -> new EntityNotFoundException("Studente non trovato con ID: " + requestDTO.getStudenteId()));

		if (requestDTO.getMensilitaSaldata() != null) {
			LocalDate iscrizione = studente.getDataIscrizione();
			String[] parts = requestDTO.getMensilitaSaldata().split(" ");
			if (parts.length == 2) {
				String monthName = parts[0];
				int year = Integer.parseInt(parts[1]);
				java.time.Month meseEnum = null;
				for (java.time.Month m : java.time.Month.values()) {
					if (m.getDisplayName(java.time.format.TextStyle.FULL, java.util.Locale.ITALIAN).equalsIgnoreCase(monthName)) {
						meseEnum = m;
						break;
					}
				}
				if (meseEnum != null) {
					LocalDate mensilita = LocalDate.of(year, meseEnum, 1);
					if (mensilita.isBefore(iscrizione.withDayOfMonth(1))) {
						throw new IllegalArgumentException("Non è possibile pagare un mese precedente alla data di iscrizione");
					}
				}
			}
		}

		Pagamento pagamento = new Pagamento();
		BeanUtils.copyProperties(requestDTO, pagamento);
		pagamento.setStudente(studente);

		pagamentoRepository.save(pagamento);
		return convertToResponseDTO(pagamento);
	}

	public List<PagamentoResponseDTO> getTuttiIPagamenti() {
		return pagamentoRepository.findAll().stream()
			.map(this::convertToResponseDTO)
			.collect(Collectors.toList());
	}

	public PagamentoResponseDTO getPagamentoById(Long id) {
		Pagamento pagamento = pagamentoRepository.findById(id)
			.orElseThrow(() -> new EntityNotFoundException("Pagamento non trovato con ID: " + id));


		return convertToResponseDTO(pagamento);
	}

	public List<PagamentoResponseDTO> getPagamentiByStudente(Long studenteId) {
		return pagamentoRepository.findByStudenteId(studenteId).stream()
			.map(this::convertToResponseDTO)
			.collect(Collectors.toList());
	}

	public List<PagamentoResponseDTO> getPagamentiByMensilita(String mensilita) {
		return pagamentoRepository.findByMensilitaSaldata(mensilita).stream()
			.map(this::convertToResponseDTO)
			.collect(Collectors.toList());
	}

	@Transactional
	public void eliminaPagamento(Long pagamentoId) {

		Pagamento pagamento = pagamentoRepository.findById(pagamentoId)
			.orElseThrow(() -> new EntityNotFoundException("Pagamento non trovato con ID: " + pagamentoId));


		pagamentoRepository.delete(pagamento);
	}


	@Transactional
	public PagamentoResponseDTO aggiornaPagamento(Long pagamentoId, PagamentoRequestDTO requestDTO) {

		Pagamento pagamento = pagamentoRepository.findById(pagamentoId)
			.orElseThrow(() -> new EntityNotFoundException("Pagamento non trovato con ID: " + pagamentoId));


		pagamento.setDataPagamento(requestDTO.getDataPagamento());
		pagamento.setImporto(requestDTO.getImporto());
		pagamento.setMensilitaSaldata(requestDTO.getMensilitaSaldata());
		pagamento.setMetodoPagamento(requestDTO.getMetodoPagamento());
		pagamento.setNumeroRicevuta(requestDTO.getNumeroRicevuta());
		pagamento.setNote(requestDTO.getNote());


		Pagamento pagamentoAggiornato = pagamentoRepository.save(pagamento);


		return convertToResponseDTO(pagamentoAggiornato);
	}



	private PagamentoResponseDTO convertToResponseDTO(Pagamento pagamento) {
		PagamentoResponseDTO dto = new PagamentoResponseDTO();
		BeanUtils.copyProperties(pagamento, dto);
		dto.setStudenteNome(pagamento.getStudente().getNome() + " " + pagamento.getStudente().getCognome());
		return dto;
	}
}