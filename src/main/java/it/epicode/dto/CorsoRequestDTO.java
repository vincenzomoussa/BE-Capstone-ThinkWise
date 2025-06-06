package it.epicode.dto;


import it.epicode.entity.CorsoTipo;
import it.epicode.entity.Livello;
import it.epicode.entity.TipoCorso;
import lombok.Data;

import java.util.List;

@Data
public class CorsoRequestDTO {
	private String nome;
	private TipoCorso tipoCorso;
	private CorsoTipo corsoTipo;
	private Livello livello;
	private String frequenza;
	private String giorno;
	private String orario;
	private String secondoGiorno;
	private String secondoOrario;
	private Long insegnanteId;
	private Long aulaId;
	private List<Long> studentiIds;
}
