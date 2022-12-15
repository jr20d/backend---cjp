package com.cjp.dto;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;

import lombok.Getter;
import lombok.Setter;

public class JWTUsuarioResponse {
	@Getter
	@Setter
	private String token;
	@Getter
	private final String BEARER = "Bearer";
	@Getter
	@Setter
	private String usuario;
	@Getter
	@Setter
	private Collection<? extends GrantedAuthority> authorities;
}