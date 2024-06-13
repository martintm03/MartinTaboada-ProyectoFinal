package com.curso.BigStep.controller;

import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.curso.BigStep.model.Orden;
import com.curso.BigStep.model.Usuario;
import com.curso.BigStep.service.IOrdenService;
import com.curso.BigStep.service.IUsuarioService;

@Controller
@RequestMapping("/usuario")
public class UsuarioController {

	private final Logger logger = LoggerFactory.getLogger(UsuarioController.class);

	@Autowired
	private IUsuarioService usuarioService;

	@Autowired
	private IOrdenService ordenService;

	// Codificador de contraseñas
	BCryptPasswordEncoder passEncode = new BCryptPasswordEncoder();

	// /usuario/registro
	@GetMapping("/registro")
	public String create() {
		// Muestra la página de registro de usuario
		return "usuario/registro";
	}

	@PostMapping("/save")
	public String save(Usuario usuario) {
		// Loguea la información del usuario
		logger.info("Usuario registro: {}", usuario);

		// Configura el tipo de usuario y encripta la contraseña
		usuario.setTipo("USER");
		usuario.setPassword(passEncode.encode(usuario.getPassword()));

		// Guarda el usuario en la base de datos
		usuarioService.save(usuario);
		return "redirect:/";
	}

	@GetMapping("/login")
	public String login() {
		// Muestra la página de login de usuario
		return "usuario/login";
	}

	@PostMapping("/acceder")
	public String acceder(Usuario usuario, HttpSession session) {
		// Loguea la información del acceso
		logger.info("Accesos : {}", usuario);

		// Busca el usuario por email
		Optional<Usuario> user = usuarioService.findByEmail(usuario.getEmail());

		if (user.isPresent()) {
			// Si el usuario existe, añade el ID del usuario a la sesión
			session.setAttribute("idusuario", user.get().getId());

			// Redirige según el tipo de usuario (ADMIN o USER)
			if (user.get().getTipo().equals("ADMIN")) {
				return "/administrador/home";
			} else {
				return "redirect:/";
			}
		} else {
			// Si el usuario no existe, loguea la información
			logger.info("Usuario no existe");
		}
		return "redirect:/";
	}

	@GetMapping("/compras")
	public String obtenerCompras(Model model, HttpSession session) {
		// Añade la sesión al model
		model.addAttribute("sesion", session.getAttribute("idusuario"));

		// Obtiene el usuario por ID de la sesión
		Usuario usuario = usuarioService.findById(Integer.parseInt(session.getAttribute("idusuario").toString())).get();

		// Busca las órdenes del usuario
		List<Orden> ordenes = ordenService.findByUsuario(usuario);
		logger.info("ordenes {}", ordenes);

		// Añade las órdenes al model
		model.addAttribute("ordenes", ordenes);

		return "usuario/compras";
	}

	@GetMapping("/detalle/{id}")
	public String detalleCompra(@PathVariable Integer id, HttpSession session, Model model) {
		// Loguea el ID de la orden
		logger.info("Id de la orden: {}", id);

		// Busca la orden por ID
		Optional<Orden> orden = ordenService.findById(id);

		// Añade los detalles de la orden al modelo
		model.addAttribute("detalles", orden.get().getDetalle());

		// Añade la sesión al model
		model.addAttribute("sesion", session.getAttribute("idusuario"));
		return "usuario/detallecompra";
	}

	@GetMapping("/cerrar")
	public String cerrarSesion(HttpSession session) {
		// Elimina el atributo de la sesión para cerrar la sesión del usuario
		session.removeAttribute("idusuario");
		return "redirect:/";
	}
}
