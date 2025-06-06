package it.epicode.exception;

import lombok.Data;

@Data
public class Error {
	private String message;
	private String details;
	private String status;
}
