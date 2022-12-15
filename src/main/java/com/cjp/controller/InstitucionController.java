package com.cjp.controller;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cjp.config.JWTCreacion;

@Controller
@RequestMapping("/institucion")
public class InstitucionController {
	@Autowired
	private JWTCreacion crearJwt;
	
	private final String VISTA = "institucion";
	private final String TITULO = "INSTITUCIONES";
	
	@GetMapping
	public String index(@CookieValue(required = false, name = "auth") String token, Model model, HttpServletResponse response) {
		try {
			model.addAttribute("view", VISTA.concat("/index"));
			model.addAttribute("activo", 4);
			model.addAttribute("titulo", TITULO);
			if (token != null && !token.equals("")) {
				if (crearJwt.validarToken(token) && !crearJwt.tokenExpiro(token)) {
					return "layout";
				}
			}
		}
		catch(Exception ex) {
			return "redirect:/login";
		}
		
		return "redirect:/login";
	}
}