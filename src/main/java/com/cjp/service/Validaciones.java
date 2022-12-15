package com.cjp.service;

public class Validaciones {
	public boolean validarTexto(String cadena) {
		return (cadena.trim().length() >= 5);
	}
	
	public boolean validarTelefono(String cadena) {
		return (cadena.trim().matches("^\\d{4}(-\\d{4})?$") || "".equals(cadena.trim()));
	}
}