package it.epicode.service;


import it.epicode.dto.SpesaRequestDTO;
import it.epicode.dto.SpesaResponseDTO;
import it.epicode.entity.Spesa;
import it.epicode.repository.SpesaRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SpesaService {

	@Autowired
	private SpesaRepository spesaRepository;


	public List<SpesaResponseDTO> getAllSpese() {
		return spesaRepository.findAll().stream()
			.map(this::convertToResponseDTO)
			.collect(Collectors.toList());
	}


	public SpesaResponseDTO getSpesaById(Long id) {
		Spesa spesa = spesaRepository.findById(id)
			.orElseThrow(() -> new EntityNotFoundException("Spesa non trovata con ID: " + id));
		return convertToResponseDTO(spesa);
	}


	public SpesaResponseDTO createSpesa(SpesaRequestDTO dto) {
		Spesa spesa = new Spesa();
		BeanUtils.copyProperties(dto, spesa);
		Spesa savedSpesa = spesaRepository.save(spesa);
		return convertToResponseDTO(savedSpesa);
	}


	public SpesaResponseDTO updateSpesa(Long id, SpesaRequestDTO dto) {
		Spesa spesa = spesaRepository.findById(id)
			.orElseThrow(() -> new EntityNotFoundException("Spesa non trovata con ID: " + id));


		BeanUtils.copyProperties(dto, spesa, "id");
		Spesa updatedSpesa = spesaRepository.save(spesa);
		return convertToResponseDTO(updatedSpesa);
	}


	public void deleteSpesa(Long id) {
		if (!spesaRepository.existsById(id)) {
			throw new EntityNotFoundException("Spesa non trovata con ID: " + id);
		}
		spesaRepository.deleteById(id);
	}

	public List<SpesaResponseDTO> getSpeseFiltrate(Integer anno, Integer mese, Spesa.CategoriaSpesa categoria) {
		if (anno != null && mese != null) {
			YearMonth ym = YearMonth.of(anno, mese);
			LocalDate inizio = ym.atDay(1);
			LocalDate fine = ym.atEndOfMonth();

			return spesaRepository.findAll().stream()
				.filter(s -> categoria == null || s.getCategoria() == categoria)
				.filter(s -> s.getDataSpesa() != null &&
					!s.getDataSpesa().isBefore(inizio) &&
					!s.getDataSpesa().isAfter(fine))
				.map(this::convertToResponseDTO)
				.collect(Collectors.toList());
		}


		return spesaRepository.findAll().stream()
			.filter(s -> categoria == null || s.getCategoria() == categoria)
			.map(this::convertToResponseDTO)
			.collect(Collectors.toList());
	}



	private SpesaResponseDTO convertToResponseDTO(Spesa spesa) {
		SpesaResponseDTO dto = new SpesaResponseDTO();
		BeanUtils.copyProperties(spesa, dto);
		return dto;
	}
}
