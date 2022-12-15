package com.cjp.config;

import java.io.Serializable;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Component
public class JWTCreacion implements Serializable {
	private static final long serialVersionUID = 1L;
	
	//Tiempo de duración del JWT en segundos
	//1 hora de duración del token
	private final int JWT_DURACION = 1 * 60 * 60;
	
	//Clave secreta para firmar el JWT
	@Value("${jwt.secret}")
	private String clave;
	
	//Crear token
	public String generarToken(Authentication authentication) {
		return Jwts.builder().setSubject(authentication.getName())
				.setIssuedAt(new Date())
				.setExpiration(new Date(new Date().getTime() + JWT_DURACION * 1000))
				.signWith(SignatureAlgorithm.HS512, clave)
				.compact();
	}
	
	//Obtener el usuario desde el token recibido
	public String getUsuarioToken(String token) {
		try {
			return Jwts.parser().setSigningKey(clave).parseClaimsJws(token).getBody().getSubject();
		}
		catch(Exception e) {
			log.info("No se pudo generar el token");
		}
		return null;
	}
	
	//Validar el token
	public boolean validarToken(String token) {
		try {
			Jwts.parser().setSigningKey(clave).parseClaimsJws(token);
			return true;
		}
		catch(MalformedJwtException e) {
			log.info("Error en el formato del token");
		}
		catch(UnsupportedJwtException e) {
			log.info("Error este tipo de token no esta soportado");
		}
		catch(ExpiredJwtException e) {
			log.info("Error el token ha expirado");
		}
		catch(IllegalArgumentException e) {
			log.info("Error el token posee argumentos no válidos");
		}
		catch(SignatureException e) {
			log.info("Error en la firma del token");
		}
		
		return true;
	}
	
	//Verificar si el token ha expirado
	public boolean tokenExpiro(String token) {
		final Date expira = Jwts.parser().setSigningKey(clave).parseClaimsJws(token).getBody().getExpiration();
		return expira.before(new Date());
	}
	
	//Refrescar token
	public String refrescarToken(String usuario) {
		return Jwts.builder().setSubject(usuario)
				.setIssuedAt(new Date())
				.setExpiration(new Date(new Date().getTime() + JWT_DURACION * 1000))
				.signWith(SignatureAlgorithm.HS512, clave)
				.compact();
	}
}