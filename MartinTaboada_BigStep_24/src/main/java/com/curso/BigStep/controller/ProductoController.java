package com.curso.BigStep.controller;

import java.io.IOException;
import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.slf4j.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.curso.BigStep.model.Producto;
import com.curso.BigStep.model.Usuario;
import com.curso.BigStep.service.IUsuarioService;
import com.curso.BigStep.service.ProductoService;
import com.curso.BigStep.service.UploadFileService;
import com.curso.BigStep.service.UsuarioServiceImpl;

@Controller
@RequestMapping("/productos")
public class ProductoController {
	
	private final Logger LOGGER = LoggerFactory.getLogger(ProductoController.class);
	
	@Autowired
	private ProductoService productoService;
	
	@Autowired
	private IUsuarioService usuarioService;
	
	@Autowired
	private UploadFileService upload;
	
	@GetMapping("")
	public String show(Model model) {
		// Añade todos los productos al modelo para mostrarlos en la vista
		model.addAttribute("productos", productoService.findAll());
		return "productos/show";
	}
	
	@GetMapping("/create")
	public String create() {
		// Muestra el formulario para crear un nuevo producto
		return "productos/create";
	}
	
	@PostMapping("/save")
	public String save(Producto producto, @RequestParam("img") MultipartFile file, HttpSession session) throws IOException {
		// Loguea el objeto producto recibido
		LOGGER.info("Este es el objeto producto {}", producto);
		
		// Obtiene el usuario de la sesión y lo asigna al producto
		Usuario u = usuarioService.findById(Integer.parseInt(session.getAttribute("idusuario").toString())).get();
		producto.setUsuario(u);	
		
		// Manejo de la imagen del producto
		if (producto.getId() == null) { // cuando se crea un producto
			String nombreImagen = upload.saveImage(file);
			producto.setImagen(nombreImagen);
		}
		
		// Guarda el producto en la base de datos
		productoService.save(producto);
		return "redirect:/productos";
	}
	
	@GetMapping("/edit/{id}")
	public String edit(@PathVariable Integer id, Model model) {
		// Busca el producto por ID para editar
		Optional<Producto> optionalProducto = productoService.get(id);
		Producto producto = optionalProducto.get();
		
		// Loguea el producto buscado
		LOGGER.info("Producto buscado: {}", producto);
		
		// Añade el producto al modelo
		model.addAttribute("producto", producto);
		
		return "productos/edit";
	}
	
	@PostMapping("/update")
	public String update(Producto producto, @RequestParam("img") MultipartFile file) throws IOException {
		// Busca el producto existente en la base de datos
		Producto p = productoService.get(producto.getId()).get();
		
		if (file.isEmpty()) { // Editamos el producto pero no cambiamos la imagen
			producto.setImagen(p.getImagen());
		} else { // Cuando se edita también la imagen
			// Eliminar la imagen existente si no es la imagen por defecto
			if (!p.getImagen().equals("default.jpg")) {
				upload.deleteImage(p.getImagen());
			}
			// Guarda la nueva imagen
			String nombreImagen = upload.saveImage(file);
			producto.setImagen(nombreImagen);
		}
		
		// Mantiene el usuario del producto original
		producto.setUsuario(p.getUsuario());
		// Actualiza el producto en la base de datos
		productoService.update(producto);		
		return "redirect:/productos";
	}
	
	@GetMapping("/delete/{id}")
	public String delete(@PathVariable Integer id) {
		// Busca el producto por ID para eliminar
		Producto p = productoService.get(id).get();
		
		// Elimina la imagen existente si no es la imagen por defecto
		if (!p.getImagen().equals("default.jpg")) {
			upload.deleteImage(p.getImagen());
		}
		
		// Elimina el producto de la base de datos
		productoService.delete(id);
		return "redirect:/productos";
	}
}
