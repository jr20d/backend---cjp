package com.cjp.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cjp.dto.RespuestaDto;
import com.cjp.model.Institucion;
import com.cjp.service.InstitucionService;

@RestController
@PreAuthorize("hasRole('ROLE_ADMIN') || hasRole('ROLE_COMUN')")
@RequestMapping("/api/instituciones")
public class InstitucionesController {
	@Autowired
	private InstitucionService crud;
	
	@CrossOrigin(origins = "https://cliente-cjp.herokuapp.com")
	@PreAuthorize("permitAll()")
	@GetMapping(consumes = "application/json", produces = "application/json", headers = "content-type=application/json")
	public List<Institucion> listadoInstituciones(){
		return crud.listadoInstituciones();
	}
	
	@PostMapping(consumes = "application/json", produces = "application/json", headers = "content-type=application/json")
	public RespuestaDto agregar(@RequestBody(required = true) Institucion institucion) {
		return guardarDatos(institucion);
	}
	
	@PutMapping(consumes = "application/json", produces = "application/json", headers = "content-type=application/json")
	public RespuestaDto actualizar(@RequestBody(required = true) Institucion institucion) {
		return guardarDatos(institucion);
	}
	
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@DeleteMapping(consumes = "application/json", produces = "application/json", headers = "content-type=application/json")
	public RespuestaDto eliminar(@RequestBody(required = true) Institucion institucion) {
		RespuestaDto dto = new RespuestaDto();
		
		if (institucion.getIdInstitucion() > 0) {
			String respuesta = crud.eliminar(institucion);
			
			dto.setOk((respuesta != null) ? true : false);
			dto.setMensaje((respuesta != null) ? respuesta : "Error al eliminar los datos");
		}
		else {
			dto.setOk(false);
			dto.setMensaje("Error en los parametros enviados");
		}
		
		return dto;
	}
	
	//Método para guardar los datos
	public RespuestaDto guardarDatos(Institucion institucion) {
		RespuestaDto dto = new RespuestaDto();
		
		if (institucion.getNombre().trim().length() > 0) {
			String respuesta = crud.guardar(institucion);
			
			dto.setOk((respuesta != null) ? true : false);
			dto.setMensaje((respuesta != null) ? respuesta : "No se pudo guardar los datos");
		}
		else {
			dto.setOk(false);
			dto.setMensaje("No se pudo guardar los cambios");
		}
		return dto;
	}
}