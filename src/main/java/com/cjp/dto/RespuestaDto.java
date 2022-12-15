package com.cjp.dto;

public class RespuestaDto {
	private boolean ok;
	private String mensaje;
	
	public RespuestaDto() {}

	public boolean getOk() {
		return ok;
	}
	
	public void setOk(boolean ok) {
		this.ok = ok;
	}
	
	public String getMensaje() {
		return mensaje;
	}

	public void setMensaje(String mensaje) {
		this.mensaje = mensaje;
	}
}