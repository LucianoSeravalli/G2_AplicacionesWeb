package LukSportPrueba.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ErrorControllerCustom {

    @GetMapping("/sin-acceso")
    public String accesoDenegado() {
        return "error/acceso";
    }
}
