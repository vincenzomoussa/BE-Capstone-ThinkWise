package it.epicode.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "pagamenti", uniqueConstraints = {
	@UniqueConstraint(columnNames = {"studente_id", "mensilitaSaldata"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pagamento {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "studente_id", nullable = false)
	private Studente studente;

	@Column(nullable = false)
	private LocalDate dataPagamento;

	@Column(nullable = false)
	private double importo;

	@Column(nullable = false)
	private String mensilitaSaldata;

	@Column(nullable = false, unique = true)
	private String numeroRicevuta;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private MetodoPagamento metodoPagamento;

	private String note;



}
