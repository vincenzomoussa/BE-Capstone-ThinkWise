package it.epicode.service;


import it.epicode.dto.OreInsegnateMensiliDTO;
import it.epicode.dto.ReportDTO;
import it.epicode.entity.Corso;
import it.epicode.entity.Pagamento;
import it.epicode.entity.Spesa;
import it.epicode.repository.CorsoRepository;
import it.epicode.repository.PagamentoRepository;
import it.epicode.repository.SpesaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.text.Normalizer;

@Service
public class ReportService {

	@Autowired private CorsoRepository corsoRepository;

	@Autowired private PagamentoRepository pagamentoRepository;

	@Autowired private SpesaRepository spesaRepository;

	@Autowired private EmailService emailService;

	@Autowired private PdfReportService pdfReportService;

	@Value("${spring.mail.username}")
	private String adminEmail;


	public ReportDTO generaReportMensile(int anno, int mese) {
		YearMonth yearMonth = YearMonth.of(anno, mese);
		LocalDate startDate = yearMonth.atDay(1);
		LocalDate endDate = yearMonth.atEndOfMonth();
		return generaReport(startDate, endDate, "Mensile");
	}


	public ReportDTO generaReportAnnuale(int anno) {
		LocalDate startDate = LocalDate.of(anno, 1, 1);
		LocalDate endDate = LocalDate.of(anno, 12, 31);
		return generaReport(startDate, endDate, "Annuale");
	}


	private ReportDTO generaReport(LocalDate startDate, LocalDate endDate, String periodo) {
		ReportDTO report = new ReportDTO();
		report.setPeriodo(periodo);


		Map<String, Integer> oreInsegnate = calcolaOreInsegnateNelPeriodo(startDate, endDate);
		report.setOreInsegnate(oreInsegnate);


		int totaleOreInsegnate = oreInsegnate.values().stream().mapToInt(Integer::intValue).sum();
		report.setTotaleOreInsegnate(totaleOreInsegnate);


		String targetMeseAnno = formatMeseAnno(startDate);

		List<Pagamento> pagamenti = pagamentoRepository.findAll().stream()
			.filter(p -> p.getMensilitaSaldata() != null &&
				p.getMensilitaSaldata().toLowerCase().endsWith(String.valueOf(endDate.getYear())))
			.collect(Collectors.toList());


		Map<String, Double> pagamentiRicevuti = pagamenti.stream()
			.collect(Collectors.groupingBy(
				p -> p.getMetodoPagamento().toString(),
				Collectors.summingDouble(Pagamento::getImporto)
			));
		report.setPagamentiRicevuti(pagamentiRicevuti.isEmpty() ? Collections.emptyMap() : pagamentiRicevuti);


		List<Spesa> spese = spesaRepository.findByDataSpesaBetween(startDate, endDate);
		Map<String, Double> speseRegistrate = spese.stream()
			.collect(Collectors.groupingBy(
				s -> s.getCategoria().toString(),
				Collectors.summingDouble(Spesa::getImporto)
			));
		report.setSpeseRegistrate(speseRegistrate.isEmpty() ? Collections.emptyMap() : speseRegistrate);


		double totaleEntrate = pagamentiRicevuti.values().stream().mapToDouble(Double::doubleValue).sum();
		double totaleUscite = speseRegistrate.values().stream().mapToDouble(Double::doubleValue).sum();

		report.setTotaleEntrate(totaleEntrate);
		report.setTotaleUscite(totaleUscite);
		report.setBilancio(totaleEntrate - totaleUscite);


		System.out.println("📊 Report generato:");
		System.out.println("💰 Totale Entrate: " + totaleEntrate);
		System.out.println("📉 Totale Uscite: " + totaleUscite);
		System.out.println("📈 Bilancio Finale: " + report.getBilancio());
		System.out.println("🕒 Totale Ore Insegnate: " + totaleOreInsegnate);

		return report;
	}


	private Map<String, Integer> calcolaOreInsegnateNelPeriodo(LocalDate start, LocalDate end) {
		List<Corso> corsi = corsoRepository.findByAttivoTrue();

		Map<String, Integer> oreMap = new HashMap<>();

		for (Corso corso : corsi) {
			if (corso == null || corso.getInsegnante() == null || corso.getGiorno() == null || corso.getFrequenza() == null) continue;

			if (corso.getInsegnante().getNome() == null || corso.getInsegnante().getCognome() == null) continue;

			String nomeCompleto = corso.getInsegnante().getNome() + " " + corso.getInsegnante().getCognome();
			int orePerSettimana = corso.getFrequenza().equals("2 volte a settimana") ? 6 : 3;

			String giornoCorsoStr = normalizzaGiorno(corso.getGiorno());
			System.out.println("🐛 calcolaOreInsegnateNelPeriodo - Giorno normalizzato: " + giornoCorsoStr);
			if (giornoCorsoStr == null) continue;
			java.time.DayOfWeek giornoCorsoEnum = java.time.DayOfWeek.valueOf(giornoCorsoStr);

			long settimane = start.datesUntil(end.plusDays(1))
					.filter(date -> date.getDayOfWeek() == giornoCorsoEnum)
					.count();

			int totaleOre = (int) (orePerSettimana * settimane);
			oreMap.put(nomeCompleto, oreMap.getOrDefault(nomeCompleto, 0) + totaleOre);
		}

		return oreMap;
	}


	public Map<String, Integer> calcolaOreInsegnateAnnuali(int anno) {
		LocalDate startDate = LocalDate.of(anno, 1, 1);
		LocalDate endDate = LocalDate.of(anno, 12, 31);
		Map<String, Integer> oreInsegnate = new HashMap<>();
		List<Corso> corsi = corsoRepository.findByAttivoTrue();

		for (Corso corso : corsi) {
			if (corso == null || corso.getInsegnante() == null || corso.getGiorno() == null || corso.getFrequenza() == null) continue;

			if (corso.getInsegnante().getNome() == null || corso.getInsegnante().getCognome() == null) continue;

			String chiaveInsegnante = corso.getInsegnante().getNome() + " " + corso.getInsegnante().getCognome();
			int orePerSettimana = corso.getFrequenza().equals("2 volte a settimana") ? 6 : 3;

			String giornoCorsoStr = normalizzaGiorno(corso.getGiorno());
			System.out.println("🐛 calcolaOreInsegnateAnnuali - Giorno normalizzato: " + giornoCorsoStr);
			if (giornoCorsoStr == null) continue;
			java.time.DayOfWeek giornoCorsoEnum = java.time.DayOfWeek.valueOf(giornoCorsoStr);

			long settimaneNelPeriodo = startDate
				.datesUntil(endDate.plusDays(1))
				.filter(data -> data.getDayOfWeek() == giornoCorsoEnum)
				.count();

			int totaleOre = (int) (orePerSettimana * settimaneNelPeriodo);
			oreInsegnate.put(chiaveInsegnante, oreInsegnate.getOrDefault(chiaveInsegnante, 0) + totaleOre);
		}

		return oreInsegnate;
	}



	private String formatMeseAnno(LocalDate data) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy", new Locale("it"));
		return data.format(formatter).toLowerCase();
	}


	public String inviaReportMensile(int anno, int mese) {
		ReportDTO report = generaReportMensile(anno, mese);

		if (report.getOreInsegnate().isEmpty() &&
			report.getPagamentiRicevuti().isEmpty() &&
			report.getSpeseRegistrate().isEmpty()) {
			return "⚠️ Nessun dato disponibile per il report mensile di " + mese + "/" + anno;
		}

		byte[] pdfBytes = pdfReportService.generateReportPdf(report);
		String subject = "📊 Report Mensile - " + mese + "/" + anno;
		String body = "Ciao,\n\nIn allegato trovi il report mensile della scuola per " + mese + "/" + anno + ".\n\nSaluti,\nGestione Scuola";

		emailService.sendEmailWithAttachment(adminEmail, subject, body, pdfBytes, "report_mensile_" + anno + "_" + mese + ".pdf");

		return "✅ Email con il report mensile inviata con successo!";
	}

	private ReportDTO generaReportPerInsegnante(LocalDate startDate, LocalDate endDate, String periodo, Long insegnanteId) {
		ReportDTO report = new ReportDTO();
		report.setPeriodo(periodo);


		Map<String, Integer> oreInsegnate = new HashMap<>();
		List<Corso> corsi = corsoRepository.findByAttivoTrue().stream()
				.filter(c -> c.getInsegnante() != null && c.getInsegnante().getId().equals(insegnanteId))
				.collect(Collectors.toList());

		for (Corso corso : corsi) {
			String nomeCompleto = corso.getInsegnante().getNome() + " " + corso.getInsegnante().getCognome();
			int oreSettimana = corso.getFrequenza().equals("2 volte a settimana") ? 6 : 3;

			String giornoCorsoStr = normalizzaGiorno(corso.getGiorno());
			if (giornoCorsoStr == null) continue;
			java.time.DayOfWeek giornoCorsoEnum = java.time.DayOfWeek.valueOf(giornoCorsoStr);

			long settimane = startDate.datesUntil(endDate.plusDays(1))
					.filter(date -> date.getDayOfWeek() == giornoCorsoEnum)
					.count();

			int totaleOre = (int) (oreSettimana * settimane);
			oreInsegnate.put(nomeCompleto, oreInsegnate.getOrDefault(nomeCompleto, 0) + totaleOre);
		}

		report.setOreInsegnate(oreInsegnate);
		report.setTotaleOreInsegnate(oreInsegnate.values().stream().mapToInt(Integer::intValue).sum());


		report.setTotaleEntrate(0);
		report.setTotaleUscite(0);
		report.setBilancio(0);
		report.setPagamentiRicevuti(Collections.emptyMap());
		report.setSpeseRegistrate(Collections.emptyMap());

		return report;
	}
	public ReportDTO generaReportMensileInsegnante(int anno, int mese, Long insegnanteId) {
		LocalDate startDate = YearMonth.of(anno, mese).atDay(1);
		LocalDate endDate = YearMonth.of(anno, mese).atEndOfMonth();
		return generaReportPerInsegnante(startDate, endDate, "Mensile", insegnanteId);
	}

	public ReportDTO generaReportAnnualeInsegnante(int anno, Long insegnanteId) {
		LocalDate startDate = LocalDate.of(anno, 1, 1);
		LocalDate endDate = LocalDate.of(anno, 12, 31);
		return generaReportPerInsegnante(startDate, endDate, "Annuale", insegnanteId);
	}

	public OreInsegnateMensiliDTO getOreInsegnatePerInsegnanteMensili(int anno, int mese) {
		YearMonth yearMonth = YearMonth.of(anno, mese);
		LocalDate startDate = yearMonth.atDay(1);
		LocalDate endDate = yearMonth.atEndOfMonth();

		Map<String, Integer> oreMap = calcolaOreInsegnateNelPeriodo(startDate, endDate);

		List<String> nomiInsegnanti = new ArrayList<>(oreMap.keySet());
		List<Integer> oreTotali = new ArrayList<>(oreMap.values());

		return OreInsegnateMensiliDTO.builder()
				.nomiInsegnanti(nomiInsegnanti)
				.oreTotali(oreTotali)
				.build();
	}

	private ReportDTO generaReportFiltratoPerInsegnante(LocalDate start, LocalDate end, String periodo, Long insegnanteId) {
		ReportDTO report = new ReportDTO();
		report.setPeriodo(periodo);

		Map<String, Integer> oreInsegnate = calcolaOreInsegnateNelPeriodo(start, end);
		report.setOreInsegnate(oreInsegnate);
		report.setTotaleOreInsegnate(oreInsegnate.values().stream().mapToInt(Integer::intValue).sum());


		report.setPagamentiRicevuti(Collections.emptyMap());
		report.setSpeseRegistrate(Collections.emptyMap());
		report.setTotaleEntrate(0);
		report.setTotaleUscite(0);
		report.setBilancio(0);

		return report;
	}

	private String normalizzaGiorno(String giorno) {
		System.out.println("🐛 normalizzaGiorno - Input: " + giorno);

		String unaccentedGiorno = Normalizer.normalize(giorno, Normalizer.Form.NFD)
				.replaceAll("\\p{InCombiningDiacriticalMarks}", "");
		System.out.println("🐛 normalizzaGiorno - Unaccented: " + unaccentedGiorno);

		String upperCaseUnaccentedGiorno = unaccentedGiorno.toUpperCase();
		System.out.println("🐛 normalizzaGiorno - Uppercase Unaccented: " + upperCaseUnaccentedGiorno);

		String result = switch (upperCaseUnaccentedGiorno) {
			case "LUNEDI" -> "MONDAY";
			case "MARTEDI" -> "TUESDAY";
			case "MERCOLEDI" -> "WEDNESDAY";
			case "GIOVEDI" -> "THURSDAY";
			case "VENERDI" -> "FRIDAY";
			case "SABATO" -> "SATURDAY";
			case "DOMENICA" -> "SUNDAY";
			default -> null;
		};
		System.out.println("🐛 normalizzaGiorno - Output: " + result);
		return result;
	}

	public String inviaReportAnnuale(int anno) {
		ReportDTO report = generaReportAnnuale(anno);

		if (report.getOreInsegnate().isEmpty() &&
			report.getPagamentiRicevuti().isEmpty() &&
			report.getSpeseRegistrate().isEmpty()) {
			return "⚠️ Nessun dato disponibile per il report annuale del " + anno;
		}

		byte[] pdfBytes = pdfReportService.generateReportPdf(report);
		String subject = "📊 Report Annuale - " + anno;
		String body = "Ciao,\n\nIn allegato trovi il report annuale della scuola per l'anno " + anno + ".\n\nSaluti,\nGestione Scuola";

		emailService.sendEmailWithAttachment(adminEmail, subject, body, pdfBytes, "report_annuale_" + anno + ".pdf");

		return "✅ Email con il report annuale inviata con successo!";
	}
}
