package LukSportPrueba.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/productos")
public class ProductosPublicosController {

    @GetMapping("")
    public String vistaProductos() {
        return "productos/productos";
    }
}