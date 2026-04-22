/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package LukSportPrueba.controller;

import LukSportPrueba.domain.Producto;
import LukSportPrueba.domain.Usuario;
import LukSportPrueba.repository.TallaProductoRepository;
import LukSportPrueba.service.CategoriaService;
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
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private ProductoService productoService;

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
            return "redirect:/";
        }

        model.addAttribute("seccion", "inicio");
        return "admin/dashboard";
    }

    @GetMapping("/productos")
    public String adminProductos(HttpSession session, Model model) {
        Usuario usuarioSesion = (Usuario) session.getAttribute("usuarioSesion");

        if (!esAdmin(usuarioSesion)) {
            return "redirect:/";
        }

        model.addAttribute("productos", productoService.listarProductos());
        model.addAttribute("seccion", "productos");
        return "admin/dashboard";
    }

    @GetMapping("/transacciones")
    public String adminTransacciones(HttpSession session, Model model) {
        Usuario usuarioSesion = (Usuario) session.getAttribute("usuarioSesion");

        if (!esAdmin(usuarioSesion)) {
            return "redirect:/";
        }

        model.addAttribute("seccion", "transacciones");
        return "admin/dashboard";
    }

    @GetMapping("/productos/listado")
    public String listarProductos(HttpSession session, Model model) {
        Usuario usuarioSesion = (Usuario) session.getAttribute("usuarioSesion");

        if (!esAdmin(usuarioSesion)) {
            return "redirect:/";
        }

        model.addAttribute("productos", productoService.listarProductos());
        model.addAttribute("seccion", "productos");

        return "admin/dashboard";
    }

    @GetMapping("/productos/nuevo")
    public String nuevoProducto(HttpSession session, Model model) {
        Usuario usuarioSesion = (Usuario) session.getAttribute("usuarioSesion");

        if (!esAdmin(usuarioSesion)) {
            return "redirect:/";
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
            return "redirect:/";
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
            return "redirect:/";
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
            return "redirect:/";
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
            return "redirect:/";
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

        return "redirect:/admin/productos/listado";
    }

} //ssss
