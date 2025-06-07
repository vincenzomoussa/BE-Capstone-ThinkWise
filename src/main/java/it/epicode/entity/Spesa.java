package it.epicode.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "spese")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Spesa {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private CategoriaSpesa categoria;

	@Column(nullable = false)
	private double importo;

	@Column(nullable = false)
	private LocalDate dataSpesa;

	private String descrizione;


	public enum CategoriaSpesa {
		PERSONALE,
		MANUTENZIONE,
		FORMAZIONE,
		ASSICURAZIONE,
		ATTREZZATURE,
		TRASPORTO,
		ALTRO
	}

	// Metodo getter esplicito per categoria, in caso Lombok non lo generi correttamente
	public CategoriaSpesa getCategoria() {
		return categoria;
	}

}
