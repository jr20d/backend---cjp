package com.cjp.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cjp.dto.RespuestaDto;
import com.cjp.model.Noticia;
import com.cjp.service.NoticiaService;
import com.cjp.service.Validaciones;

@RestController
@RequestMapping("/api/noticias")
public class NoticiasController {
	@Autowired
	private NoticiaService crud;
	
	private Validaciones validacion = new Validaciones();
	
	@CrossOrigin(origins = "https://cliente-cjp.herokuapp.com")
	@PreAuthorize("permitAll()")
	@GetMapping(consumes = "application/json", produces = "application/json", headers = "content-type=application/json")
	public List<Noticia> noticias(){
		return crud.listadoNoticias();
	}
	
	@CrossOrigin(origins = "https://cliente-cjp.herokuapp.com")
	@PreAuthorize("permitAll()")
	@GetMapping(value = "/ultimas", consumes = "application/json", produces = "application/json", headers = "content-type=application/json")
	public List<Noticia> ultimas(){
		return crud.ultimasNoticias();
	}
	
	@CrossOrigin(origins = "https://cliente-cjp.herokuapp.com")
	@PreAuthorize("permitAll()")
	@GetMapping(value = "/{id}", consumes = "application/json", produces = "application/json", headers = "content-type=application/json")
	public ResponseEntity<Object> verNoticia(@PathVariable(name = "id", required = true) Integer idNoticia){
		System.out.println(idNoticia);
		if (idNoticia != null) {
			Noticia noticia = crud.buscarNoticia(idNoticia);
			if (noticia != null) {
				return new ResponseEntity<>(noticia, HttpStatus.OK);
			}
			else {
				return new ResponseEntity<>("Noticia no encontrada", HttpStatus.NOT_FOUND);
			}
		}
		else {
			return new ResponseEntity<>("{mensaje: Error en la petición}", HttpStatus.BAD_REQUEST);
		}
	}
	
	@PreAuthorize("hasRole('ROLE_ADMIN') || hasRole('ROLE_COMUN')")
	@PostMapping(consumes = "application/json", produces = "application/json", headers = "content-type=application/json")
	public RespuestaDto agregar(@RequestBody(required = true) Noticia noticia){
		try {
			return guardar(noticia);
		}
		catch(Exception e) {
			RespuestaDto dto = new RespuestaDto();
			dto.setOk(false);
			dto.setMensaje("Error al realizar la operación");
			return dto;
		}
	}
	
	@PreAuthorize("hasRole('ROLE_ADMIN') || hasRole('ROLE_COMUN')")
	@PutMapping(consumes = "application/json", produces = "application/json", headers = "content-type=application/json")
	public RespuestaDto actualizar(@RequestBody(required = true) Noticia noticia){
		return guardar(noticia);
	}
	
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@DeleteMapping(consumes = "application/json", produces = "application/json", headers = "content-type=application/json")
	public RespuestaDto eliminar(@RequestBody(required = true) Noticia noticia) {
		RespuestaDto dto = new RespuestaDto();
		if (noticia.getIdNoticia() != null || noticia.getIdNoticia() > 0) {
			String resultado = crud.eliminarNotricia(noticia);
			if (resultado != null) {
				dto.setOk(true);
				dto.setMensaje("Se ha borrado la noticia");
			}
			else {
				dto.setOk(false);
				dto.setMensaje("No se pudo eliminar la noticia");
			}
		}
		else {
			dto.setOk(false);
			dto.setMensaje("No se pudo realizar esta operación");
		}
		return dto;
	}
	
	//Método para guardar los datos de la noticia
	private RespuestaDto guardar(Noticia noticia) {
		RespuestaDto dto = new RespuestaDto();
		
		if (validacion.validarTexto(noticia.getTitulo()) && validacion.validarTexto(noticia.getContenido()) &&
				validacion.validarTexto(noticia.getImagen())) {
			String respuesta = crud.guardarNoticia(noticia);
			if (respuesta != null) {
				dto.setOk(true);
				dto.setMensaje(respuesta);
			}
			else {
				dto.setOk(false);
				dto.setMensaje("No se pudo guardar los datos");
			}
		}
		else {
			dto.setOk(false);
			dto.setMensaje("Faltan campos por completar");
		}
		
		return dto;
	}
}