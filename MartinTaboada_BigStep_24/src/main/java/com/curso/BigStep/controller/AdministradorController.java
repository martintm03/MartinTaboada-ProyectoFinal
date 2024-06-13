package com.curso.BigStep.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.curso.BigStep.model.Orden;
import com.curso.BigStep.model.Producto;
import com.curso.BigStep.service.IOrdenService;
import com.curso.BigStep.service.IUsuarioService;
import com.curso.BigStep.service.ProductoService;

@Controller
@RequestMapping("/administrador")
public class AdministradorController {

	@Autowired
	private ProductoService productoService;

	@Autowired
	private IUsuarioService usuarioService;

	@Autowired
	private IOrdenService ordensService;

	private Logger logg = LoggerFactory.getLogger(AdministradorController.class);

	@GetMapping("") 
	public String home(Model model) {
		List<Producto> productos = productoService.findAll(); // Obtiene todos los productos
		model.addAttribute("productos", productos); // Añade la lista de productos al model
		return "administrador/home"; // Devuelve la vista "home" para el administrador
	}

	@GetMapping("/usuarios") 
	public String usuarios(Model model) {
		model.addAttribute("usuarios", usuarioService.findAll()); // Añade la lista de usuarios al model
		return "administrador/usuarios"; // Devuelve la vista "usuarios" para el administrador
	}

	@GetMapping("/ordenes") 
	public String ordenes(Model model) {
		model.addAttribute("ordenes", ordensService.findAll()); // Añade la lista de órdenes al model
		return "administrador/ordenes"; // Devuelve la vista "ordenes" para el admin
	}

	@GetMapping("/detalle/{id}") 
	public String detalle(Model model, @PathVariable Integer id) {
		logg.info("Id de la orden {}", id); // Registra el ID de la orden solicitada
		Orden orden = ordensService.findById(id).orElse(null); // Busca la orden por ID
		if (orden != null) {
			model.addAttribute("detalles", orden.getDetalle()); // Si la orden existe, añade los detalles al modelo
		}
		return "administrador/detalleorden"; // Devuelve la vista "detalleorden" para el admin
	}
}
