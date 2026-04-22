package LukSportPrueba.controller;

import LukSportPrueba.domain.Pedido;
import LukSportPrueba.domain.Usuario;
import LukSportPrueba.service.CarritoService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class CarritoController {

    @Autowired
    private CarritoService carritoService;

    private boolean puedeUsarCarrito(Usuario usuario) {
        return usuario != null
                && usuario.getRol() != null
                && (usuario.getRol().getIdRol() == 1
                || usuario.getRol().getIdRol() == 2
                || usuario.getRol().getIdRol() == 3);
    }

    @GetMapping("/carrito")
    public String verCarrito(HttpSession session, Model model) {
        Usuario usuarioSesion = (Usuario) session.getAttribute("usuarioSesion");

        if (!puedeUsarCarrito(usuarioSesion)) {
            return "redirect:/login";
        }

        Pedido carrito = carritoService.obtenerCarritoActivo(usuarioSesion.getIdUsuario());

        model.addAttribute("carrito", carrito);
        model.addAttribute("itemsCarrito", carritoService.obtenerItemsCarrito(usuarioSesion.getIdUsuario()));

        return "carrito/carrito";
    }

    @PostMapping("/carrito/cancelar")
    public String cancelarPedido(HttpSession session, RedirectAttributes redirectAttributes) {
        Usuario usuarioSesion = (Usuario) session.getAttribute("usuarioSesion");

        if (!puedeUsarCarrito(usuarioSesion)) {
            return "redirect:/login";
        }

        String resultado = carritoService.cancelarPedido(usuarioSesion.getIdUsuario());

        if (!"ok".equals(resultado)) {
            redirectAttributes.addFlashAttribute("error", resultado);
        } else {
            redirectAttributes.addFlashAttribute("mensaje", "El pedido fue cancelado correctamente.");
        }

        return "redirect:/carrito";
    }

    @PostMapping("/carrito/pagar")
    public String pagarPedido(HttpSession session, RedirectAttributes redirectAttributes) {
        Usuario usuarioSesion = (Usuario) session.getAttribute("usuarioSesion");

        if (!puedeUsarCarrito(usuarioSesion)) {
            return "redirect:/login";
        }

        String resultado = carritoService.pagarPedido(usuarioSesion.getIdUsuario());

        if (!"ok".equals(resultado)) {
            redirectAttributes.addFlashAttribute("error", resultado);
        } else {
            redirectAttributes.addFlashAttribute("mensaje", "El pedido fue realizado correctamente.");
        }

        return "redirect:/carrito";
    }

    @PostMapping("/carrito/agregar")
    public String agregarAlCarrito(HttpSession session,
            @RequestParam("idProducto") Integer idProducto,
            @RequestParam("idTalla") Integer idTalla,
            @RequestParam("cantidad") Integer cantidad,
            @RequestParam("tipoEntrega") String tipoEntrega,
            RedirectAttributes redirectAttributes) {

        Usuario usuarioSesion = (Usuario) session.getAttribute("usuarioSesion");

        if (!puedeUsarCarrito(usuarioSesion)) {
            redirectAttributes.addFlashAttribute("error",
                    "Debes iniciar sesión para agregar productos al carrito.");
            return "redirect:/login";
        }

        String resultado = carritoService.agregarProductoAlCarrito(
                usuarioSesion.getIdUsuario(),
                idProducto,
                idTalla,
                cantidad,
                tipoEntrega
        );

        if (!"ok".equals(resultado)) {
            redirectAttributes.addFlashAttribute("error", resultado);

            return "redirect:/producto/detalle/" + idProducto;
        }

        redirectAttributes.addFlashAttribute("mensaje",
                "Producto agregado al carrito correctamente.");

        return "redirect:/carrito";
    }

}
