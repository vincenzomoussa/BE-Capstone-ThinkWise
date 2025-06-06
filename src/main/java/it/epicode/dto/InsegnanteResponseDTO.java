package it.epicode.dto;

import it.epicode.entity.CorsoTipo;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Set;

@Data
@NoArgsConstructor
public class InsegnanteResponseDTO {
	private Long id;
	private String nome;
	private String cognome;
	private String email;
	private int eta;
	private LocalDate dataAssunzione;
	private Set<CorsoTipo> specializzazioni;
	private Set<String> giorniDisponibili;
	private Set<String> fasceOrarieDisponibili;
	private int oreMensili;

	public InsegnanteResponseDTO(Long id, String nome, String cognome) {
		this.id = id;
		this.nome = nome;
		this.cognome = cognome;
	}
}
