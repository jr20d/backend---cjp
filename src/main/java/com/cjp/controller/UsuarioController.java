package com.cjp.controller;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cjp.config.JWTCreacion;

@Controller
@RequestMapping("/usuario")
public class UsuarioController {
	@Autowired
	private JWTCreacion jwt;
	private final String VISTA = "usuario";
	private final String TITULO = "USUARIOS";
	
	@PreAuthorize("permitAll()")
	@GetMapping
	public String index(@CookieValue(name = "rol", required = false) String rol, @CookieValue(name = "auth", required = false) String token, Model model, HttpServletResponse response) {
		try {
			if (!jwt.tokenExpiro(token) && jwt.validarToken(token)) {
				if (rol.equals("ROLE_ADMIN")) {
					model.addAttribute("view", VISTA.concat("/index"));
					model.addAttribute("activo", 2);
					model.addAttribute("titulo", TITULO);
					return "layout";
				}
				else if (rol.equals("ROLE_COMUN")) {
					response.setStatus(HttpStatus.FORBIDDEN.value());
					model.addAttribute("error", "ERROR 403");
					model.addAttribute("contenido", "No posees los privilegios necesarios para acceder");
					return "error/error";
				}
			}
		}
		catch(Exception ex) {
			return "redirect:/login";
		}
		
		return "redirect:/login";
	}
}