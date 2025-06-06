package it.epicode.dto;
import it.epicode.entity.CorsoTipo;
import jakarta.persistence.Column;
import lombok.Data;

import java.time.LocalDate;
import java.util.Set;

@Data
public class InsegnanteRequestDTO {
	private String nome;
	private String cognome;
	private String email;
	private int eta;
	private Set<CorsoTipo> specializzazioni;
	private LocalDate dataAssunzione;
	private Set<String> giorniDisponibili;
	private Set<String> fasceOrarieDisponibili;
}
