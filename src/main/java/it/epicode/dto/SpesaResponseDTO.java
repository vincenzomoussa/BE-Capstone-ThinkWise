package it.epicode.dto;


import it.epicode.entity.Spesa;
import lombok.Data;

import java.time.LocalDate;

@Data
public class SpesaResponseDTO {
	private Long id;
	private Spesa.CategoriaSpesa categoria;
	private double importo;
	private LocalDate dataSpesa;
	private String descrizione;
}
