package it.epicode.dto;


import it.epicode.entity.CorsoTipo;
import it.epicode.entity.Livello;
import it.epicode.entity.TipoCorso;
import lombok.Data;

import java.util.List;

@Data
public class CorsoResponseDTO {
	private Long id;
	private String nome;
	private TipoCorso tipoCorso;
	private CorsoTipo corsoTipo;
	private Livello livello;
	private String frequenza;
	private String giorno;
	private String orario;
	private String secondoGiorno;
	private String secondoOrario;
	private InsegnanteResponseDTO insegnante;
	private AulaResponseDTO aula;
	private List<StudenteResponseDTO> studenti;
	private boolean attivo;
}

