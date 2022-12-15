package com.cjp.dto;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.cjp.model.Usuario;

import lombok.Getter;

public class UsuarioSecurity {
	@Getter
	private String usuario;
	@Getter
	private String password;
	@Getter	
	private Collection<? extends GrantedAuthority> authorities;
	
	public UsuarioSecurity(String usuario, String password, Collection<? extends GrantedAuthority> authorities) {
		this.usuario = usuario;
		this.password = password;
		this.authorities = authorities;
	}
	
	public static UsuarioSecurity build(Usuario usuario) {
		List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
		
		authorities.add(new SimpleGrantedAuthority(usuario.getRol().getRol()));
		
		return new UsuarioSecurity(usuario.getUsuario(), usuario.getPassword(), authorities);
	}
}