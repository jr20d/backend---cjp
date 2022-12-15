package com.cjp.service;

import java.util.ArrayList;
import java.util.Collection;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.cjp.dao.UsuarioDao;
import com.cjp.model.Usuario;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
@Transactional
public class UserDetailsServiceImpl implements UserDetailsService {
	@Autowired
	private UsuarioDao dao;
	
	public UserDetailsServiceImpl(UsuarioDao dao) {
		this.dao = dao;
	}
	
	@Override
	public UserDetails loadUserByUsername(String usuario) throws UsernameNotFoundException {
		Usuario u = dao.buscarUsuarioPorNombre(usuario);
		if (u == null) {
			log.info("Acceso no autorizado, usuario no válido");
			return null;
		}
		Collection<GrantedAuthority> roles = new ArrayList<GrantedAuthority>();
		
		roles.add(new SimpleGrantedAuthority(u.getRol().getRol()));
		
		return new User(u.getUsuario(), u.getPassword(), roles);
	}
	
}