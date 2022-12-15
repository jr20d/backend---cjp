package com.cjp.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.cjp.model.Usuario;

@Repository
public interface UsuarioDao extends JpaRepository<Usuario, Integer> {
	@Query("SELECT u FROM Usuario u ORDER BY u.idUsuario ASC")
	List<Usuario> listadoUsuarios();
	
	@Query("SELECT u FROM Usuario u WHERE u.usuario = :usuario")
	Usuario buscarUsuarioPorNombre(@Param("usuario") String usuario);
}