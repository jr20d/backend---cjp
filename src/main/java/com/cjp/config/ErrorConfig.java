package com.cjp.config;

import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;

import com.cjp.dto.ErrorDto;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class ErrorConfig implements ErrorController {
	@Autowired
	ErrorAttributes errorAttributes;
	
	@RequestMapping("/error")
	public String HandleError(@RequestHeader("Accept") String encabezado, HttpServletRequest request, Model model) {
		Object estado = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
		
		if (encabezado.contains(MediaType.APPLICATION_JSON.toString()) || encabezado.equals("*/*")) {
			return "forward:/errorJSON";
		}
		
		if (estado != null) {
			Integer codigo = Integer.valueOf(estado.toString());
			
			if (codigo == HttpStatus.NOT_FOUND.value()) {
				model.addAttribute("error", "ERROR 404");
				model.addAttribute("contenido", "No se pudo encontrar la página");
				return "error/error";
			}
			else if (codigo == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
				model.addAttribute("error", "ERROR 500");
				model.addAttribute("contenido", "Ha ocurrido algo inesperado, contacta con el Administrador del sitio");
				return "error/error";
			}
			else if(codigo == HttpStatus.UNAUTHORIZED.value()) {
				return "redirect:/login";
			}
			else {
				model.addAttribute("error", "ERROR " + codigo);
				model.addAttribute("contenido", "Contacta con el Administrador del sitio");
				return "error/error";
			}
		}
		else {
			model.addAttribute("error", "ERROR 404");
			model.addAttribute("contenido", "No se pudo encontrar la página");
			return "error/error";
		}
	}
	
	@RequestMapping(value = "/errorJSON", produces = {MediaType.APPLICATION_JSON_VALUE})
	@ResponseBody
    public ErrorDto handleErrorJson(HttpServletRequest request, WebRequest webRequest) {
        Map<String, Object> mapErrors = errorAttributes.getErrorAttributes(webRequest, false);
        log.info(mapErrors.toString());
        return new ErrorDto((int) request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE),
        		(String) mapErrors.get("error"), "Error al realizar la operación");
    }

	@Override
	public String getErrorPath() {
		return null;
	}
	
}
