/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package LukSportPrueba.controller;

import LukSportPrueba.domain.Usuario;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private boolean esAdmin(Usuario usuario) {
        return usuario != null
                && usuario.getRol() != null
                && usuario.getRol().getIdRol() == 2;
    }

    @GetMapping
    public String dashboardAdmin(HttpSession session, Model model) {
        Usuario usuarioSesion = (Usuario) session.getAttribute("usuarioSesion");

        if (!esAdmin(usuarioSesion)) {
            return "redirect:/";
        }

        model.addAttribute("seccion", "inicio");
        return "admin/dashboard";
    }


    @GetMapping("/productos")
    public String adminProductos(HttpSession session, Model model) {
        Usuario usuarioSesion = (Usuario) session.getAttribute("usuarioSesion");

        if (!esAdmin(usuarioSesion)) {
            return "redirect:/";
        }

        model.addAttribute("seccion", "productos");
        return "admin/dashboard";
    }

    @GetMapping("/transacciones")
    public String adminTransacciones(HttpSession session, Model model) {
        Usuario usuarioSesion = (Usuario) session.getAttribute("usuarioSesion");

        if (!esAdmin(usuarioSesion)) {
            return "redirect:/";
        }

        model.addAttribute("seccion", "transacciones");
        return "admin/dashboard";
    }
}