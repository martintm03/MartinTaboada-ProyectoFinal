package com.curso.BigStep.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.curso.BigStep.model.Producto;

@Repository
public interface IProductoRepository extends JpaRepository<Producto, Integer> {

}
