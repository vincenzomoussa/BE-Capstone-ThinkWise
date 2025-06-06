package it.epicode.dto;

import lombok.Data;

import java.util.Map;

@Data
public class ReportDTO {
	private Map<String, Integer> oreInsegnate;
	private Map<String, Double> pagamentiRicevuti;
	private Map<String, Double> speseRegistrate;
	private double bilancio;
	private String periodo;
	private double totaleEntrate;
	private double totaleUscite;
	private int totaleOreInsegnate;
}
