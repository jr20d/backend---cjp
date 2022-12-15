package com.cjp.dto;

public class ErrorDto {
	private int status;
	private String error;
	private String contenido;
	
	public ErrorDto(int status, String error, String contenido) {
		this.status = status;
		this.error = error;
		this.contenido = contenido;
	}

	public int getStatus() {
		return status;
	}

	public String getError() {
		return error;
	}

	public String getContenido() {
		return contenido;
	}
}