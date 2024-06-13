package com.curso.BigStep.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ContactoController {

    @GetMapping("/contacto")
    public String mostrarFormularioContacto() {
        return "contacto";
    }

    @PostMapping("/contacto/enviar")
    public String enviarFormularioContacto(@RequestParam("nombre") String nombre,
                                           @RequestParam("email") String email,
                                           @RequestParam("mensaje") String mensaje,
                                           Model model) {
       
        model.addAttribute("mensajeExito", "Tu mensaje ha sido enviado con éxito.");
        return "contacto";
    }
}
