package com.cjp.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cjp.dao.UsuarioDao;
import com.cjp.dto.UsuarioDto;
import com.cjp.model.Rol;
import com.cjp.model.Usuario;

@Service
public class UsuarioService{
	@Autowired
	private UsuarioDao dao;
	
	@Autowired
	private BCryptPasswordEncoder encoder;
	
	@Transactional(readOnly = true)
	public List<UsuarioDto> listadoUsuarios() {
		try {
			List<UsuarioDto> usuarios = new ArrayList<UsuarioDto>();
			dao.listadoUsuarios().forEach(u ->{
				UsuarioDto dto = new UsuarioDto();
				dto.setUsuario(u.getUsuario());
				dto.setRol(u.getRol().getRol().equals("ROLE_ADMIN") ? "Administrador" : "Común");
				usuarios.add(dto);
			});
			return usuarios;
		}
		catch(Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	@Transactional
	public String agregar(Usuario usuario) {
		try {
			usuario.setUsuario(usuario.getUsuario().trim().toLowerCase());
			Rol rol = new Rol();
			rol.setIdRol(usuario.getRol().getIdRol());
			rol.setRol(usuario.getRol().getIdRol() == 1 ? "ROLE_ADMIN" : "ROLE_COMUN");
			usuario.setRol(rol);
			usuario.setPassword(encoder.encode(usuario.getPassword()));
			dao.save(usuario);
			
			return "El usuario ha sido creado";
		}
		catch(Exception ex) {
			ex.printStackTrace();
			return null;
		}		
	}
	
	@Transactional
	public String actualizar(Usuario usuario) {
		try {
			String nuevoPassword = usuario.getPassword();
			int idNuevoRol = usuario.getRol().getIdRol();
			usuario = dao.buscarUsuarioPorNombre(usuario.getUsuario());
			
			if (nuevoPassword != null && !nuevoPassword.equals("")) {
				usuario.setPassword(encoder.encode(nuevoPassword));
			}
			
			if (idNuevoRol != usuario.getRol().getIdRol()) {
				Rol rol = new Rol();
				rol.setIdRol(idNuevoRol);
				rol.setRol(idNuevoRol == 1 ? "ROLE_ADMIN" : "ROLE_COMUN");
				usuario.setRol(rol);
			}
			
			return "Los datos del usuario se han actualizado";
		}
		catch(Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	@Transactional
	public String eliminar(Usuario usuario) {
		try {
			usuario = dao.buscarUsuarioPorNombre(usuario.getUsuario());
			dao.delete(usuario);
			return "El usuario ha sido eliminado";
		}
		catch(Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	@Transactional(readOnly = true)
	public boolean existeUsuario(String usuario) {
		return (dao.buscarUsuarioPorNombre(usuario) != null);
	}
	
	@Transactional
	public String cambiarPassword(String usuario, String password) {
		try {
			Usuario u = dao.buscarUsuarioPorNombre(usuario);
			if (u != null) {
				u.setPassword(encoder.encode(password));
				return "Se ha cambiado la contraseña";
			}
			else {
				return null;
			}
		}
		catch(Exception ex) {
			return null;
		}
	}
}