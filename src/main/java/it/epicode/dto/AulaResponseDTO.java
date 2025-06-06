package it.epicode.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
public class AulaResponseDTO {
	private Long id;
	private String nome;
	private int capienzaMax;
	private Map<String, String> disponibilita;

	public AulaResponseDTO(Long id, String nome, int capienzaMax) {
		this.id = id;
		this.nome = nome;
		this.capienzaMax = capienzaMax;
	}
}
