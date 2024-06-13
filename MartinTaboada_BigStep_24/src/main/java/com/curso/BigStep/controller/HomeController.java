package com.curso.BigStep.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.curso.BigStep.model.DetalleOrden;
import com.curso.BigStep.model.Orden;
import com.curso.BigStep.model.Producto;
import com.curso.BigStep.model.Usuario;
import com.curso.BigStep.service.IDetalleOrdenService;
import com.curso.BigStep.service.IOrdenService;
import com.curso.BigStep.service.IUsuarioService;
import com.curso.BigStep.service.ProductoService;

@Controller
@RequestMapping("/")
public class HomeController {

	private final Logger log = LoggerFactory.getLogger(HomeController.class);

	@Autowired
	private ProductoService productoService;

	@Autowired
	private IUsuarioService usuarioService;

	@Autowired
	private IOrdenService ordenService;

	@Autowired
	private IDetalleOrdenService detalleOrdenService;

	// Para almacenar los detalles de la orden
	List<DetalleOrden> detalles = new ArrayList<DetalleOrden>();

	// Datos de la orden
	Orden orden = new Orden();

	@GetMapping("")
	public String home(Model model, HttpSession session) {
		// Loguea la sesión del usuario
		log.info("Sesion del usuario: {}", session.getAttribute("idusuario"));
		
		// Añade todos los productos al modelo
		model.addAttribute("productos", productoService.findAll());
		
		// Añade la sesión al modelo
		model.addAttribute("sesion", session.getAttribute("idusuario"));

		return "usuario/home";
	}

	@GetMapping("productohome/{id}")
	public String productoHome(@PathVariable Integer id, Model model) {
		// Loguea el ID del producto enviado como parámetro
		log.info("Id producto enviado como parámetro {}", id);
		
		// Busca el producto por ID
		Optional<Producto> productoOptional = productoService.get(id);
		Producto producto = productoOptional.get();

		// Añade el producto al modelo
		model.addAttribute("producto", producto);

		return "usuario/productohome";
	}

	@PostMapping("/cart")
	public String addCart(@RequestParam Integer id, @RequestParam Integer cantidad, Model model) {
		DetalleOrden detalleOrden = new DetalleOrden();
		double sumaTotal = 0;

		// Busca el producto por ID
		Optional<Producto> optionalProducto = productoService.get(id);
		log.info("Producto añadido: {}", optionalProducto.get());
		log.info("Cantidad: {}", cantidad);
		Producto producto = optionalProducto.get();

		// Configura los detalles de la orden
		detalleOrden.setCantidad(cantidad);
		detalleOrden.setPrecio(producto.getPrecio());
		detalleOrden.setNombre(producto.getNombre());
		detalleOrden.setTotal(producto.getPrecio() * cantidad);
		detalleOrden.setProducto(producto);

		// Valida que el producto no se añada dos veces
		Integer idProducto = producto.getId();
		boolean ingresado = detalles.stream().anyMatch(p -> p.getProducto().getId() == idProducto);

		if (!ingresado) {
			detalles.add(detalleOrden);
		}

		// Calcula el total de la orden
		sumaTotal = detalles.stream().mapToDouble(dt -> dt.getTotal()).sum();

		// Configura la orden y añade atributos al modelo
		orden.setTotal(sumaTotal);
		model.addAttribute("cart", detalles);
		model.addAttribute("orden", orden);

		return "usuario/carrito";
	}

	// Quitar un producto del carrito
	@GetMapping("/delete/cart/{id}")
	public String deleteProductoCart(@PathVariable Integer id, Model model) {
		// Crea una nueva lista de productos
		List<DetalleOrden> ordenesNueva = new ArrayList<DetalleOrden>();

		// Añade los productos restantes a la nueva lista
		for (DetalleOrden detalleOrden : detalles) {
			if (detalleOrden.getProducto().getId() != id) {
				ordenesNueva.add(detalleOrden);
			}
		}

		// Actualiza la lista de detalles de la orden
		detalles = ordenesNueva;

		// Calcula el nuevo total de la orden
		double sumaTotal = 0;
		sumaTotal = detalles.stream().mapToDouble(dt -> dt.getTotal()).sum();

		// Configura la orden y añade atributos al modelo
		orden.setTotal(sumaTotal);
		model.addAttribute("cart", detalles);
		model.addAttribute("orden", orden);

		return "usuario/carrito";
	}
	
	@GetMapping("/getCart")
	public String getCart(Model model, HttpSession session) {
		// Añade la lista de detalles de la orden y la orden al modelo
		model.addAttribute("cart", detalles);
		model.addAttribute("orden", orden);
		
		// Añade la sesión al modelo
		model.addAttribute("sesion", session.getAttribute("idusuario"));
		return "/usuario/carrito";
	}
	
	@GetMapping("/order")
	public String order(Model model, HttpSession session) {
		// Verifica si el usuario está registrado
		if (session.getAttribute("idusuario") == null) {
			// Redirige al login si no está registrado
			return "redirect:/usuario/login";
		}
		
		// Obtiene el usuario desde el servicio
		Usuario usuario = usuarioService.findById(Integer.parseInt(session.getAttribute("idusuario").toString())).get();
		
		// Añade atributos al modelo
		model.addAttribute("cart", detalles);
		model.addAttribute("orden", orden);
		model.addAttribute("usuario", usuario);
		
		return "usuario/resumenorden";
	}
	
	// Guardar la orden
	@GetMapping("/saveOrder")
	public String saveOrder(HttpSession session ) {
		// Configura la fecha de creación y el número de orden
		Date fechaCreacion = new Date();
		orden.setFechaCreacion(fechaCreacion);
		orden.setNumero(ordenService.generarNumeroOrden());
		
		// Obtiene el usuario desde el servicio
		Usuario usuario = usuarioService.findById(Integer.parseInt(session.getAttribute("idusuario").toString())).get();
		
		// Configura la orden con el usuario
		orden.setUsuario(usuario);
		ordenService.save(orden);
		
		// Guarda los detalles de la orden
		for (DetalleOrden dt : detalles) {
			dt.setOrden(orden);
			detalleOrdenService.save(dt);
		}
		
		// Limpia la lista y la orden
		orden = new Orden();
		detalles.clear();
		
		return "redirect:/";
	}
	
	@PostMapping("/search")
	public String searchProduct(@RequestParam String nombre, Model model) {
		// Loguea el nombre del producto buscado
		log.info("Nombre del producto: {}", nombre);
		
		// Filtra los productos que coincidan con la busqueda
		List<Producto> productos = productoService.findAll()
												  .stream()
												  .filter(p -> p.getNombre().contains(nombre))
												  .collect(Collectors.toList());
		
		// Añade los productos al modelo
		model.addAttribute("productos", productos);
		//	En caso de que no haya coincidencias envia "noMatches" para implementación con JS 
		model.addAttribute("noMatches", productos.isEmpty());
		
		return "usuario/home";
	}
}
