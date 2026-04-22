/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package LukSportPrueba.controller;

import LukSportPrueba.domain.Producto;
import LukSportPrueba.service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    @GetMapping("/producto/listado")
    public String listadoProductosPorCategoria(@RequestParam("categoriaId") Integer categoriaId, Model model) {

        model.addAttribute("productos", productoService.getProductosPorCategoria(categoriaId));

        return "producto/listado";
    }

    @GetMapping("/producto/detalle/{idProducto}")
    public String detalleProducto(@PathVariable("idProducto") Integer idProducto, Model model) {

        Producto producto = productoService.obtenerProductoPorId(idProducto);

        model.addAttribute("producto", producto);

        if (producto != null && Boolean.TRUE.equals(producto.getTieneTalla())) {
            model.addAttribute("tallasDisponibles", productoService.obtenerTallasDisponibles(idProducto));
        } else {
            model.addAttribute("tallaSinTallas", productoService.obtenerTallaSinTallas());
        }

        return "producto/detalle";
    }

}
