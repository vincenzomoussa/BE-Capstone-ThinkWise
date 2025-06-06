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

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class ExceptionHandlerClass extends ResponseEntityExceptionHandler {

	private static final Logger logger = LoggerFactory.getLogger(ExceptionHandlerClass.class);

	@ExceptionHandler(EntityNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public String handleEntityNotFound(EntityNotFoundException ex) {
		logger.error("Errore: Entità non trovata - {}", ex.getMessage());
		return ex.getMessage();
	}


	@ExceptionHandler(value = SecurityException.class)
	protected ResponseEntity<String> handleSecurityException(SecurityException ex) {
		logger.warn("Tentativo di accesso non autorizzato: {}", ex.getMessage());
		return new ResponseEntity<>(ex.getMessage(), HttpStatus.FORBIDDEN);
	}

	@ExceptionHandler(value = EntityExistsException.class)
	protected ResponseEntity<String> handleEntityExists(EntityExistsException ex) {
		logger.error("Errore: Entità già esistente - {}", ex.getMessage());
		return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(value = AccessDeniedException.class)
	protected ResponseEntity<String> handleAccessDenied(AccessDeniedException ex) {
		logger.warn("Accesso negato: {}", ex.getMessage());
		return new ResponseEntity<>(ex.getMessage(), HttpStatus.FORBIDDEN);
	}

	@ExceptionHandler(value = JwtTokenMissingException.class)
	protected ResponseEntity<String> handleJwtTokenMissing(JwtTokenMissingException ex) {
		logger.error("Token JWT mancante o non valido: {}", ex.getMessage());
		return new ResponseEntity<>(ex.getMessage(), HttpStatus.UNAUTHORIZED);
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
		return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<String> handleGenericException(Exception ex) {
		logger.error("Errore interno del server: {}", ex.getMessage(), ex);
		return new ResponseEntity<>("Si è verificato un errore interno.", HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
