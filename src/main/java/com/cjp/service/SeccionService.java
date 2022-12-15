package com.cjp.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cjp.dao.SeccionDao;
import com.cjp.model.Seccion;

@Service
public class SeccionService {
	@Autowired
	private SeccionDao dao;

	@Transactional
	public String guardarSeccion(Seccion seccion) {
		String respuesta;
		try {
			dao.save(seccion);
			respuesta = "Los datos han sido actualizados";
		}
		catch(Exception ex) {
			ex.printStackTrace();
			respuesta = null;
		}
		return respuesta;
	}

	@Transactional(readOnly = true)
	public List<Seccion> listadoSecciones() {
		try {
			return dao.listadoSecciones();
		}
		catch(Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	@Transactional(readOnly = true)
	public List<Seccion> seccionesPorCategoria(Integer id) {
		try {
			return dao.listadoPorCategoria(id);
		}
		catch(Exception ex){
			return null;
		}
	}
}