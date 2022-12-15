package com.cjp.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cjp.model.Telefono;

@Repository
public interface TelefonoDao extends JpaRepository<Telefono, Integer> {}