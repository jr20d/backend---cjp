package com.cjp.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cjp.dto.RespuestaDto;
import com.cjp.model.Telefono;
import com.cjp.service.TelefonoService;
import com.cjp.service.Validaciones;

@RestController
@RequestMapping("/api/telefonos")
public class TelefonosController {
	@Autowired
	TelefonoService crud;
	
	private Validaciones validacion = new Validaciones();
	
	@CrossOrigin(origins = "https://cliente-cjp.herokuapp.com")
	@PreAuthorize("permitAll()")
	@GetMapping()
	public List<Telefono> telefonos() {
		return crud.listado();
	}
	
	@PreAuthorize("hasRole('ROLE_ADMIN') || hasRole('ROLE_COMUN')")
	@PutMapping(consumes = "application/json", produces = "application/json", headers = "content-type=application/json")
	public RespuestaDto actualizarTelefonos(@RequestBody(required = true) List<Telefono> telefonos) {
		RespuestaDto dto = new RespuestaDto();
		
		telefonos.forEach(telefono -> {
			String resultado;
			if (validacion.validarTelefono(telefono.getTelefono())){
				resultado = crud.guardar(telefono);
				
				dto.setOk(true);
				dto.setMensaje((resultado == null) ? "No se pudo guardar los datos" : resultado);
			}
			else {
				dto.setOk(false);
				dto.setMensaje("Error en el formato del telefono");
			}
		});
		return dto;
	}
}