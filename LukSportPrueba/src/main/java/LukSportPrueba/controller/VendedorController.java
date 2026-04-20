package LukSportPrueba.controller;

import LukSportPrueba.domain.Producto;
import LukSportPrueba.domain.Usuario;
import LukSportPrueba.repository.TallaProductoRepository;
import LukSportPrueba.service.ProductoService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/vendedor")
public class VendedorController {

    @Autowired
    private ProductoService productoService;

    @Autowired
    private TallaProductoRepository tallaProductoRepository;

    private boolean esVendedor(Usuario usuario) {
        return usuario != null
                && usuario.getRol() != null
                && (usuario.getRol().getIdRol() == 2
                || usuario.getRol().getIdRol() == 3);
    }

    @GetMapping
    public String dashboardVendedor(HttpSession session, Model model) {
        Usuario usuarioSesion = (Usuario) session.getAttribute("usuarioSesion");

        if (!esVendedor(usuarioSesion)) {
            return "redirect:/";
        }

        model.addAttribute("productos", productoService.listarProductos());
        model.addAttribute("seccion", "inicio");
        return "vendedor/dashboard";
    }

    @GetMapping("/productos")
    public String vendedorProductos(HttpSession session, Model model) {
        Usuario usuarioSesion = (Usuario) session.getAttribute("usuarioSesion");

        if (!esVendedor(usuarioSesion)) {
            return "redirect:/";
        }

        model.addAttribute("productos", productoService.listarProductos());
        model.addAttribute("seccion", "productos");
        return "vendedor/dashboard";
    }

    @GetMapping("/productos/{idProducto}/inventario")
    public String gestionarInventario(HttpSession session,
            @PathVariable("idProducto") Integer idProducto,
            Model model) {
        Usuario usuarioSesion = (Usuario) session.getAttribute("usuarioSesion");

        if (!esVendedor(usuarioSesion)) {
            return "redirect:/";
        }

        Producto producto = productoService.obtenerProductoPorId(idProducto);

        model.addAttribute("producto", producto);
        model.addAttribute("tallas", tallaProductoRepository.findAll());
        model.addAttribute("seccion", "inventarioProducto");
        return "vendedor/dashboard";
    }

    @PostMapping("/productos/agregarExistencia")
    public String agregarExistencia(HttpSession session,
            @RequestParam("idProducto") Integer idProducto,
            @RequestParam("idTalla") Integer idTalla,
            @RequestParam("cantidad") Integer cantidad,
            Model model) {

        Usuario usuarioSesion = (Usuario) session.getAttribute("usuarioSesion");

        if (!esVendedor(usuarioSesion)) {
            return "redirect:/";
        }

        String resultado = productoService.agregarExistenciaProductoTalla(idProducto, idTalla, cantidad);

        model.addAttribute("producto", productoService.obtenerProductoPorId(idProducto));
        model.addAttribute("tallas", tallaProductoRepository.findAll());
        model.addAttribute("seccion", "inventarioProducto");

        if (!"ok".equals(resultado)) {
            model.addAttribute("errorInventario", resultado);
        } else {
            model.addAttribute("mensajeExito", "Existencia agregada correctamente");
        }

        return "vendedor/dashboard";
    }

    @PostMapping("/productos/quitarExistencia")
    public String quitarExistencia(HttpSession session,
            @RequestParam("idProducto") Integer idProducto,
            @RequestParam("idTalla") Integer idTalla,
            @RequestParam("cantidad") Integer cantidad,
            Model model) {

        Usuario usuarioSesion = (Usuario) session.getAttribute("usuarioSesion");

        if (!esVendedor(usuarioSesion)) {
            return "redirect:/";
        }

        String resultado = productoService.quitarExistenciaProductoTalla(idProducto, idTalla, cantidad);

        model.addAttribute("producto", productoService.obtenerProductoPorId(idProducto));
        model.addAttribute("tallas", tallaProductoRepository.findAll());
        model.addAttribute("seccion", "inventarioProducto");

        if (!"ok".equals(resultado)) {
            model.addAttribute("errorInventario", resultado);
        } else {
            model.addAttribute("mensajeExito", "Existencia descontada correctamente");
        }

        return "vendedor/dashboard";
    }
}
