package LukSportPrueba.controller;

import LukSportPrueba.service.CategoriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/categorias")
public class CategoriaPublicController {

    @Autowired
    private CategoriaService categoriaService;

    @GetMapping("")
    public String listadoCategorias(Model model) {

        model.addAttribute("categorias", categoriaService.listarCategoriasActivas());

        return "categoria/listado";
    }
}