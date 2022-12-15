package com.cjp.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cjp.dao.TelefonoDao;
import com.cjp.model.Telefono;

@Service
public class TelefonoService {
	@Autowired
	private TelefonoDao comunDao;

	@Transactional(readOnly = true)
	public List<Telefono> listado() {
		return comunDao.findAll();
	}

	@Transactional
	public String guardar(Telefono registro) {
		String resultado;
		
		try {
			registro.setTelefono("".equals(registro.getTelefono().trim()) ? null : registro.getTelefono().trim());
			comunDao.save(registro);
			resultado = "Se han guardado los cambios";
		}
		catch(Exception ex) {
			ex.printStackTrace();
			resultado = null;
		}
		return resultado;
	}
}