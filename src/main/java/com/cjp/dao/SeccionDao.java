package com.cjp.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.cjp.model.Seccion;

@Repository
public interface SeccionDao extends JpaRepository<Seccion, Integer> {
	@Query("SELECT s FROM Seccion s ORDER BY s.idSeccion ASC")
	List<Seccion> listadoSecciones();
	
	@Query("SELECT s FROM Seccion s WHERE s.idCategoria = :id ORDER BY s.idSeccion ASC")
	List<Seccion> listadoPorCategoria(Integer id);
}