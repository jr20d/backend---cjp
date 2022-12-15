package com.cjp.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cjp.dto.RespuestaDto;
import com.cjp.dto.UsuarioDto;
import com.cjp.model.Usuario;
import com.cjp.service.UsuarioService;
import com.cjp.service.Validaciones;

@RestController
@PreAuthorize("hasRole('ROLE_ADMIN')")
@RequestMapping("/api/usuarios")
public class UsuariosController {
	@Autowired
	private UsuarioService crud;
	private Validaciones validar = new Validaciones();
	
	@GetMapping(consumes = "application/json", produces = "application/json", headers = "content-type=application/json")
	public List<UsuarioDto> listado(){
		return crud.listadoUsuarios();
	}
	
	@PostMapping(consumes = "application/json", produces = "application/json", headers = "content-type=application/json")
	public RespuestaDto agregar(@RequestBody(required = true) Usuario usuario, BindingResult result) {
		RespuestaDto dto = new RespuestaDto();
		if (!result.hasErrors()) {
			if (usuario.getUsuario().trim().length() > 0 && validar.validarTexto(usuario.getPassword())) {
				if (!crud.existeUsuario(usuario.getUsuario().trim().toLowerCase())) {
					String resultado = crud.agregar(usuario);
					
					dto.setOk(resultado != null ? true : false);
					dto.setMensaje(resultado != null ? resultado : "Error al agregar al usuario");
				}
				else {
					dto.setOk(false);
					dto.setMensaje("El usuario ingresado ya existe");
				}
				return dto;
			}
			dto.setOk(false);
			dto.setMensaje("Faltan campos por completar");
			return dto;
		}
		dto.setOk(false);
		dto.setMensaje("Error en los valores enviados");
		return dto;
	}
	
	@PutMapping(consumes = "application/json", produces = "application/json", headers = "content-type=application/json")
	public RespuestaDto actualizar(@RequestBody(required = true) Usuario usuario, BindingResult result) {
		RespuestaDto dto = new RespuestaDto();
		if (!result.hasErrors()) {
			if (usuario.getUsuario().trim().length() > 0) {
				String resultado = crud.actualizar(usuario);
				
				dto.setOk(resultado != null ? true : false);
				dto.setMensaje(resultado != null ? resultado : "Error al actualizar los datos del usuario");
			}
			else {
				dto.setOk(false);
				dto.setMensaje("Faltan campos por completar");
			}
			return dto;
		}
		dto.setOk(false);
		dto.setMensaje("Error en los valores enviados");
		return dto;
	}
	
	@DeleteMapping(consumes = "application/json", produces = "application/json", headers = "content-type=application/json")
	public RespuestaDto eliminar(@RequestBody(required = true) Usuario usuario, BindingResult result) {
		RespuestaDto dto = new RespuestaDto();
		if (!result.hasErrors()) {
			if (usuario.getUsuario().trim().length() > 0) {
				String resultado = crud.eliminar(usuario);
				
				dto.setOk(resultado != null ? true : false);
				dto.setMensaje(resultado != null ? resultado : "Error al eliminar el usuario");
			}
			else {
				dto.setOk(false);
				dto.setMensaje("Los datos son erroneos");
			}
			return dto;
		}
		dto.setOk(false);
		dto.setMensaje("Error en los valores enviados");
		return dto;
	}
}