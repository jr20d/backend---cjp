package com.cjp.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cjp.dao.InstitucionDao;
import com.cjp.model.Institucion;

@Service
public class InstitucionService {
	@Autowired
	private InstitucionDao dao;
	
	@Transactional(readOnly = true)
	public List<Institucion> listadoInstituciones() {
		try {
			return dao.listarInstituciones();
		}
		catch(Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	@Transactional
	public String guardar(Institucion institucion) {
		String resultado;
		
		try {
			dao.save(institucion);
			resultado = "Los datos de la institución se han guardado";
		}
		catch(Exception ex) {
			ex.printStackTrace();
			resultado = null;
		}
		
		return resultado;
	}

	@Transactional
	public String eliminar(Institucion institucion) {
		String respuesta;
		
		try {
			dao.delete(institucion);
			respuesta = "Los datos han sido eliminados";
		}
		catch(Exception ex) {
			ex.printStackTrace();
			respuesta = null;
		}
		
		return respuesta;
	}
	
}
