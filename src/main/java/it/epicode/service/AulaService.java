package it.epicode.service;

import it.epicode.dto.AulaRequestDTO;
import it.epicode.dto.AulaResponseDTO;
import it.epicode.entity.Aula;
import it.epicode.repository.AulaRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AulaService {

	private final AulaRepository aulaRepository;

	public AulaService(AulaRepository aulaRepository) {
		this.aulaRepository = aulaRepository;
	}


	public List<AulaResponseDTO> getAllAule() {
		return aulaRepository.findAll().stream()
			.map(this::convertToResponseDTO)
			.collect(Collectors.toList());
	}


	public AulaResponseDTO getAulaById(Long id) {
		Aula aula = aulaRepository.findById(id)
			.orElseThrow(() -> new EntityNotFoundException("Aula non trovata con ID: " + id));
		return convertToResponseDTO(aula);
	}


	public AulaResponseDTO createAula(AulaRequestDTO aulaRequestDTO) {
		Aula aula = new Aula();
		BeanUtils.copyProperties(aulaRequestDTO, aula);
		aulaRepository.save(aula);
		return convertToResponseDTO(aula);
	}


	public AulaResponseDTO updateAula(Long id, AulaRequestDTO aulaRequestDTO) {
		Aula aula = aulaRepository.findById(id)
			.orElseThrow(() -> new EntityNotFoundException("Aula non trovata con ID: " + id));
		BeanUtils.copyProperties(aulaRequestDTO, aula);
		aulaRepository.save(aula);
		return convertToResponseDTO(aula);
	}


	public void deleteAula(Long id) {
		aulaRepository.deleteById(id);
	}


	public List<AulaResponseDTO> getAuleDisponibiliByGiornoEOrario(String giorno, String orario) {
		return aulaRepository.findAuleDisponibiliByGiornoEOrario(giorno, orario).stream()
			.map(this::convertToResponseDTO)
			.collect(Collectors.toList());
	}


	private AulaResponseDTO convertToResponseDTO(Aula aula) {
		AulaResponseDTO dto = new AulaResponseDTO();
		BeanUtils.copyProperties(aula, dto);
		return dto;
	}
}
