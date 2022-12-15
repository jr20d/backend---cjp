package com.cjp.dto;

import lombok.Getter;
import lombok.Setter;

public class JWTUsuarioRequest {
	@Getter
	@Setter
	private String usuario;
	
	@Getter
	@Setter
	private String password;
	
	@Getter
	@Setter
	private String token;
}