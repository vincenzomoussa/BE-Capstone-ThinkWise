package it.epicode.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Entity
@Table(name = "aule")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Aula {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String nome;
	private int capienzaMax;

	@ElementCollection
	@CollectionTable(name = "aula_disponibilita", joinColumns = @JoinColumn(name = "aula_id"))
	@MapKeyColumn(name = "giorno")
	@Column(name = "orari_disponibili")
	private Map<String, String> disponibilita;

	public boolean isDisponibile(String giorno, String orario, List<Corso> corsiAttivi) {
		corsiAttivi.stream()
			.filter(c -> c.getAula() != null && c.getAula().getId().equals(this.id))
			.forEach(c -> System.out.println("📌 Corso occupato: " + " - " + c.getGiorno() + " - " + c.getOrario()));


		boolean eOccupata = corsiAttivi.stream()
			.anyMatch(c -> c.getAula() != null &&
				c.getAula().getId().equals(this.id) &&
				c.getGiorno().equals(giorno) &&
				c.getOrario().equals(orario));

		return !eOccupata;
	}

}

