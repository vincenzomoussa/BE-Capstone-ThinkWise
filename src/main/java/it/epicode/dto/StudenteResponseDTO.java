package it.epicode.dto;

import it.epicode.entity.CorsoTipo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
public class StudenteResponseDTO {
	private Long id;
	private String nome;
	private String cognome;
	private String email;
	private int eta;
	private LocalDate dataIscrizione;
	private Set<CorsoTipo> preferenzaCorso;
	private Set<String> giorniPreferiti;
	private Set<String> fasceOrariePreferite;
	private List<CorsoAttivo> corsi;

	// Constructor for mapping in CorsoService
	public StudenteResponseDTO(Long id, String nome, String cognome) {
		this.id = id;
		this.nome = nome;
		this.cognome = cognome;
	}

	@Data
	@NoArgsConstructor
	public static class CorsoAttivo {
		private Long id;
		private String nome;
		private boolean attivo;

		public CorsoAttivo(Long id, String nome, boolean attivo) {
			this.id = id;
			this.nome = nome;
			this.attivo = attivo;
		}
	}
}
