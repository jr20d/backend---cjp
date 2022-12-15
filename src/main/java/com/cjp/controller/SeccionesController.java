package com.cjp.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cjp.dto.RespuestaDto;
import com.cjp.model.Seccion;
import com.cjp.service.SeccionService;
import com.cjp.service.Validaciones;

@RestController
@RequestMapping("/api/secciones")
public class SeccionesController {
	@Autowired
	private SeccionService crud;
	private Validaciones validar = new Validaciones();
	
	@PreAuthorize("permitAll()")
	@GetMapping(consumes = "application/json", produces = "application/json", headers = "content-type=application/json")
	public List<Seccion> listado(){
		return crud.listadoSecciones();
	}
	
	@PreAuthorize("permitAll()")
	@CrossOrigin(origins = "https://cliente-cjp.herokuapp.com")
	@GetMapping(value = "/{id}", consumes = "application/json", produces = "application/json", headers = "content-type=application/json")
	public ResponseEntity<Object> listado(@PathVariable(name = "id", required = true) Integer id){
		if (id != null) {
			List<Seccion> secciones = crud.seccionesPorCategoria(id);
			if (secciones != null && !secciones.isEmpty()) {
				return new ResponseEntity<>(secciones, HttpStatus.OK);
			}
			else {
				return new ResponseEntity<>("No se encontraron resultados", HttpStatus.NOT_FOUND);
			}
		}
		else {
			return new ResponseEntity<>("Error en la petición", HttpStatus.BAD_REQUEST);
		}
	}
	
	@PreAuthorize("hasRole('ROLE_ADMIN') || hasRole('ROLE_COMUN')")
	@PutMapping(consumes = "application/json", produces = "application/json", headers = "content-type=application/json")
	public RespuestaDto actualizar(@RequestBody(required = true) Seccion seccion) {
		RespuestaDto dto = new RespuestaDto();
		
		if (validar.validarTexto(seccion.getTitulo()) && validar.validarTexto(seccion.getUrl())) {
			String respuesta = crud.guardarSeccion(seccion);
			dto.setOk((respuesta != null) ? true : false);
			dto.setMensaje((respuesta != null) ? respuesta : "No se pudo guardar los datos");
		}
		else {
			dto.setOk(false);
			dto.setMensaje("Faltan campos por completar");
		}
		return dto;
	}
}