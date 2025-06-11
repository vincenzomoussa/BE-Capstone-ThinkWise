package it.epicode.exception;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class ExceptionHandlerClass extends ResponseEntityExceptionHandler {

	private static final Logger logger = LoggerFactory.getLogger(ExceptionHandlerClass.class);

	@ExceptionHandler(EntityNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public Map<String, String> handleEntityNotFound(EntityNotFoundException ex) {
		logger.error("Errore: Entità non trovata - {}", ex.getMessage());
		Map<String, String> error = new HashMap<>();
		error.put("message", ex.getMessage());
		return error;
	}


	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<Map<String, String>> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
		logger.error("Violazione di integrità dati: {}", ex.getMessage());

		String userMessage = "Hai già inserito un pagamento per questa mensilità!";

		// Se vuoi essere più specifico, puoi controllare il messaggio dell'eccezione:
		if (ex.getMessage() != null && ex.getMessage().contains("pagamenti_studente_id_mensilita_saldata_key")) {
			userMessage = "Hai già inserito un pagamento per questa mensilità!";
		}

		Map<String, String> error = new HashMap<>();
		error.put("message", userMessage);
		return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(value = SecurityException.class)
	protected ResponseEntity<Map<String, String>> handleSecurityException(SecurityException ex) {
		logger.warn("Tentativo di accesso non autorizzato: {}", ex.getMessage());
		Map<String, String> error = new HashMap<>();
		error.put("message", ex.getMessage());
		return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
	}

	@ExceptionHandler(value = EntityExistsException.class)
	protected ResponseEntity<Map<String, String>> handleEntityExists(EntityExistsException ex) {
		logger.error("Errore: Entità già esistente - {}", ex.getMessage());
		Map<String, String> error = new HashMap<>();
		error.put("message", ex.getMessage());
		return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(value = AccessDeniedException.class)
	protected ResponseEntity<Map<String, String>> handleAccessDenied(AccessDeniedException ex) {
		logger.warn("Accesso negato: {}", ex.getMessage());
		Map<String, String> error = new HashMap<>();
		error.put("message", ex.getMessage());
		return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
	}

	@ExceptionHandler(value = JwtTokenMissingException.class)
	protected ResponseEntity<Map<String, String>> handleJwtTokenMissing(JwtTokenMissingException ex) {
		logger.error("Token JWT mancante o non valido: {}", ex.getMessage());
		Map<String, String> error = new HashMap<>();
		error.put("message", ex.getMessage());
		return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
	}

	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<Map<String, String>> handleConstraintViolation(ConstraintViolationException ex) {
		logger.error("Errore di validazione: {}", ex.getMessage());

		Map<String, String> errors = new HashMap<>();
		for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
			String fieldName = violation.getPropertyPath().toString();
			if (fieldName.contains(".")) {
				fieldName = fieldName.substring(fieldName.lastIndexOf('.') + 1);
			}
			errors.put(fieldName, violation.getMessage());
		}
		errors.put("message", "Errore di validazione");
		return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<Map<String, String>> handleGenericException(Exception ex) {
		logger.error("Errore interno del server: {}", ex.getMessage(), ex);
		Map<String, String> error = new HashMap<>();
		error.put("message", ex.getMessage() != null ? ex.getMessage() : "Si è verificato un errore interno.");
		return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
	}
}