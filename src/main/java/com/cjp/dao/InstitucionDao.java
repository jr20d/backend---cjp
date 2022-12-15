package com.cjp.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.cjp.model.Institucion;

@Repository
public interface InstitucionDao extends JpaRepository<Institucion, Integer> {
	@Query("SELECT i FROM Institucion i ORDER BY i.idInstitucion ASC")
	List<Institucion> listarInstituciones();
}