package com.curso.BigStep.service;

import java.util.List;
import java.util.Optional;

import com.curso.BigStep.model.Orden;
import com.curso.BigStep.model.Usuario;

public interface IOrdenService {
	List<Orden> findAll();
	Optional<Orden> findById(Integer id);
	Orden save (Orden orden);
	String generarNumeroOrden();
	List<Orden> findByUsuario (Usuario usuario);
}
