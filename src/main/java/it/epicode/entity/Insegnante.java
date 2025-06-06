package it.epicode.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Set;

@Entity
@Table(name = "insegnanti")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Insegnante {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String nome;

	@Column(nullable = false)
	private String cognome;

	@Column(nullable = false)
	private LocalDate dataAssunzione;

	@Column(nullable = false, unique = true)
	private String email;

	@ElementCollection(targetClass = CorsoTipo.class)
	@Enumerated(EnumType.STRING)
	@CollectionTable(name = "insegnante_specializzazioni", joinColumns = @JoinColumn(name = "insegnante_id"))
	@Column(name = "specializzazione")
	private Set<CorsoTipo> specializzazioni;

	@ElementCollection
	private Set<String> giorniDisponibili;

	@Column(nullable = false)
	private int eta;

	@ElementCollection
	private Set<String> fasceOrarieDisponibili;



	public boolean isDisponibile(String giorno, String orario) {
		return giorniDisponibili.contains(giorno) && fasceOrarieDisponibili.contains(orario);
	}
}
