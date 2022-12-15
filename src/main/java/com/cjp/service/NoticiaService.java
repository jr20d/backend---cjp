package com.cjp.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cjp.dao.NoticiaDao;
import com.cjp.model.Noticia;

@Service
public class NoticiaService {
	@Autowired
	private NoticiaDao noticiaDao;
	
	@Transactional(readOnly = true)
	public List<Noticia> listadoNoticias() {
		return noticiaDao.findAllDesc();
	}

	@Transactional(readOnly = true)
	public List<Noticia> ultimasNoticias() {
		return noticiaDao.findTop3Desc(PageRequest.of(0, 3));
	}

	@Transactional(readOnly = true)
	public Noticia buscarNoticia(int idNoticia) {
		return noticiaDao.findById(idNoticia).orElse(null);
	}

	@Transactional
	public String guardarNoticia(Noticia noticia) {
		String resultado;
		noticia.setFecha((noticia.getFecha() != null ? noticia.getFecha() : new Date()));
		try {
			noticia.setTitulo(noticia.getTitulo().trim());
			noticia.setImagen(noticia.getImagen().trim());
			noticia.setContenido(noticia.getContenido().trim());
			noticiaDao.save(noticia);
			resultado = "Los datos de la noticia han sido guardados";
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			resultado = null;
		}
		return resultado;
	}

	@Transactional
	public String eliminarNotricia(Noticia noticia) {
		String resultado;
		
		try {
			noticiaDao.delete(noticia);
			resultado = "La noticia ha sido borrada";
		}
		catch(Exception ex) {
			ex.printStackTrace();
			resultado = null;
		}
		
		return resultado;
	}
}