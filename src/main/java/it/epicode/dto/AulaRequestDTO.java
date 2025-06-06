package it.epicode.dto;

import lombok.Data;

import java.util.Map;

@Data
public class AulaRequestDTO {
	private String nome;
	private int capienzaMax;
	private Map<String, String> disponibilita;
}
