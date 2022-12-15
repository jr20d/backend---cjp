package com.cjp.dao;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.cjp.model.Noticia;

@Repository
public interface NoticiaDao extends JpaRepository<Noticia, Integer> {
	@Query("SELECT n FROM Noticia n ORDER BY n.idNoticia DESC")
	List<Noticia> findAllDesc();
	
	@Query("SELECT n FROM Noticia n ORDER BY n.idNoticia DESC")
	List<Noticia> findTop3Desc(Pageable pageable);
}