package LukSportPrueba.controller;

import LukSportPrueba.service.UsuarioService;
import LukSportPrueba.domain.Pedido;
import LukSportPrueba.domain.Usuario;
import LukSportPrueba.service.CarritoService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/carrito")
public class CarritoController {

    @Autowired
    private CarritoService carritoService;
    
    @Autowired
    private UsuarioService usuarioService;

    private boolean puedeUsarCarrito(Usuario usuario) {
        return usuario != null
                && usuario.getRol() != null
                && (usuario.getRol().getIdRol() == 1
                || usuario.getRol().getIdRol() == 2
                || usuario.getRol().getIdRol() == 3);
    }

    @GetMapping("")
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

    @PostMapping("/agregar")
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

    @PostMapping("/eliminar/{idItem}")
    public String eliminarItem(HttpSession session,
            @PathVariable Integer idItem,
            RedirectAttributes redirectAttributes) {

        Usuario usuarioSesion = (Usuario) session.getAttribute("usuarioSesion");

        if (!puedeUsarCarrito(usuarioSesion)) {
            return "redirect:/login";
        }

        String resultado = carritoService.eliminarItemCarrito(usuarioSesion.getIdUsuario(), idItem);

        if (!"ok".equals(resultado)) {
            redirectAttributes.addFlashAttribute("error", resultado);
        } else {
            redirectAttributes.addFlashAttribute("mensaje", "Producto eliminado del carrito.");
        }

        return "redirect:/carrito";
    }

    @PostMapping("/actualizar/{idItem}")
    public String actualizarCantidad(HttpSession session,
            @PathVariable Integer idItem,
            @RequestParam("cantidad") Integer cantidad,
            RedirectAttributes redirectAttributes) {

        Usuario usuarioSesion = (Usuario) session.getAttribute("usuarioSesion");

        if (!puedeUsarCarrito(usuarioSesion)) {
            return "redirect:/login";
        }

        String resultado = carritoService.actualizarCantidadItem(
                usuarioSesion.getIdUsuario(), idItem, cantidad
        );

        if (!"ok".equals(resultado)) {
            redirectAttributes.addFlashAttribute("error", resultado);
        } else {
            redirectAttributes.addFlashAttribute("mensaje", "Cantidad actualizada correctamente.");
        }

        return "redirect:/carrito";
    }

    @PostMapping("/cancelar")
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

    @PostMapping("/pagar")
    public String pagarPedido(HttpSession session,
            @RequestParam("nombreTarjeta") String nombreTarjeta,
            @RequestParam("numeroTarjeta") String numeroTarjeta,
            @RequestParam("mesExpiracion") String mesExpiracion,
            @RequestParam("anioExpiracion") String anioExpiracion,
            @RequestParam("cvv") String cvv,
            RedirectAttributes redirectAttributes) {

        Usuario usuarioSesion = (Usuario) session.getAttribute("usuarioSesion");

        if (!puedeUsarCarrito(usuarioSesion)) {
            return "redirect:/login";
        }

        // Validar datos de tarjeta
        String validacionPago = carritoService.validarDatosPago(
                nombreTarjeta,
                numeroTarjeta,
                mesExpiracion,
                anioExpiracion,
                cvv
        );

        if (!"ok".equals(validacionPago)) {
            redirectAttributes.addFlashAttribute("error", validacionPago);
            return "redirect:/carrito/pago";
        }

        // Obtener carrito actual
        Pedido carrito = carritoService.obtenerCarritoActivo(usuarioSesion.getIdUsuario());

        if (carrito == null) {
            redirectAttributes.addFlashAttribute("error",
                    "No tienes un carrito activo.");
            return "redirect:/carrito";
        }

        Integer idPedido = carrito.getIdPedido();

        // Procesar pago
        String resultado = carritoService.pagarPedido(usuarioSesion.getIdUsuario(), usuarioSesion);

        if (!"ok".equals(resultado)) {
            redirectAttributes.addFlashAttribute("error", resultado);
            return "redirect:/carrito/pago";
        }

        redirectAttributes.addFlashAttribute("mensaje",
                "El pago se procesó correctamente. Se envió un comprobante a tu correo.");

        return "redirect:/carrito/comprobante/" + idPedido;
    }

    @GetMapping("/historial")
    public String historial(HttpSession session, Model model) {
        Usuario usuarioSesion = (Usuario) session.getAttribute("usuarioSesion");

        if (!puedeUsarCarrito(usuarioSesion)) {
            return "redirect:/login";
        }

        model.addAttribute("pedidos", carritoService.obtenerHistorialPedidos(usuarioSesion.getIdUsuario()));
        return "carrito/historial";
    }

    @GetMapping("/comprobante/{idPedido}")
    public String comprobante(HttpSession session,
            @PathVariable Integer idPedido,
            Model model) {

        Usuario usuarioSesion = (Usuario) session.getAttribute("usuarioSesion");

        if (!puedeUsarCarrito(usuarioSesion)) {
            return "redirect:/login";
        }

        Pedido pedido = carritoService.obtenerPedidoUsuario(usuarioSesion.getIdUsuario(), idPedido);

        if (pedido == null) {
            return "redirect:/carrito/historial";
        }

        model.addAttribute("pedido", pedido);
        model.addAttribute("itemsPedido", carritoService.obtenerItemsPedido(idPedido));

        return "carrito/comprobante";
    }

    @GetMapping("/pago")
    public String vistaPago(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        Usuario usuarioSesion = (Usuario) session.getAttribute("usuarioSesion");

        if (!puedeUsarCarrito(usuarioSesion)) {
            return "redirect:/login";
        }

        Pedido carrito = carritoService.obtenerCarritoActivo(usuarioSesion.getIdUsuario());

        if (carrito == null) {
            redirectAttributes.addFlashAttribute("error", "No tienes un carrito activo.");
            return "redirect:/carrito";
        }

        model.addAttribute("carrito", carrito);
        model.addAttribute("itemsCarrito", carritoService.obtenerItemsCarrito(usuarioSesion.getIdUsuario()));

        return "carrito/pago";
    }

}
