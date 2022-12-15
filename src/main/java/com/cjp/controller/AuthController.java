package com.cjp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cjp.config.JWTCreacion;
import com.cjp.dto.JWTUsuarioRequest;
import com.cjp.dto.JWTUsuarioResponse;
import com.cjp.dto.RespuestaDto;
import com.cjp.service.UsuarioService;

import lombok.extern.log4j.Log4j2;

@Log4j2
@RestController
@RequestMapping("/auth")
public class AuthController {
	
	@Autowired
	private AuthenticationManager manager;
	
	@Autowired
	private UsuarioService crud;
	
	private Authentication autenticar;
	
	@Autowired
	private JWTCreacion jwtCrear;
	
	@PreAuthorize("permitAll()")
	@PostMapping(consumes = "application/json", produces = "application/json", headers = "content-type=application/json")
	public ResponseEntity<Object> login(@RequestBody(required = true) JWTUsuarioRequest usuarioLogin, BindingResult bindingResult){
		if (bindingResult.hasErrors()) {
			return new ResponseEntity<>("Error en los datos enviados", HttpStatus.BAD_REQUEST);
		}
		autenticar = manager.authenticate(new UsernamePasswordAuthenticationToken(usuarioLogin.getUsuario(), usuarioLogin.getPassword()));
		SecurityContextHolder.getContext().setAuthentication(autenticar);
		String jwt = jwtCrear.generarToken(autenticar);
		UserDetails userDetails = (UserDetails) autenticar.getPrincipal();
		JWTUsuarioResponse response = new JWTUsuarioResponse();
		response.setToken(jwt);
		response.setUsuario(userDetails.getUsername());
		response.setAuthorities(userDetails.getAuthorities());
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	@PreAuthorize("hasRole('ROLE_ADMIN') || hasRole('ROLE_COMUN')")
	@PostMapping(value = "/extend", consumes = "application/json", produces = "application/json", headers = "content-type=application/json")
	public ResponseEntity<Object> extenderToken(@RequestHeader(name = "Authorization", required = true) String token){
		try {
			if (!token.equals(null) && token.startsWith("Bearer ") && jwtCrear.validarToken(token.replace("Bearer ", "")) && !jwtCrear.tokenExpiro(token.replace("Bearer ", ""))) {
				JWTUsuarioResponse response = new JWTUsuarioResponse();
				String usuario = jwtCrear.getUsuarioToken(token.replace("Bearer ", ""));
				String nuevoToken = jwtCrear.refrescarToken(usuario);
				response.setToken(nuevoToken);
				return new ResponseEntity<>(response, HttpStatus.OK);
			}
		}
		catch(Exception ex) {
			log.info("Error al procesar los datos");
			log.error(ex.getMessage());
		}
		
		return new ResponseEntity<>("Error al procesar los datos enviados", HttpStatus.BAD_REQUEST);
	}
	
	@PreAuthorize("hasRole('ROLE_ADMIN') || hasRole('ROLE_COMUN')")
	@PutMapping(value = "/credenciales", consumes = "application/json", produces = "application/json", headers = "content-type=application/json")
	public ResponseEntity<Object> actualizarPassword(@RequestHeader(name = "Authorization", required = true) String token, @RequestBody(required = true) JWTUsuarioRequest usuario, BindingResult result) {
		RespuestaDto dto = new RespuestaDto();
		if (result.hasErrors()) {
			return new ResponseEntity<>("Error en los datos enviados", HttpStatus.BAD_REQUEST);
		}
		try {
			if (token.startsWith("Bearer ") && jwtCrear.validarToken(token.replace("Bearer ", "")) && !jwtCrear.tokenExpiro(token.replace("Bearer ", ""))) {
				if (usuario.getPassword().trim().length() > 4)
				{
					String usuarioLogeado = jwtCrear.getUsuarioToken(token.replace("Bearer ", ""));
					String resultado = crud.cambiarPassword(usuarioLogeado, usuario.getPassword());
					dto.setOk(resultado != null ? true : false);
					dto.setMensaje(resultado != null ? resultado : "Error al cambiar la contraseña");
					return new ResponseEntity<>(dto, HttpStatus.OK);
				}
				else {
					dto.setOk(false);
					dto.setMensaje("La contraseña es muy corta");
					return new ResponseEntity<>(dto, HttpStatus.OK);
				}
			}
		}
		catch(Exception ex) {
			ex.printStackTrace();
			log.info(ex.getMessage());
		}
		return new ResponseEntity<>("Error al procesar los datos", HttpStatus.UNAUTHORIZED);
	}
}