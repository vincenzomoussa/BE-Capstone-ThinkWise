package it.epicode.service;


import it.epicode.entity.Corso;
import it.epicode.entity.Pagamento;
import it.epicode.repository.CorsoRepository;
import it.epicode.repository.InsegnanteRepository;
import it.epicode.repository.PagamentoRepository;
import it.epicode.repository.StudenteRepository;
import it.epicode.repository.SpesaRepository;
import it.epicode.entity.Spesa;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ThinkBoardService {

	@Autowired
	private StudenteRepository studenteRepository;

	@Autowired
	private CorsoRepository corsoRepository;

	@Autowired
	private PagamentoRepository pagamentoRepository;

	@Autowired
	private SpesaRepository spesaRepository;

	@Autowired
	private InsegnanteRepository insegnanteRepository;

	public Map<String, Object> getStats() {
		long studenti = studenteRepository.count();
		long corsiAttivi = corsoRepository.findByAttivoTrue().size();
		double pagamenti = pagamentoRepository.getTotalePagamenti();
		long insegnanti = insegnanteRepository.count();
		double totaleSpese = spesaRepository.findAll().stream()
			.mapToDouble(Spesa::getImporto)
			.sum();

		return Map.of(
			"studenti", studenti,
			"corsi", corsiAttivi,
			"pagamenti", pagamenti,
			"insegnanti", insegnanti,
			"spese", totaleSpese
		);
	}

	public List<Map<String, String>> getAvvisi() {
		List<Map<String, String>> avvisi = new ArrayList<>();

		long studentiSenzaCorso = studenteRepository.findAll().stream()
			.filter(s -> s.getCorsi() == null || s.getCorsi().stream().noneMatch(Corso::isAttivo))
			.count();

		if (studentiSenzaCorso > 0) {
			avvisi.add(Map.of(
				"messaggio", "Ci sono " + studentiSenzaCorso + " studenti non iscritti a un corso.",
				"link", "/studenti"
			));
		}

		long corsiDisattivati = corsoRepository.findAll().stream()
			.filter(c -> !c.isAttivo())
			.count();

		if (corsiDisattivati > 0) {
			avvisi.add(Map.of(
				"messaggio", corsiDisattivati + " corsi sono attualmente disattivati.",
				"link", "/corsi"
			));
		}

		long corsiConPochiStudenti = corsoRepository.findByAttivoTrue().stream()
			.filter(c -> c.getStudenti().size() < 3)
			.count();

		if (corsiConPochiStudenti > 0) {
			avvisi.add(Map.of(
				"messaggio",  + corsiConPochiStudenti + " corsi hanno meno di 3 studenti.",
				"link", "/corsi"
			));
		}

		long studentiConPagamentiMancanti = studenteRepository.findAll().stream()
			.filter(s -> pagamentoRepository.findByStudenteId(s.getId()).isEmpty())
			.count();

		if (studentiConPagamentiMancanti > 0) {
			avvisi.add(Map.of(
				"messaggio",  studentiConPagamentiMancanti + " studenti non hanno ancora effettuato alcun pagamento.",
				"link", "/studenti"
			));
		}

		return avvisi;
	}

	public Map<String, Object> getPagamentiMensili() {
		List<Pagamento> pagamenti = pagamentoRepository.findAll();
		Map<Month, Double> mappa = new LinkedHashMap<>();


		for (Month m : Month.values()) {
			mappa.put(m, 0.0);
		}

		int annoCorrente = java.time.Year.now().getValue();

		for (Pagamento pagamento : pagamenti) {
			String mensilita = pagamento.getMensilitaSaldata();
			if (mensilita == null || !mensilita.contains(" ")) {
				System.err.println("Errore: Formato mensilità pagamento non valido: " + mensilita);
				continue; // Salta questo pagamento
			}

			String[] parts = mensilita.split(" ");
			if (parts.length != 2) {
				System.err.println("Errore: Formato mensilità pagamento non valido (split): " + mensilita);
				continue; // Salta questo pagamento
			}
			String monthName = parts[0];
			int year;
			try {
				year = Integer.parseInt(parts[1]);
			} catch (NumberFormatException e) {
				System.err.println("Errore: Anno non numerico in mensilità pagamento: " + mensilita);
				continue; // Salta questo pagamento
			}

			Month meseEnum = null;
			for (Month m : Month.values()) {
				if (m.getDisplayName(java.time.format.TextStyle.FULL, Locale.ITALIAN).equalsIgnoreCase(monthName)) {
					meseEnum = m;
					break;
				}
			}

			if (meseEnum == null) {
				System.err.println("Errore: Mese non riconosciuto in mensilità pagamento: " + mensilita);
				continue; // Salta questo pagamento
			}

			if (year == annoCorrente) {
				mappa.put(meseEnum, mappa.get(meseEnum) + pagamento.getImporto());
			}
		}


		List<String> mesi = mappa.keySet().stream()
			.map(m -> m.getDisplayName(java.time.format.TextStyle.FULL, Locale.ITALIAN))
			.collect(Collectors.toList());

		List<Double> importi = new ArrayList<>(mappa.values());

		return Map.of("mesi", mesi, "importi", importi);
	}

	public Map<String, Object> getEntrateUscite() {
		List<Pagamento> pagamenti = pagamentoRepository.findAll();
		List<Spesa> spese = spesaRepository.findAll();

		Map<Month, Double> entratePerMese = new LinkedHashMap<>();
		Map<Month, Double> uscitePerMese = new LinkedHashMap<>();

		int annoCorrente = LocalDate.now().getYear();
		LocalDate today = LocalDate.now();
		LocalDate sixMonthsAgo = today.minusMonths(5).withDayOfMonth(1); // Start of the month 6 months ago

		// Initialize maps for the last 6 months
		for (int i = 0; i < 6; i++) {
			Month month = today.minusMonths(i).getMonth();
			entratePerMese.put(month, 0.0);
			uscitePerMese.put(month, 0.0);
		}

		// Calcola entrate
		for (Pagamento pagamento : pagamenti) {
			String mensilita = pagamento.getMensilitaSaldata();
			if (mensilita == null || !mensilita.contains(" ")) {
				System.err.println("Errore: Formato mensilità pagamento non valido: " + mensilita);
				continue; // Salta questo pagamento
			}

			String[] parts = mensilita.split(" ");
			if (parts.length != 2) {
				System.err.println("Errore: Formato mensilità pagamento non valido (split): " + mensilita);
				continue; // Salta questo pagamento
			}
			String monthName = parts[0];
			int year;
			try {
				year = Integer.parseInt(parts[1]);
			} catch (NumberFormatException e) {
				System.err.println("Errore: Anno non numerico in mensilità pagamento: " + mensilita);
				continue; // Salta questo pagamento
			}

			Month meseEnum = null;
			for (Month m : Month.values()) {
				if (m.getDisplayName(java.time.format.TextStyle.FULL, Locale.ITALIAN).equalsIgnoreCase(monthName)) {
					meseEnum = m;
					break;
				}
			}

			if (meseEnum == null) {
				System.err.println("Errore: Mese non riconosciuto in mensilità pagamento: " + mensilita);
				continue; // Salta questo pagamento
			}

			LocalDate paymentDate = LocalDate.of(year, meseEnum, 1);
			if (!paymentDate.isBefore(sixMonthsAgo) && !paymentDate.isAfter(today.withDayOfMonth(today.lengthOfMonth()))) {
				entratePerMese.put(meseEnum, entratePerMese.get(meseEnum) + pagamento.getImporto());
			}
		}

		// Calcola uscite
		for (Spesa spesa : spese) {
			if (!spesa.getDataSpesa().isBefore(sixMonthsAgo) && !spesa.getDataSpesa().isAfter(today.withDayOfMonth(today.lengthOfMonth()))) {
				Month mese = spesa.getDataSpesa().getMonth();
				uscitePerMese.put(mese, uscitePerMese.get(mese) + spesa.getImporto());
			}
		}

		// Sort months to ensure correct order
		List<Month> orderedMonths = new ArrayList<>();
		for (int i = 5; i >= 0; i--) {
			orderedMonths.add(today.minusMonths(i).getMonth());
		}

		List<String> mesi = orderedMonths.stream()
			.map(m -> m.getDisplayName(java.time.format.TextStyle.FULL, Locale.ITALIAN))
			.collect(Collectors.toList());

		List<Double> entrate = orderedMonths.stream()
			.map(entratePerMese::get)
			.collect(Collectors.toList());
		List<Double> uscite = orderedMonths.stream()
			.map(uscitePerMese::get)
			.collect(Collectors.toList());

		return Map.of("mesi", mesi, "entrate", entrate, "uscite", uscite);
	}

	public Map<String, Object> getSpeseGenerali() {
		List<Spesa> spese = spesaRepository.findAll();
		Map<String, Double> spesePerCategoria = new LinkedHashMap<>();

		for (Spesa spesa : spese) {
			String categoria = spesa.getCategoria().name(); // Assumendo che CategoriaSpesa sia un enum
			spesePerCategoria.put(categoria, spesePerCategoria.getOrDefault(categoria, 0.0) + spesa.getImporto());
		}

		List<String> categorie = new ArrayList<>(spesePerCategoria.keySet());
		List<Double> importi = new ArrayList<>(spesePerCategoria.values());

		return Map.of("categorie", categorie, "importi", importi);
	}
}

