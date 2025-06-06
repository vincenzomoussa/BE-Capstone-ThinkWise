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