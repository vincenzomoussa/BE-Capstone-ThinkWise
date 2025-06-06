package it.epicode.dto;


import it.epicode.entity.CorsoTipo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CalendarioDTO {
	private Long corsoId;
	private CorsoTipo corsoTipo;
	private String frequenza;
	private String giorno;
	private String orario;
	private String aula;
	private String insegnante;
	private String livello;
}
