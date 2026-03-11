package LukSportPrueba.controller;

import LukSportPrueba.domain.Categoria;
import LukSportPrueba.domain.Usuario;
import LukSportPrueba.service.CategoriaService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/categorias")
public class CategoriaController {

    @Autowired
    private CategoriaService categoriaService;

    private boolean esAdmin(Usuario usuario) {
        return usuario != null
                && usuario.getRol() != null
                && usuario.getRol().getIdRol() == 2;
    }

    @GetMapping
    public String listarCategorias(HttpSession session, Model model) {
        Usuario usuarioSesion = (Usuario) session.getAttribute("usuarioSesion");

        if (!esAdmin(usuarioSesion)) {
            return "redirect:/";
        }

        model.addAttribute("seccion", "categorias");
        model.addAttribute("categorias", categoriaService.listarCategorias());
        model.addAttribute("categoria", new Categoria());

        return "admin/dashboard";
    }

    @PostMapping("/guardar")
    public String guardarCategoria(Categoria categoria,
            MultipartFile imagenFile,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        Usuario usuarioSesion = (Usuario) session.getAttribute("usuarioSesion");

        if (!esAdmin(usuarioSesion)) {
            return "redirect:/";
        }

        Categoria categoriaGuardada = categoriaService.guardarCategoria(categoria, imagenFile);

        if (categoriaGuardada == null) {
            redirectAttributes.addFlashAttribute("toastError", "No se pudo guardar la categoría");
        } else {
            redirectAttributes.addFlashAttribute("toastSuccess", "Categoría guardada correctamente");
        }

        return "redirect:/admin/categorias";
    }

    @GetMapping("/editar/{id}")
    public String editarCategoria(@PathVariable("id") Integer idCategoria,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes) {

        Usuario usuarioSesion = (Usuario) session.getAttribute("usuarioSesion");

        if (!esAdmin(usuarioSesion)) {
            return "redirect:/";
        }

        Categoria categoria = categoriaService.getCategoriaPorId(idCategoria);

        if (categoria == null) {
            redirectAttributes.addFlashAttribute("toastError", "La categoría no existe");
            return "redirect:/admin/categorias";
        }

        model.addAttribute("seccion", "categorias");
        model.addAttribute("categorias", categoriaService.listarCategorias());
        model.addAttribute("categoria", categoria);
        model.addAttribute("modoEdicion", true);

        return "admin/dashboard";
    }

    @PostMapping("/actualizar/{id}")
    public String actualizarCategoria(@PathVariable("id") Integer idCategoria,
            Categoria categoria,
            MultipartFile imagenFile,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        Usuario usuarioSesion = (Usuario) session.getAttribute("usuarioSesion");

        if (!esAdmin(usuarioSesion)) {
            return "redirect:/";
        }

        Categoria categoriaActualizada = categoriaService.actualizarCategoria(idCategoria, categoria, imagenFile);

        if (categoriaActualizada == null) {
            redirectAttributes.addFlashAttribute("toastError", "No se pudo actualizar la categoría");
        } else {
            redirectAttributes.addFlashAttribute("toastSuccess", "Categoría actualizada correctamente");
        }

        return "redirect:/admin/categorias";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarCategoria(@PathVariable("id") Integer idCategoria,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        Usuario usuarioSesion = (Usuario) session.getAttribute("usuarioSesion");

        if (!esAdmin(usuarioSesion)) {
            return "redirect:/";
        }

        Categoria categoria = categoriaService.getCategoriaPorId(idCategoria);

        if (categoria == null) {
            redirectAttributes.addFlashAttribute("toastError", "La categoría no existe");
            return "redirect:/admin/categorias";
        }

        boolean eliminada = categoriaService.eliminarCategoria(idCategoria);

        if (!eliminada) {
            redirectAttributes.addFlashAttribute("toastError",
                    "No se puede eliminar la categoría porque tiene productos asociados o ocurrió un error");
        } else {
            redirectAttributes.addFlashAttribute("toastSuccess", "Categoría eliminada correctamente");
        }

        return "redirect:/admin/categorias";
    }
}