package it.epicode.service;


import it.epicode.dto.InsegnanteRequestDTO;
import it.epicode.dto.InsegnanteResponseDTO;
import it.epicode.entity.Insegnante;
import it.epicode.repository.InsegnanteRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class InsegnanteService {

	@Autowired private InsegnanteRepository insegnanteRepository;

	public List<InsegnanteResponseDTO> getAllInsegnanti() {
		return insegnanteRepository.findAll().stream()
			.map(this::convertToResponseDTO)
			.collect(Collectors.toList());
	}

	public InsegnanteResponseDTO getInsegnanteById(Long id) {
		Insegnante insegnante = insegnanteRepository.findById(id)
			.orElseThrow(() -> new EntityNotFoundException("Insegnante non trovato con ID: " + id));
		return convertToResponseDTO(insegnante);
	}

	public InsegnanteResponseDTO createInsegnante(InsegnanteRequestDTO dto) {
		Insegnante insegnante = new Insegnante();
		BeanUtils.copyProperties(dto, insegnante);
		insegnante.setSpecializzazioni(dto.getSpecializzazioni());
		insegnanteRepository.save(insegnante);
		return convertToResponseDTO(insegnante);
	}

	public InsegnanteResponseDTO updateInsegnante(Long id, InsegnanteRequestDTO dto) {
		Insegnante insegnante = insegnanteRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Insegnante non trovato con ID: " + id));
		BeanUtils.copyProperties(dto, insegnante);
		insegnante.setSpecializzazioni(dto.getSpecializzazioni());
		insegnanteRepository.save(insegnante);
		return convertToResponseDTO(insegnante);
	}

	public void deleteInsegnante(Long id) {
		insegnanteRepository.deleteById(id);
	}


	private InsegnanteResponseDTO convertToResponseDTO(Insegnante insegnante) {
		InsegnanteResponseDTO dto = new InsegnanteResponseDTO();
		BeanUtils.copyProperties(insegnante, dto);
		dto.setSpecializzazioni(insegnante.getSpecializzazioni());
		return dto;
	}

}
