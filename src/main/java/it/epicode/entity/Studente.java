package it.epicode.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "studenti")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Studente {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String nome;

	@Column(nullable = false)
	private String cognome;

	@Column(nullable = false, unique = true)
	private String email;

	@Column(nullable = false)
	private int eta;

	@Column(nullable = false)
	private LocalDate dataIscrizione;

	@ElementCollection(targetClass = CorsoTipo.class)
	@Enumerated(EnumType.STRING)
	@CollectionTable(name = "studente_preferenzaCorso", joinColumns = @JoinColumn(name = "studente_id"))
	@Column(name = "preferenza_corso")
	private Set<CorsoTipo> preferenzaCorso;

	@ElementCollection
	private Set<String> giorniPreferiti;

	@ElementCollection
	private Set<String> fasceOrariePreferite;

	@OneToMany(mappedBy = "studente", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Pagamento> pagamenti;

	@ManyToMany(mappedBy = "studenti")
	private List<Corso> corsi = new ArrayList<>();

}
