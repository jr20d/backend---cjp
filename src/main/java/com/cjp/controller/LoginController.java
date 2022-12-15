package com.cjp.controller;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;

import com.cjp.config.JWTCreacion;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Controller
public class LoginController {
	@Autowired
	private JWTCreacion crearJwt;
	
	@PreAuthorize("permitAll()")
	@GetMapping("/login")
	public String index(@CookieValue(required = false, name = "auth") String token, HttpServletResponse response) {
		try {
			if (token != null && !token.trim().equals("")) {
				if (crearJwt.validarToken(token) && !crearJwt.tokenExpiro(token)) {
					return "redirect:/";
				}
			}
		}
		catch(Exception ex) {
			log.info("Error al iniciar sesión");
		}
		response.setStatus(HttpStatus.UNAUTHORIZED.value());
		return "login/index";
	}
	
	@PreAuthorize("permitAll()")
	@GetMapping("/login/destroy")
	public String cerrar(@CookieValue(required = false, name = "auth") String token, HttpServletResponse response) {
		try {
			if (crearJwt.validarToken(token)) {
				Cookie cookie = new Cookie("auth", null);
				cookie.setSecure(true);
				cookie.setMaxAge(0);
				response.addCookie(cookie);
			}
		}
		catch(Exception ex) {
			log.info("Ocurrío un error al cerrar sesión " + ex.getCause());
		}
		return "redirect:/login";
	}
}