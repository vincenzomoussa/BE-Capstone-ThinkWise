package it.epicode.dto;

import it.epicode.entity.MetodoPagamento;
import lombok.Data;

import java.time.LocalDate;

@Data
public class PagamentoResponseDTO {
	private Long id;
	private String studenteNome;
	private LocalDate dataPagamento;
	private double importo;
	private String mensilitaSaldata;
	private MetodoPagamento metodoPagamento;
	private String numeroRicevuta;
	private String note;
}
