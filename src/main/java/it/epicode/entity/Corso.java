package it.epicode.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "corsi")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Corso {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String nome;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private TipoCorso tipoCorso;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private CorsoTipo corsoTipo;

	@Column(nullable = false)
	private String frequenza;

	@Column(nullable = false)
	private String giorno;

	@Column(nullable = false)
	private String orario;

	@Column
	private String secondoGiorno;

	@Column
	private String secondoOrario;


	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Livello livello;

	@ManyToOne
	@JoinColumn(name = "insegnante_id")
	private Insegnante insegnante;

	@ManyToOne
	@JoinColumn(name = "aula_id")
	private Aula aula;

	@ManyToMany
	@JoinTable(
		name = "corso_studenti",
		joinColumns = @JoinColumn(name = "corso_id"),
		inverseJoinColumns = @JoinColumn(name = "studente_id")
	)
	private List<Studente> studenti;

	@Column(nullable = false)
	private boolean attivo = true;


	public Corso(Corso corsoOriginale, List<Studente> nuoviStudenti) {
		this.nome = corsoOriginale.getNome();
		this.tipoCorso = corsoOriginale.getTipoCorso();
		this.corsoTipo = corsoOriginale.getCorsoTipo();
		this.frequenza = corsoOriginale.getFrequenza();
		this.giorno = corsoOriginale.getGiorno();
		this.orario = corsoOriginale.getOrario();
		this.secondoGiorno = corsoOriginale.getSecondoGiorno();
		this.secondoOrario = corsoOriginale.getSecondoOrario();
		this.livello = corsoOriginale.getLivello();
		this.insegnante = corsoOriginale.getInsegnante();
		this.aula = corsoOriginale.getAula();
		this.attivo = true;
		this.studenti = nuoviStudenti;
	}


}
