package com.cjp.config;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.cjp.service.UserDetailsServiceImpl;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Component
public class JWTAuthTokenFilter extends OncePerRequestFilter {
	@Autowired
	private JWTCreacion jwtCreacion;
	
	@Autowired
	private UserDetailsServiceImpl userDetailsService;
	
	//Sobreescritura al metodo doFilterInternal para comprobar la validez del token
	//cada vez que se hagan peticiones
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		try {
			String token = getToken(request);
			
			if (token != null && jwtCreacion.validarToken(token)) {
				String usuario = jwtCreacion.getUsuarioToken(token);
				UserDetails userDetails = userDetailsService.loadUserByUsername(usuario);
				UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
				SecurityContextHolder.getContext().setAuthentication(auth);
			}
		}
		catch(Exception e) {
			log.info("No se ha proporcionado un token válido");
		}
		filterChain.doFilter(request, response);
	}
	
	//Método para obtener el token sin el prefijo de Bearer
	//del header de la petición
	private String getToken(HttpServletRequest request) {
		String header = request.getHeader("Authorization");
		if (header != null && header.startsWith("Bearer")) {
			return header.replace("Bearer ", "");
		}
		return null;
	}
}
