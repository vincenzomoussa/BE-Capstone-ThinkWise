package it.epicode.service;


import it.epicode.dto.StudenteRequestDTO;
import it.epicode.dto.StudenteResponseDTO;
import it.epicode.entity.Studente;
import it.epicode.repository.StudenteRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StudenteService {

	@Autowired
	private StudenteRepository studenteRepository;

	public List<StudenteResponseDTO> getAllStudenti() {
		return studenteRepository.findAll().stream()
				.map(this::convertToResponseDTO)
				.collect(Collectors.toList());
	}

	public StudenteResponseDTO getStudenteById(Long id) {
		Studente studente = studenteRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Studente non trovato con ID: " + id));
		return convertToResponseDTO(studente);
	}

	public StudenteResponseDTO createStudente(StudenteRequestDTO dto) {
		Studente s = new Studente();
		BeanUtils.copyProperties(dto, s);
		studenteRepository.save(s);
		return convertToResponseDTO(s);
	}

	public StudenteResponseDTO updateStudente(Long id, StudenteRequestDTO dto) {
		Studente s = studenteRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Studente non trovato con ID: " + id));
		BeanUtils.copyProperties(dto, s);
		studenteRepository.save(s);
		return convertToResponseDTO(s);
	}

	public void deleteStudente(Long id) {
		studenteRepository.deleteById(id);
	}

	public StudenteResponseDTO convertToResponseDTO(Studente s) {
		StudenteResponseDTO dto = new StudenteResponseDTO();
		BeanUtils.copyProperties(s, dto);
		if (s.getCorsi() != null && !s.getCorsi().isEmpty()) {
			List<StudenteResponseDTO.CorsoAttivo> corsiDTO = s.getCorsi().stream()
					.map(c -> new StudenteResponseDTO.CorsoAttivo(c.getId(), c.getNome(), c.isAttivo()))
					.toList();
			dto.setCorsi(corsiDTO);
		}

		return dto;
	}
	public List<Studente> getStudentiSenzaCorso() {
		return studenteRepository.findStudentiSenzaCorso();
	}

	public List<StudenteResponseDTO> getStudentiSenzaCorsoDTO() {
		return studenteRepository.findStudentiSenzaCorso().stream()
				.map(this::convertToResponseDTO)
				.collect(Collectors.toList());
	}
}
