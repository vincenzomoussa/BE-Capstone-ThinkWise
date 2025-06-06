package it.epicode.dto;

import it.epicode.entity.CorsoTipo;
import it.epicode.entity.Pagamento;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Data
public class StudenteRequestDTO {
	private Long studenteId;
	private String nome;
	private String cognome;
	private String email;
	private int eta;
	private LocalDate dataIscrizione;
	private Set<CorsoTipo> preferenzaCorso;
	private Set<String> giorniPreferiti;
	private Set<String> fasceOrariePreferite;
}