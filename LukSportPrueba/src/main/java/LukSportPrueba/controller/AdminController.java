/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package LukSportPrueba.controller;

import LukSportPrueba.service.UsuarioService;
import LukSportPrueba.domain.Producto;
import LukSportPrueba.domain.Usuario;
import LukSportPrueba.repository.TallaProductoRepository;
import LukSportPrueba.service.CategoriaService;
import LukSportPrueba.service.ProductoService;
import LukSportPrueba.service.CarritoService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private ProductoService productoService;

    @Autowired
    private CarritoService carritoService;

    @Autowired
    private CategoriaService categoriaService;

    @Autowired
    private TallaProductoRepository tallaProductoRepository;

    private boolean esAdmin(Usuario usuario) {
        return usuario != null
                && usuario.getRol() != null
                && usuario.getRol().getIdRol() == 2;
    }

    @GetMapping
    public String dashboardAdmin(HttpSession session, Model model) {
        Usuario usuarioSesion = (Usuario) session.getAttribute("usuarioSesion");

        if (!esAdmin(usuarioSesion)) {
            return "redirect:/sin-acceso";
        }

        model.addAttribute("seccion", "inicio");
        return "admin/dashboard";
    }

    @GetMapping("/productos")
    public String adminProductos(HttpSession session, Model model) {
        Usuario usuarioSesion = (Usuario) session.getAttribute("usuarioSesion");

        if (!esAdmin(usuarioSesion)) {
            return "redirect:/sin-acceso";
        }

        model.addAttribute("productos", productoService.listarProductos());
        model.addAttribute("seccion", "productos");
        return "admin/dashboard";
    }

    @GetMapping("/transacciones")
    public String adminTransacciones(HttpSession session, Model model) {

        Usuario usuarioSesion = (Usuario) session.getAttribute("usuarioSesion");

        if (!esAdmin(usuarioSesion)) {
            return "redirect:/sin-acceso";
        }

        model.addAttribute("seccion", "transacciones");
        model.addAttribute("transacciones",
                carritoService.obtenerTodasLasTransacciones());

        return "admin/dashboard";
    }

    @GetMapping("/productos/listado")
    public String listarProductos(HttpSession session, Model model) {
        Usuario usuarioSesion = (Usuario) session.getAttribute("usuarioSesion");

        if (!esAdmin(usuarioSesion)) {
            return "redirect:/sin-acceso";
        }

        model.addAttribute("productos", productoService.listarProductos());
        model.addAttribute("seccion", "productos");

        return "admin/dashboard";
    }

    @GetMapping("/productos/nuevo")
    public String nuevoProducto(HttpSession session, Model model) {
        Usuario usuarioSesion = (Usuario) session.getAttribute("usuarioSesion");

        if (!esAdmin(usuarioSesion)) {
            return "redirect:/sin-acceso";
        }

        model.addAttribute("producto", new Producto());
        model.addAttribute("categorias", categoriaService.listarCategorias());
        model.addAttribute("seccion", "nuevoProducto");

        return "admin/dashboard";
    }

    @PostMapping("/productos/guardar")
    public String guardarProducto(HttpSession session,
            Producto producto,
            @RequestParam("imagenFile") MultipartFile imagenFile,
            @RequestParam("idCategoria") Integer idCategoria) {

        Usuario usuarioSesion = (Usuario) session.getAttribute("usuarioSesion");

        if (!esAdmin(usuarioSesion)) {
            return "redirect:/sin-acceso";
        }
        producto.setCantidadExistencia(0);
        productoService.guardarProducto(producto, imagenFile, idCategoria);

        return "redirect:/admin/productos/listado";
    }

    @GetMapping("/productos/editar/{idProducto}")
    public String editarProducto(HttpSession session,
            @PathVariable("idProducto") Integer idProducto,
            Model model) {

        Usuario usuarioSesion = (Usuario) session.getAttribute("usuarioSesion");

        if (!esAdmin(usuarioSesion)) {
            return "redirect:/sin-acceso";
        }

        Producto producto = productoService.obtenerProductoPorId(idProducto);

        model.addAttribute("producto", producto);
        model.addAttribute("seccion", "editarProducto");

        return "admin/dashboard";
    }

    @PostMapping("/productos/editar/{idProducto}")
    public String actualizarProducto(HttpSession session,
            @PathVariable("idProducto") Integer idProducto,
            @RequestParam("nombre") String nombre,
            @RequestParam("descripcion") String descripcion,
            @RequestParam("actividad") String actividad,
            @RequestParam("imagenFile") MultipartFile imagenFile) {

        Usuario usuarioSesion = (Usuario) session.getAttribute("usuarioSesion");

        if (!esAdmin(usuarioSesion)) {
            return "redirect:/";
        }

        productoService.editarProducto(idProducto, nombre, descripcion, actividad, imagenFile);

        return "redirect:/admin/productos";
    }

    @GetMapping("/productos/eliminar/{idProducto}")
    public String eliminarProducto(HttpSession session,
            @PathVariable("idProducto") Integer idProducto) {

        Usuario usuarioSesion = (Usuario) session.getAttribute("usuarioSesion");

        if (!esAdmin(usuarioSesion)) {
            return "redirect:/sin-acceso";
        }

        productoService.eliminarProducto(idProducto);

        return "redirect:/admin/productos/listado";
    }

    @GetMapping("/productos/{idProducto}/inventario")
    public String gestionarInventario(HttpSession session,
            @PathVariable("idProducto") Integer idProducto,
            Model model) {

        Usuario usuarioSesion = (Usuario) session.getAttribute("usuarioSesion");

        if (!esAdmin(usuarioSesion)) {
            return "redirect:/sin-acceso";
        }

        Producto producto = productoService.obtenerProductoPorId(idProducto);

        model.addAttribute("producto", producto);
        model.addAttribute("tallas", tallaProductoRepository.findAll());
        model.addAttribute("seccion", "inventarioProducto");

        return "admin/dashboard";
    }

    @PostMapping("/productos/agregarExistencia")
    public String agregarExistencia(HttpSession session,
            @RequestParam("idProducto") Integer idProducto,
            @RequestParam("idTalla") Integer idTalla,
            @RequestParam("cantidad") Integer cantidad,
            Model model) {

        Usuario usuarioSesion = (Usuario) session.getAttribute("usuarioSesion");

        if (!esAdmin(usuarioSesion)) {
            return "redirect:/sin-acceso";
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

        return "redirect:/admin/productos/listado";
    }

    @GetMapping("/usuarios")
    public String adminUsuarios(HttpSession session, Model model) {
        Usuario usuarioSesion = (Usuario) session.getAttribute("usuarioSesion");

        if (!esAdmin(usuarioSesion)) {
            return "redirect:/sin-acceso";
        }

        model.addAttribute("seccion", "usuarios");
        model.addAttribute("roles", usuarioService.listarRoles());

        return "admin/dashboard";
    }

    @GetMapping("/usuarios/buscar")
    public String buscarUsuarios(HttpSession session,
            @RequestParam("criterio") String criterio,
            Model model) {

        Usuario usuarioSesion = (Usuario) session.getAttribute("usuarioSesion");

        if (!esAdmin(usuarioSesion)) {
            return "redirect:/sin-acceso";
        }

        model.addAttribute("seccion", "usuarios");
        model.addAttribute("criterio", criterio);
        model.addAttribute("usuariosEncontrados", usuarioService.buscarUsuariosPorNombreOCorreo(criterio));
        model.addAttribute("roles", usuarioService.listarRoles());

        return "admin/dashboard";
    }

    @PostMapping("/usuarios/actualizarRol")
    public String actualizarRolUsuario(HttpSession session,
            @RequestParam("idUsuario") Integer idUsuario,
            @RequestParam("idRol") Integer idRol,
            @RequestParam(value = "criterio", required = false) String criterio,
            RedirectAttributes redirectAttributes) {

        Usuario usuarioSesion = (Usuario) session.getAttribute("usuarioSesion");

        if (!esAdmin(usuarioSesion)) {
            return "redirect:/sin-acceso";
        }

        String resultado = usuarioService.actualizarRolUsuario(idUsuario, idRol);

        if (!"ok".equals(resultado)) {
            redirectAttributes.addFlashAttribute("errorUsuarios", resultado);
        } else {
            redirectAttributes.addFlashAttribute("mensajeUsuarios", "Rol actualizado correctamente.");
        }

        if (criterio != null && !criterio.isBlank()) {
            return "redirect:/admin/usuarios/buscar?criterio=" + criterio;
        }

        return "redirect:/admin/usuarios";
    }

}
