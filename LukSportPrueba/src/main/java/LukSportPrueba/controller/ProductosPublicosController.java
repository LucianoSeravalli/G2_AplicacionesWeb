package LukSportPrueba.controller;

import LukSportPrueba.service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/productos")
public class ProductosPublicosController {

    @Autowired
    private ProductoService productoService;
    
    @GetMapping("")
    public String vistaProductos(Model model) {
        model.addAttribute("topProducto", productoService.obtenerTop1Destacado());
        return "productos/productos";
    }

}
