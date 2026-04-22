/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package LukSportPrueba.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;

import LukSportPrueba.service.ProductoService;

@Controller
public class IndexController {
    
    @Autowired
    private ProductoService productoService;

    @GetMapping("/")
    public String inicio(Model model) {

        model.addAttribute("destacados",
                productoService.obtenerDestacados());

        return "index";
    }

}
