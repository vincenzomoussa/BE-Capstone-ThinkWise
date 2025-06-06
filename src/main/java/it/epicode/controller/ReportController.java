package it.epicode.controller;


import it.epicode.dto.CorsoRequestDTO;
import it.epicode.dto.CorsoResponseDTO;
import it.epicode.dto.OreInsegnateMensiliDTO;
import it.epicode.dto.ReportDTO;
import it.epicode.service.PdfReportService;
import it.epicode.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/report")
public class ReportController {

	@Autowired
	private ReportService reportService;

	@Autowired
	private PdfReportService pdfReportService;


	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@GetMapping("/mensile")
	public ResponseEntity<ReportDTO> getReportMensile(@RequestParam int anno, @RequestParam int mese) {
		return ResponseEntity.ok(reportService.generaReportMensile(anno, mese));
	}


	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@GetMapping("/mensile/pdf")
	public ResponseEntity<byte[]> downloadReportMensile(@RequestParam int anno, @RequestParam int mese) {
		ReportDTO report = reportService.generaReportMensile(anno, mese);
		byte[] pdfBytes = pdfReportService.generateReportPdf(report);

		return ResponseEntity.ok()
			.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=report_mensile_" + anno + "_" + mese + ".pdf")
			.contentType(MediaType.APPLICATION_PDF)
			.body(pdfBytes);
	}


	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@GetMapping("/annuale/pdf")
	public ResponseEntity<byte[]> downloadReportAnnuale(@RequestParam int anno) {
		ReportDTO report = reportService.generaReportAnnuale(anno);
		byte[] pdfBytes = pdfReportService.generateReportPdf(report);

		return ResponseEntity.ok()
			.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=report_annuale_" + anno + ".pdf")
			.contentType(MediaType.APPLICATION_PDF)
			.body(pdfBytes);
	}


	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@GetMapping("/annuale/{anno}")
	public ReportDTO getReportAnnuale(@PathVariable int anno) {
		return reportService.generaReportAnnuale(anno);
	}


	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@GetMapping("/annuale/{anno}/email")
	public ResponseEntity<String> inviaReportAnnuale(@PathVariable int anno) {
		String result = reportService.inviaReportAnnuale(anno);
		return ResponseEntity.ok(result);
	}
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@GetMapping("/insegnante")
	public ResponseEntity<ReportDTO> getReportInsegnante(
			@RequestParam int anno,
			@RequestParam(required = false) Integer mese,
			@RequestParam Long insegnanteId
	) {
		ReportDTO report = (mese != null)
				? reportService.generaReportMensileInsegnante(anno, mese, insegnanteId)
				: reportService.generaReportAnnualeInsegnante(anno, insegnanteId);

		return ResponseEntity.ok(report);
	}
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@GetMapping("/insegnante/pdf")
	public ResponseEntity<byte[]> downloadReportInsegnante(
			@RequestParam int anno,
			@RequestParam(required = false) Integer mese,
			@RequestParam Long insegnanteId
	) {

		ReportDTO report = (mese != null)
				? reportService.generaReportMensileInsegnante(anno, mese, insegnanteId)
				: reportService.generaReportAnnualeInsegnante(anno, insegnanteId);

		byte[] pdfBytes = pdfReportService.generateReportPdf(report);

		String filename = (mese != null)
				? "report_insegnante_" + anno + "_" + mese + "_id" + insegnanteId + ".pdf"
				: "report_insegnante_" + anno + "_id" + insegnanteId + ".pdf";

		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
				.contentType(MediaType.APPLICATION_PDF)
				.body(pdfBytes);
	}


	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@PostMapping("/mensile/email")
	public String inviaReportMensile(@RequestParam int anno, @RequestParam int mese) {
		reportService.inviaReportMensile(anno, mese);
		return "Email con il report mensile inviata con successo!";
	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@GetMapping("/ore-insegnate-mensili")
	public ResponseEntity<OreInsegnateMensiliDTO> getOreInsegnateMensili(@RequestParam int anno, @RequestParam int mese) {
		return ResponseEntity.ok(reportService.getOreInsegnatePerInsegnanteMensili(anno, mese));
	}
}
